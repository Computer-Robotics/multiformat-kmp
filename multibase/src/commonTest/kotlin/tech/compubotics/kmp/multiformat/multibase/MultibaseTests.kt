package tech.compubotics.kmp.multiformat.multibase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class MultibaseTests {
    @Test
    fun testEncodeDecode() {
        val data = "Hello, world!".encodeToByteArray()
        for (encoding in Encoding.entries) {
             if (encoding != Encoding.BASE256EMOJI && encoding != Encoding.PROQUINT) { // Skip BASE256EMOJI as it's not yet implemented
                val encoded = Multibase.encode(encoding, data)
                val (decodedEncoding, decodedData) = Multibase.decode(encoded)
                assertEquals(encoding, decodedEncoding, "Failed in $encoding")
                assertEquals(data.decodeToString(), decodedData.decodeToString(), "Failed in $encoding")
            }
        }
    }

    @Test
    fun testDecode_emptyString_returnsNullEncoding() {
        val (encoding, data) = Multibase.decode("")
        assertEquals(Encoding.NULL, encoding)
        assertEquals(0, data.size)
    }

    @Test
    fun testDecode_unknownPrefix_throwsUnknownPrefixException() {
        val unknownPrefix = "q"
        val encodedData = unknownPrefix + "Hello, world!"
        assertFailsWith<UnknownPrefixException> {
            Multibase.decode(encodedData)
        }
    }

    @Test
    fun testBase256Emoji_encode_throwsUnsupportedEncodingException() {
        assertFailsWith<UnsupportedEncodingException> {
            Multibase.encode(Encoding.BASE256EMOJI, "test".encodeToByteArray())
        }
    }
}
