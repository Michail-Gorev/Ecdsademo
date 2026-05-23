package ru.gorevmichael.math.domain

import io.github.gatrongdev.kbignum.math.KBigInteger
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModulusNumberInverterTest {

    @Test
    fun testInverseBasic() {
        val modulus = KBigInteger.fromString("11")
        val inverter = ModulusNumberInverter(modulus)
        
        // 3 * 4 = 12 = 1 mod 11
        val inv3 = inverter.inverse(KBigInteger.fromString("3"))
        assertEquals(KBigInteger.fromString("4"), inv3)
        
        // 7 * 8 = 56 = 1 mod 11
        val inv7 = inverter.inverse(KBigInteger.fromString("7"))
        assertEquals(KBigInteger.fromString("8"), inv7)
    }

    @Test
    fun testInverseWithNegativeInput() {
        val modulus = KBigInteger.fromString("13")
        val inverter = ModulusNumberInverter(modulus)
        
        // -1 mod 13 is 12. Inverse of 12 mod 13 is 12.
        val invMinus1 = inverter.inverse(KBigInteger.fromString("-1"))
        assertEquals(KBigInteger.fromString("12"), invMinus1)
    }

    @Test
    fun testCheckAreInverse() {
        val modulus = KBigInteger.fromString("17")
        val inverter = ModulusNumberInverter(modulus)
        
        val n1 = KBigInteger.fromString("3")
        val n2 = KBigInteger.fromString("6") // 3 * 6 = 18 = 1 mod 17
        
        assertTrue(inverter.checkAreInverse(n1, n2))
    }

    @Test
    fun testLargeNumbers() {
        // Example from some RSA or ECDSA params
        val modulus = KBigInteger.fromString("1000000007") // A prime
        val inverter = ModulusNumberInverter(modulus)
        
        val value = KBigInteger.fromString("123456789")
        val inv = inverter.inverse(value)
        
        assertTrue(inverter.checkAreInverse(value, inv), "Inverse calculation failed for large numbers")
    }
}
