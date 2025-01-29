package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BigNumTests {
    @Test
    fun `isZero for empty digits`() {
        assertTrue(BigNum().isZero())
    }

    @Test
    fun `isZero for all zero digits`() {
        assertTrue(BigNum(listOf(0, 0, 0)).isZero())
    }

    @Test
    fun `isZero for non-zero digits`() {
        assertFalse(BigNum(listOf(0, 1, 0)).isZero())
    }

    @Test
    fun `divide single-byte number`() {
        val num = BigNum(listOf(100)) // 100
        val (quotient, remainder) = num.divide(36)
        assertEquals(1, quotient.digits.size) // Corrected from 2 to 1
        assertEquals(28, remainder)
        assertEquals(listOf(2), quotient.digits)
    }

    @Test
    fun `divide multi-byte number`() {
        val num = BigNum(listOf(0x12, 0x34)) // 4660
        val (quotient, remainder) = num.divide(36)
        assertEquals(129, quotient.digits.single()) // 4660 ÷ 36 = 129 rem 16
        assertEquals(16, remainder)
    }

    @Test
    fun `multiply with carry`() {
        val num = BigNum(listOf(200))
        val result = num.multiply(2) // 400 = 1*256 + 144
        assertEquals(listOf(1, 144), result.digits)
    }

    @Test
    fun `multiply zero`() {
        val num = BigNum(listOf(0))
        val multiplied = num.multiply(100)
        assertEquals(num, multiplied) // Now passes
    }

    @Test
    fun `add with carry propagation`() {
        val num = BigNum(listOf(255))
        val result = num.add(1) // 255 + 1 = 256
        assertEquals(listOf(1, 0), result.digits)
    }

    @Test
    fun `add to empty number`() {
        val num = BigNum()
        assertEquals(listOf(42), num.add(42).digits)
    }

    @Test
    fun `toByteArray strips leading zeros`() {
        val num = BigNum(listOf(0, 0, 0x12))
        assertContentEquals(byteArrayOf(0x12), num.toByteArray())
    }

    @Test
    fun `toByteArray handles empty digits`() {
        val num = BigNum()
        assertContentEquals(byteArrayOf(0), num.toByteArray())
    }

    @Test
    fun `division and multiplication roundtrip`() {
        val original = BigNum(listOf(0x12, 0x34, 0x56))
        val (quotient, remainder) = original.divide(36)
        val reconstructed = quotient.multiply(36).add(remainder)
        assertEquals(original, reconstructed)
    }
}
