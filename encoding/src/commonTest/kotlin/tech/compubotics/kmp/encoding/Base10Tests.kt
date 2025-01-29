package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base10Tests {
    @Test
    fun `test encodeToString - empty ByteArray returns empty string`() {
        val result = Base10.encodeToString(byteArrayOf())
        assertEquals("", result)
    }

    @Test
    fun `test encodeToString - basic bytes`() {
        val input = byteArrayOf(0x00, 0x41, 0xFF.toByte()) // 0x00, 65, -1 (unsigned 255)
        val expected = "000065255"
        assertEquals(expected, Base10.encodeToString(input))
    }

    @Test
    fun `test encode - roundtrip via String`() {
        val original = "Test".encodeToByteArray()
        val encoded = Base10.encode(original)
        val decoded = Base10.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `test decode - empty string returns empty ByteArray`() {
        val result = Base10.decode("")
        assertContentEquals(byteArrayOf(), result)
    }

    @Test
    fun `test decode - valid decimal string`() {
        val decimalStr = "000065255" // 0x00, 0x41, 0xFF
        val expected = byteArrayOf(0x00, 0x41, 0xFF.toByte())
        assertContentEquals(expected, Base10.decode(decimalStr))
    }

    @Test
    fun `test decode - maximum value 255`() {
        val decoded = Base10.decode("255")
        assertEquals(0xFF.toByte(), decoded[0])
    }

    @Test
    fun `test decode - invalid characters throw exception`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Base10.decode("12A") // 'A' is invalid
            }
        assertEquals("Invalid Base10: Contains illegal characters: 'A'", exception.message)
    }

    @Test
    fun `test decode - invalid length throws exception`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Base10.decode("1234") // Length 4 (not multiple of 3)
            }
        assertEquals("Invalid Base10: Length must be multiple of 3 (got 4)", exception.message)
    }

    @Test
    fun `test decode - chunk exceeding 255 throws exception`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Base10.decode("256") // 256 > 255
            }
        assertEquals("Invalid Base10: Chunk '256' exceeds byte range (0-255)", exception.message)
    }

    @Test
    fun `test decode - negative chunk throws exception`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Base10.decode("-12") // Negative numbers not allowed
            }
        assertEquals("Invalid Base10: Contains illegal characters: '-'", exception.message)
    }
}
