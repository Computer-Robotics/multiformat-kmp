package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base8Tests {
    @Test
    fun `test Encode Empty ByteArray`() {
        val data = byteArrayOf()
        assertEquals("", Base8.encodeToString(data))
        assertContentEquals(data, Base8.decode(Base8.encode(data)))
    }

    @Test
    fun `test Encode Simple ByteArray`() {
        val data = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7)
        val expected = "000001002003004005006007"
        assertEquals(expected, Base8.encodeToString(data))
        assertContentEquals(data, Base8.decode(Base8.encode(data)))
    }

    @Test
    fun `test Decode Valid Base8 String`() {
        val encoded = "110 145 154 154 157 054 040 127 157 162 154 144 041"
        val expected = "Hello, World!".encodeToByteArray()
        assertContentEquals(expected, Base8.decode(encoded.encodeToByteArray()))
    }

    @Test
    fun `test EncodeToString HelloWorld`() {
        val data = "Hello, World!".encodeToByteArray()
        val expected = "110145154154157054040127157162154144041"
        assertEquals(expected, Base8.encodeToString(data))
        assertEquals("Hello, World!", Base8.decode(Base8.encode(data)).decodeToString())
    }

    @Test
    fun `test Decode Empty String`() {
        assertContentEquals(byteArrayOf(), Base8.decode("".encodeToByteArray()))
    }

    @Test
    fun `test Decode another Valid Base8 String`() {
        val encoded = "000001002003004005006007"
        val expected = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7)
        assertContentEquals(expected, Base8.decode(encoded.encodeToByteArray()))
    }

    @Test
    fun `test Decode Invalid Base8 String wrong Character`() {
        val invalid = "22044554331471405355636270408"
        assertFailsWith<IllegalArgumentException> {
            Base8.decode(invalid.encodeToByteArray())
        }
    }

    @Test
    fun `test Encode Decode RandomData`() {
        val random = kotlin.random.Random
        repeat(100) {
            val size = random.nextInt(1, 1000) // Test with different sizes
            val data = ByteArray(size) { random.nextInt().toByte() }
            val encoded = Base8.encodeToString(data)
            val decoded = Base8.decode(encoded.encodeToByteArray())
            assertContentEquals(data, decoded)
        }
    }

    @Test
    fun `test Encode Empty String`() {
        assertEquals("", Base8.encodeToString(byteArrayOf()))
    }

    @Test
    fun `test Decode Empty ByteArray`() {
        assertEquals("", Base8.decode(byteArrayOf()).decodeToString())
    }

    @Test
    fun `test encodeToString - empty ByteArray returns empty string`() {
        val result = Base8.encodeToString(byteArrayOf())
        assertEquals("", result)
    }

    @Test
    fun `test encodeToString - basic bytes`() {
        val input = byteArrayOf(0x00, 0x41, 0xFF.toByte()) // 0x00, 65, -1
        val expected = "000101377"
        assertEquals(expected, Base8.encodeToString(input))
    }

    @Test
    fun `test encode - roundtrip via String`() {
        val original = "KMP".encodeToByteArray()
        val encoded = Base8.encode(original)
        val decoded = Base8.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `test decode - empty string returns empty ByteArray`() {
        val result = Base8.decode("")
        assertContentEquals(byteArrayOf(), result)
    }

    @Test
    fun `test decode - valid octal string`() {
        val octalStr = "000101377" // 0x00, 0x41, 0xFF
        val expected = byteArrayOf(0x00, 0x41, 0xFF.toByte())
        assertContentEquals(expected, Base8.decode(octalStr))
    }

    @Test
    fun `test decode - all zeros`() {
        val octalStr = "000000000" // 0x00, 0x00, 0x00
        assertContentEquals(byteArrayOf(0, 0, 0), Base8.decode(octalStr))
    }

    @Test
    fun `test decode - invalid characters throw exception`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                Base8.decode("01208A") // '8' and 'A' are invalid
            }
        assertEquals("Invalid Base8: Contains illegal characters: '8', 'A'", exception.message)
    }

    @Test
    fun `test decode - invalid length throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base8.decode("0000") // Length 4 (not multiple of 3)
        }
        assertEquals("Invalid Base8: Length must be multiple of 3 (got 4)", exception.message)
    }

    @Test
    fun `test decode - mixed case validation`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base8.decode("12x") // 'x' is invalid
        }
        assertEquals("Invalid Base8: Contains illegal characters: 'x'", exception.message)
    }
}
