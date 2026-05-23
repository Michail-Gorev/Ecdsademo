package ru.gorevmichael.core.interfaces

import io.github.gatrongdev.kbignum.math.KBigInteger

/**
 * Интерфейс для объектов, отвечающих за нахождение обратного числа
 * (обратное по модулю и т.д.)
 */
interface NumberInverter {

    /**
     * Возвращает число, обратное переданному
     * @param value [KBigInteger] - число, обратное для которого необходимо найти
     * @return [KBigInteger] - число, обратное переданному
     */
    fun inverse(value: KBigInteger): KBigInteger

    /**
     * Проверяет, являются ли два числа обратными
     * @param n1 [KBigInteger] - первое число
     * @param n2 [KBigInteger] - второе число
     * @return [Boolean] - true, если числа являются обратными, false - иначе
     */
    fun checkAreInverse(n1: KBigInteger, n2: KBigInteger): Boolean
}