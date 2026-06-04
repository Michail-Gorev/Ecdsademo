package ru.gorevmichael.sign_v2.data.custom

import ru.gorevmichael.core.interfaces.DummyInterface

class DummyInterfaceImpl(
    private val os: String
): DummyInterface {
    override fun dummyMethod(): String {
        return "DummyInterfaceImpl from sign_v2 (OS: $os)"
    }
}