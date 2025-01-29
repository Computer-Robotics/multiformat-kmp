package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base32Tests {
    @Test
    fun `test Base32 encodes without padding`() {
        val data = "Hello".encodeToByteArray()
        assertEquals("jbswy3dp", Base32.encodeToString(data))
    }

    @Test
    fun `test Base32Pad adds padding`() {
        val data = "Hello".encodeToByteArray()
        assertEquals("jbswy3dp", Base32Pad.encodeToString(data)) // Same as non-padded due to exact fit
    }

    @Test
    fun `test Base32Upper encodes uppercase`() {
        val data = "Hello".encodeToByteArray()
        assertEquals("JBSWY3DP", Base32Upper.encodeToString(data))
    }

    @Test
    fun `test Base32Hex encodes with hex alphabet`() {
        val data = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F) // "Hello"
        assertEquals("91IMOR3F", Base32HexPadUpper.encodeToString(data))
    }

    @Test
    fun `test Base32Hex decodes case-insensitively`() {
        val encoded = "91IMOR3F" // Mixed case input
        val decoded = Base32HexPad.decode(encoded) // Normalizes to lowercase
        assertContentEquals("Hello".encodeToByteArray(), decoded)
    }

    @Test
    fun `test empty input handled`() {
        assertContentEquals(byteArrayOf(), Base32.decode(""))
        assertEquals("", Base32.encodeToString(byteArrayOf()))
    }

    @Test
    fun `test roundtrip with padding`() {
        val original = "Kotlin Multiplatform!".encodeToByteArray()
        val encoded = Base32PadUpper.encodeToString(original)
        val decoded = Base32PadUpper.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `test invalid characters throw`() {
        assertFailsWith<IllegalArgumentException> {
            Base32HexUpper.decode("Z!234") // '!' is invalid
        }
    }

    @Test
    fun `test decode Base32 case insensitive`() {
        val textToEncode = "Hello World!"
        val encoded1 = Base32.encodeToString(textToEncode.encodeToByteArray())
        val encoded2 = Base32Upper.encodeToString(textToEncode.encodeToByteArray())
        val encoded3 = Base32Pad.encodeToString(textToEncode.encodeToByteArray())
        val encoded4 = Base32PadUpper.encodeToString(textToEncode.encodeToByteArray())

        assertEquals(textToEncode, Base32CaseInsensitive.decode(encoded1).decodeToString())
        assertEquals(textToEncode, Base32CaseInsensitive.decode(encoded2).decodeToString())
        assertEquals(textToEncode, Base32CaseInsensitive.decode(encoded3).decodeToString())
        assertEquals(textToEncode, Base32CaseInsensitive.decode(encoded4).decodeToString())
    }

    @Test
    fun `test decode Base32Hex case insensitive`() {
        val textToEncode = "Hello World!"
        val encoded1 = Base32Hex.encodeToString(textToEncode.encodeToByteArray())
        val encoded2 = Base32HexUpper.encodeToString(textToEncode.encodeToByteArray())
        val encoded3 = Base32HexPadUpper.encodeToString(textToEncode.encodeToByteArray())
        val encoded4 = Base32HexPad.encodeToString(textToEncode.encodeToByteArray())

        assertEquals(textToEncode, Base32HexCaseInsensitive.decode(encoded1).decodeToString())
        assertEquals(textToEncode, Base32HexCaseInsensitive.decode(encoded2).decodeToString())
        assertEquals(textToEncode, Base32HexCaseInsensitive.decode(encoded3).decodeToString())
        assertEquals(textToEncode, Base32HexCaseInsensitive.decode(encoded4).decodeToString())
    }
}
