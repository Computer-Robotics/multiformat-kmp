package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base2Tests {
    @Test
    fun `test Encode Empty ByteArray`() {
        val data = byteArrayOf()
        assertEquals("", Base2.encodeToString(data))
        assertContentEquals(data, Base2.decode(Base2.encode(data)))
    }

    @Test
    fun `test Decode Empty String`() {
        assertContentEquals(byteArrayOf(), Base2.decode("".encodeToByteArray()))
    }

    @Test
    fun `test Decode Empty ByteArray`() {
        assertEquals("", Base2.encodeToString(byteArrayOf()))
    }

    @Test
    fun `test decode With Spaces`() {
        val encoded = "01001000 01100101 01101100 01101100 01101111"
        val expected = "Hello".encodeToByteArray()
        assertContentEquals(expected, Base2.decode(encoded))
    }

    @Test
    fun `test Encode Simple ByteArray`() {
        val data = byteArrayOf(0, 1, 2, 3, 4, 5)
        val expected = "000000000000000100000010000000110000010000000101"
        assertEquals(expected, Base2.encodeToString(data))
        assertContentEquals(data, Base2.decode(Base2.encode(data)))
    }

    @Test
    fun `test Decode Valid Base2 String`() {
        val encoded = "000000000000000100000010000000110000010000000101"
        val expected = byteArrayOf(0, 1, 2, 3, 4, 5)
        encoded.encodeToByteArray()
        assertContentEquals(expected, Base2.decode(encoded.encodeToByteArray()))
    }

    @Test
    fun `test Decode another Valid Base2 String`() {
        val encoded = "01001000011001010110110001101100011011110010110000100000010101110110111101110010011011000110010000100001"
        val expected = "Hello, World!".encodeToByteArray()
        assertContentEquals(expected, Base2.decode(encoded.encodeToByteArray()))
    }

    @Test
    fun `test Decode Invalid Base2 String wrong Character`() {
        val invalid = "0100100001100101011011000110110001101111001011000010000001010111011011110111001001101100011001000010000A"
        assertFailsWith<IllegalArgumentException> {
            Base2.decode(invalid.encodeToByteArray())
        }
    }

    @Test
    fun `test Decode Invalid Base2 String wrong Length`() {
        val invalid = "0100100001100101011011000110110001101111001011000010000001010111011011110111001001101100011001000010000" // Missing one 0 at the end
        assertFailsWith<IllegalArgumentException> {
            Base2.decode(invalid.encodeToByteArray())
        }
    }

    @Test
    fun `test Encode Maximum ByteArray`() {
        val data = byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1) // Represents 8 bytes of all 1s
        val expected = "1111111111111111111111111111111111111111111111111111111111111111"
        assertEquals(expected, Base2.encodeToString(data))
        assertContentEquals(data, Base2.decode(Base2.encode(data)))
    }

    @Test
    fun `test Encode To String HelloWorld`() {
        val data = "Hello, World!"
        val expected = "01001000011001010110110001101100011011110010110000100000010101110110111101110010011011000110010000100001"
        assertEquals(expected, Base2.encodeToString(data.encodeToByteArray()))
    }

    @Test
    fun `test Encode Decode Random Data`() {
        val random = kotlin.random.Random
        repeat(100) {
            val size = random.nextInt(1, 1000) // Test with different sizes
            val data = ByteArray(size) { random.nextInt().toByte() }
            val encoded = Base2.encodeToString(data)
            val decoded = Base2.decode(encoded)
            assertContentEquals(data, decoded)
        }
    }

    @Test
    fun `test decode - invalid characters throw exception`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base2.decode("0110X10Y")
        }
        assertEquals("Invalid Base2: Contains illegal characters: 'X', 'Y'", exception.message)
    }

    @Test
    fun `test decode - invalid length throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base2.decode("0000111") // 7 characters
        }
        assertEquals("Invalid Base2: Length must be multiple of 8 (got 7)", exception.message)
    }

    @Test
    fun `test decode - UTF-8 encoded bytes roundtrip`() {
        val original = "Test".encodeToByteArray()
        val encodedBytes = Base2.encode(original)
        val decodedBytes = Base2.decode(encodedBytes)
        assertContentEquals(original, decodedBytes)
    }

    @Test
    fun `test decode - mixed case validation`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base2.decode("0100000A") // 'A' is invalid
        }
        assertEquals("Invalid Base2: Contains illegal characters: 'A'", exception.message)
    }
}
