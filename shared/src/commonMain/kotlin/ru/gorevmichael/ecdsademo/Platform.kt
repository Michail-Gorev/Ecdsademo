package ru.gorevmichael.ecdsademo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform