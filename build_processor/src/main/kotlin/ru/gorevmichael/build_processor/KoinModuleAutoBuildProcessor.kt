package ru.gorevmichael.build_processor

import com.google.devtools.ksp.KspExperimental
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
import ru.gorevmichael.annotations.KoinModuleAutoBuild
import ru.gorevmichael.annotations.AutoBuildReference
import java.io.IOException

class KoinModuleAutoBuildProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger
    private var totalSymbolsForBuildCount: Int = 0
    private var totalSymbolsReferencesCount: Int = 0
    private var errorsCount: Int = 0

    // Получаем значение "переменной" окружения, которое установили в build.gradle
    // TODO переименовать из features в modules (?), т.к. дальше сравниваем мы имя модуля
    private val includedFeatures = environment.options["includedFeatures"]?.split(",")

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("=====Starting KoinModuleAutoBuildProcessor=====")
        if (includedFeatures.isNullOrEmpty()) {
            logger.info("List of included features is empty! Check gradle configuration!")
            return emptyList()
        } else {
            includedFeatures.forEach {
                logger.info("=====Got a feature: $it=====")
                //TODO разобраться, как пихать в resolver-а объявления из других пакетов
                resolver.getDeclarationsFromPackage("ru.gorevmichael.$it").forEach { decl ->
                    logger.info("=====Found declaration: ${decl.simpleName.getShortName()}")
                }
            }
        }
        val symbolsForBuild: Sequence<KSAnnotated> =
            resolver.getSymbolsWithAnnotation(KoinModuleAutoBuild::class.qualifiedName!!)
        val symbolsReferences: Sequence<KSAnnotated> =
            resolver.getSymbolsWithAnnotation(AutoBuildReference::class.qualifiedName!!)
        // Проверка на то, что модуль, в котором найдена аннотация ссылки, включен в проект
        logger.info("=====Starting processing references=====")
        symbolsReferences.forEach {
            logger.info("=====${it.origin.name}")
        }
        logger.info("=====Finalizing processing references=====")
        for (symbol in symbolsForBuild) {
            try {
                if (symbol is KSClassDeclaration && symbol.classKind == ClassKind.CLASS) {
                    logger.info("=====Processing symbol for build: ${symbol.simpleName.getShortName()}")
                    generateBuilderFor(symbol)
                    totalSymbolsForBuildCount++
                    logger.info("=====Finalizing with symbol: ${symbol.simpleName.getShortName()}")
                } else {
                    logger.info("=====Unsupported symbol: $symbol")
                }
            } catch (e: Exception) {
                logger.exception(e)
                errorsCount++
            }
        }
        for (symbol in symbolsReferences) {
            try {
                if (symbol is KSClassDeclaration && symbol.classKind == ClassKind.CLASS) {
                    logger.info("=====Processing symbol for build: ${symbol.simpleName.getShortName()}")
                    generateReference(symbol)
                    totalSymbolsForBuildCount++
                    logger.info("=====Finalizing with symbol: ${symbol.simpleName.getShortName()}")
                } else {
                    logger.info("=====Unsupported symbol: $symbol")
                }
            } catch (e: Exception) {
                logger.exception(e)
                errorsCount++
            }
        }
        return emptyList()
    }

    override fun finish() {
        logger.info("=====Finalizing KoinModuleAutoBuildProcessor=====")
        logger.info("=====Stats:")
        logger.info(
            """
            Total symbols referenced: $totalSymbolsReferencesCount
            Total symbols built: $totalSymbolsForBuildCount
            Total errors count: $errorsCount
        """.trimIndent()
        )
        logger.info("=====End of KoinModuleAutoBuildProcessor=====:")
    }

    private fun generateBuilderFor(classDecl: KSClassDeclaration) {
        val className = classDecl.simpleName.asString()
        //TODO заменить на подставляемое значение (?)
        val packageName = classDecl.packageName.asString()
        val builderClassName = "${className}Builder"

        // Используем KotlinPoet для генерации класса
        val fileSpecBuilder = FileSpec.builder(packageName, builderClassName)

        // Создаём класс Builder
        val classBuilder = TypeSpec.classBuilder(builderClassName)

        // Финализируем файл
        fileSpecBuilder.addType(classBuilder.build())
        val customDependencies = if (includedFeatures?.contains("sign_v2") == true) {
            listOf(
                "package ru.gorevmichael.build_outputs.di\n",
                "import ru.gorevmichael.sign_v2.di.KoinSign\n",
                "import kotlin.collections.List\n",
                "import org.koin.core.module.Module\n",
                "\n",
                "\n",
                "public class FeaturesDIBuilder {\n",
                "  val featureModules: Module = KoinSign().featureModule\n",
                "\n",
                "  operator fun invoke(): FeaturesDI = FeaturesDI(featureModules ?: throw IllegalStateException(\"featureModules must be provided!\"))\n",
                "}"
            )
        } else if (includedFeatures?.contains("sign_v1") == true) {
            listOf(
                "package ru.gorevmichael.build_outputs.di\n",
                "import ru.gorevmichael.sign_v1.di.KoinSign\n",
                "import kotlin.collections.List\n",
                "import org.koin.core.module.Module\n",
                "\n",
                "\n",
                "public class FeaturesDIBuilder {\n",
                "  val featureModules: Module = KoinSign().featureModule\n",
                "\n",
                "  operator fun invoke(): FeaturesDI = FeaturesDI(featureModules ?: throw IllegalStateException(\"featureModules must be provided!\"))\n",
                "}"
            )
        } else {
            listOf("")
        }

        // Пишем файл через KSP
        try {
            val file = codeGenerator.createNewFile(
                Dependencies(true, *classDecl.containingFile?.let { arrayOf(it) } ?: emptyArray()),
                packageName,
                builderClassName
            )
            file.bufferedWriter().use { writer ->
                customDependencies.forEach {
                    writer.append(it)
                }
            }
        } catch (e: IOException) {
            logger.error("Error generating reference for $className: ${e.message}")

        }
    }

    private fun generateReference(classDecl: KSClassDeclaration) {
        val className = classDecl.simpleName.asString()
        //TODO заменить на подставляемое значение (?)
        val packageName = classDecl.packageName.asString()
        val builderClassName = "${className}Builder"

        // Используем KotlinPoet для генерации класса
        val fileSpecBuilder = FileSpec.builder(packageName, builderClassName)

        // Создаём класс Builder
        val classBuilder = TypeSpec.classBuilder(builderClassName)

        // Финализируем файл
        fileSpecBuilder.addType(classBuilder.build())
        val customDependencies = if (includedFeatures?.contains("sign_v2") == true && packageName.contains("sign_v2")) {
            listOf(
                "package ru.gorevmichael.sign_v2\n",
                "import ru.gorevmichael.sign_v2.di.KoinSign",
                "\n",
                "\n",
                "fun test(): String {\n",
                "    return \"test_v2\"\n",
                "}"
            )
        } else if (includedFeatures?.contains("sign_v1") == true && packageName.contains("sign_v1")) {
            listOf(
                "package ru.gorevmichael.sign_v1\n",
                "import ru.gorevmichael.sign_v1.di.KoinSign",
                "\n",
                "\n",
                "fun test(): String {\n",
                "    return \"test_v1\"\n",
                "}"
            )
        } else {
            listOf("")
        }

        // Пишем файл через KSP
        try {
            val file = codeGenerator.createNewFile(
                Dependencies(true, *classDecl.containingFile?.let { arrayOf(it) } ?: emptyArray()),
                packageName,
                builderClassName
            )
            file.bufferedWriter().use { writer ->
                customDependencies.forEach {
                    writer.append(it)
                }
            }
        } catch (e: IOException) {
            logger.error("Error generating reference for $className: ${e.message}")

        }
    }
}