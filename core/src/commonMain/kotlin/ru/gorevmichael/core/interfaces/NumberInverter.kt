package ru.gorevmichael.core.interfaces

import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Интерфейс для объектов, отвечающих за нахождение обратного числа
 * (обратное по модулю и т.д.)
 */
//TODO подумать, как обобщить, не привязываясь к Int и тем более к KBigInteger
interface NumberInverter {

    /**
     * Возвращает число, обратное переданному
     * @param value [BigInteger] - число, обратное для которого необходимо найти
     * @return [BigInteger] - число, обратное переданному
     */
    fun inverse(value: BigInteger): BigInteger

    /**
     * Проверяет, являются ли два числа обратными
     * @param n1 [BigInteger] - первое число
     * @param n2 [BigInteger] - второе число
     * @return [Boolean] - true, если числа являются обратными, false - иначе
     */
    fun checkAreInverse(n1: BigInteger, n2: BigInteger): Boolean
}