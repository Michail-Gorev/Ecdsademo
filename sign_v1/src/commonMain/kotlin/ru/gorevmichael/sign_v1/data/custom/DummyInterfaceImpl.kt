package ru.gorevmichael.sign_v1.data.custom

import ru.gorevmichael.core.interfaces.DummyInterface

class DummyInterfaceImpl(
    private val os: String,
    private val customProperty: Int
): DummyInterface {
    override fun dummyMethod(): String {
        return "DummyInterfaceImpl from  sign_v1 (OS: $os; CustomProperty: $customProperty)"
    }
}