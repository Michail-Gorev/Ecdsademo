package ru.gorevmichael.build_processor

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import ru.gorevmichael.annotations.AutoBuild
import ru.gorevmichael.annotations.AutoBuildReference
import java.io.IOException

class AutoBuildProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger
    private var totalSymbolsForBuildCount: Int = 0
    private var totalSymbolsReferencesCount: Int = 0
    private var errorsCount: Int = 0

    // Получаем значение "переменной" окружения, которое установили в build.gradle
    // TODO переименовать из features в modules (?), т.к. дальше сравниваем мы имя модуля
    private val includedFeatures = environment.options["includedFeatures"]?.split(",")

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("=====Starting AutoBuildProcessor=====")
        if (includedFeatures.isNullOrEmpty()) {
            logger.info("List of included features is empty! Check gradle configuration!")
            return emptyList()
        }
        val symbolsForBuild: Sequence<KSAnnotated> =
            resolver.getSymbolsWithAnnotation(AutoBuild::class.qualifiedName!!)
        val symbolsReferences: Sequence<KSAnnotated> =
            resolver.getSymbolsWithAnnotation(AutoBuildReference::class.qualifiedName!!)
        // Проверка на то, что модуль, в котором найдена аннотация ссылки, включен в проект
        logger.info("=====Starting processing references=====")
        symbolsReferences.forEach {
            logger.info("=====${it.javaClass}")
        }
        logger.info("=====Finalizing processing references=====")
        for (symbol in symbolsForBuild) {
            logger.info("=====Processing symbol for build: ${symbol.javaClass.simpleName}")
            try {
                if (symbol is KSClassDeclaration && symbol.classKind == ClassKind.CLASS) {
                    generateBuilderFor(symbol)
                }
                totalSymbolsForBuildCount++
            } catch (e: Exception) {
                logger.exception(e)
                errorsCount++
            }
            logger.info("=====Finalizing with symbol: ${symbol.javaClass.simpleName}")
        }
        return emptyList()
    }

    override fun finish() {
        logger.info("=====Finalizing AutoBuildProcessor=====")
        logger.info("=====Stats:")
        logger.info(
            """
            Total symbols referenced: $totalSymbolsReferencesCount
            Total symbols built: $totalSymbolsForBuildCount
            Total errors count: $errorsCount
        """.trimIndent()
        )
        logger.info("=====End of AutoBuildProcessor=====:")
    }

    private fun generateBuilderFor(classDecl: KSClassDeclaration) {
        val className = classDecl.simpleName.asString()
        //TODO заменить на подставляемое значение
        val packageName = classDecl.packageName.asString()
        val builderClassName = "${className}Builder"

        // Используем KotlinPoet для генерации класса
        val fileSpecBuilder = FileSpec.builder(packageName, builderClassName)

        // Создаём класс Builder
        val classBuilder = TypeSpec.classBuilder(builderClassName)

        // Добавим поля в билдер на основе свойств оригинального класса
        for (property in classDecl.getDeclaredProperties()) {
            val propName = property.simpleName.asString()
            val typeName =
                property.type.toTypeName()  // KotlinPoet extension, понадобится kotlinpoet-ksp
            // В билдере поля делаем мутабельными var
            classBuilder.addProperty(
                PropertySpec.builder(propName, typeName.copy(nullable = true))
                    .mutable(false)
                    .initializer("null")
                    .build()
            )
        }

        // Добавляем метод invoke()
        // Он должен вернуть модули, которые передали в качестве параметра в вызов конструктора класса
        val constructorCall = buildString {
            append("$className(")
            val props = classDecl.getDeclaredProperties().map { it.simpleName.asString() }
            append(props.joinToString(", ") { prop ->
                "$prop ?: throw IllegalStateException(\"$prop must be provided!\")"
            })
            append(")")
        }
        classBuilder.addFunction(
            FunSpec.builder("invoke")
                .returns(classDecl.toClassName())  // вернёт TypeName оригинального класса
                .addStatement("return $constructorCall")
                .build()
        )

        // Финализируем файл
        fileSpecBuilder.addType(classBuilder.build())
        val fileSpec = fileSpecBuilder.build()

        // Пишем файл через KSP
        try {
            val file = codeGenerator.createNewFile(
                Dependencies(false, *classDecl.containingFile?.let { arrayOf(it) } ?: emptyArray()),
                packageName,
                builderClassName
            )
            file.bufferedWriter().use { writer -> fileSpec.writeTo(writer) }
        } catch (e: IOException) {
            logger.error("Error generating builder for $className: ${e.message}")
        }
    }
}