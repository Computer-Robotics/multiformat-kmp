package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base16Tests {
    @Test
    fun `test Base16 encodeToString - lowercase hex`() {
        assertEquals("0041ff", Base16.encodeToString(testBytes))
    }

    @Test
    fun `test Base16 decode - lowercase hex`() {
        val decoded = Base16.decode("0041ff")
        assertContentEquals(testBytes, decoded)
    }

    @Test
    fun `test Base16 rejects uppercase`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base16.decode("0041FF")
        }
        assertEquals("Invalid Base16: Contains illegal characters: 'F'", exception.message)
    }

    @Test
    fun `test Base16Upper encodeToString - uppercase hex`() {
        assertEquals("0041FF", Base16Upper.encodeToString(testBytes))
    }

    @Test
    fun `test Base16Upper decode - uppercase hex`() {
        val decoded = Base16Upper.decode("0041FF")
        assertContentEquals(testBytes, decoded)
    }

    @Test
    fun `test Base16Upper rejects lowercase`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base16Upper.decode("0041ff")
        }
        assertEquals("Invalid Base16: Contains illegal characters: 'f'", exception.message)
    }

    @Test
    fun `test empty input handled`() {
        assertContentEquals(byteArrayOf(), Base16.decode(""))
        assertEquals("", Base16.encodeToString(byteArrayOf()))
    }

    @Test
    fun `test odd length throws exception`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base16.decode("0")
        }
        assertEquals("Invalid Base16: Length must be even (got 1)", exception.message)
    }

    @Test
    fun `test invalid characters throw`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base16.decode("0g")
        }
        assertEquals("Invalid Base16: Contains illegal characters: 'g'", exception.message)
    }

    @Test
    fun `test roundtrip via bytes`() {
        val original = "Test".encodeToByteArray()
        val encoded = Base16Upper.encode(original)
        val decoded = Base16Upper.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `test decode Base16 case insensitive`() {
        val text = "Hello World!"
        val encodedUpper = Base16Upper.encodeToString(text.encodeToByteArray())
        val encodedLower = Base16.encodeToString(text.encodeToByteArray())
        assertEquals(text, Base16CaseInsensitive.decodeCaseInsensitive(encodedLower).decodeToString())
        assertEquals(text, Base16CaseInsensitive.decodeCaseInsensitive(encodedUpper).decodeToString())
    }

    companion object {
        private val testBytes = byteArrayOf(0x00, 0x41, 0xFF.toByte()) // 0x00, 65, -1
    }
}
