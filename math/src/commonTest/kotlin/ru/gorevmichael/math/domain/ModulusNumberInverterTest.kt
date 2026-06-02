package ru.gorevmichael.math.domain

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import ru.gorevmichael.math.domain.inverters.ModulusNumberInverter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModulusNumberInverterTest {

    @Test
    fun testInverseBasic() {
        val modulus = "11".toBigInteger(10)
        val inverter = ModulusNumberInverter(modulus)
        
        // 3 * 4 = 12 = 1 mod 11
        val inv3 = inverter.inverse("3".toBigInteger(10))
        assertEquals("4".toBigInteger(10), inv3)
        
        // 7 * 8 = 56 = 1 mod 11
        val inv7 = inverter.inverse("7".toBigInteger(10))
        assertEquals("8".toBigInteger(10), inv7)
    }

    @Test
    fun testInverseWithNegativeInput() {
        val modulus = BigInteger.fromString("13")
        val inverter = ModulusNumberInverter(modulus)
        
        // -1 mod 13 is 12. Inverse of 12 mod 13 is 12.
        val invMinus1 = inverter.inverse(BigInteger.fromString("-1"))
        assertEquals(BigInteger.fromString("12"), invMinus1)
    }

    @Test
    fun testCheckAreInverse() {
        val modulus = BigInteger.fromString("17")
        val inverter = ModulusNumberInverter(modulus)
        
        val n1 = BigInteger.fromString("3")
        val n2 = BigInteger.fromString("6") // 3 * 6 = 18 = 1 mod 17
        
        assertTrue(inverter.checkAreInverse(n1, n2))
    }

    @Test
    fun testLargeNumbers() {
        // Example from some RSA or ECDSA params
        val modulus = BigInteger.fromString("1000000007") // A prime
        val inverter = ModulusNumberInverter(modulus)
        
        val value = BigInteger.fromString("123456789")
        val inv = inverter.inverse(value)
        
        assertTrue(inverter.checkAreInverse(value, inv), "Inverse calculation failed for large numbers")
    }
}
