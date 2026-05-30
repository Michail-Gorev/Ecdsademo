package ru.gorevmichael.files.data

data class File(
    val path: String,
    val type: Type,
    val content: String
) {
    enum class Type {
        JSON,
        TEXT
    }
}