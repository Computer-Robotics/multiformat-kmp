package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProquintTests {
    private val proquint = Proquint

    @Test
    fun `encode test vectors`() {
        assertEquals("lusab-babad", proquint.encodeToString("127.0.0.1".ipToBytes()))
        assertEquals("gutih-tugad", proquint.encodeToString("63.84.220.193".ipToBytes()))
        assertEquals("gutuk-bisog", proquint.encodeToString("63.118.7.35".ipToBytes()))
        assertEquals("mudof-sakat", proquint.encodeToString("140.98.193.141".ipToBytes()))
        assertEquals("haguz-biram", proquint.encodeToString("64.255.6.200".ipToBytes()))
        assertEquals("mabiv-gibot", proquint.encodeToString("128.30.52.45".ipToBytes()))
        assertEquals("natag-lisaf", proquint.encodeToString("147.67.119.2".ipToBytes()))
        assertEquals("tibup-zujah", proquint.encodeToString("212.58.253.68".ipToBytes()))
        assertEquals("tobog-higil", proquint.encodeToString("216.35.68.215".ipToBytes()))
        assertEquals("todah-vobij", proquint.encodeToString("216.68.232.21".ipToBytes()))
        assertEquals("sinid-makam", proquint.encodeToString("198.81.129.136".ipToBytes()))
        assertEquals("budov-kuras", proquint.encodeToString("12.110.110.204".ipToBytes()))
    }

    @Test
    fun `decode test vectors`() {
        assertEquals("127.0.0.1", proquint.decode("lusab-babad").ipToString())
        assertEquals("63.84.220.193", proquint.decode("gutih-tugad").ipToString())
        assertEquals("63.118.7.35", proquint.decode("gutuk-bisog").ipToString())
        assertEquals("140.98.193.141", proquint.decode("mudof-sakat").ipToString())
        assertEquals("64.255.6.200", proquint.decode("haguz-biram").ipToString())
        assertEquals("128.30.52.45", proquint.decode("mabiv-gibot").ipToString())
        assertEquals("147.67.119.2", proquint.decode("natag-lisaf").ipToString())
        assertEquals("212.58.253.68", proquint.decode("tibup-zujah").ipToString())
        assertEquals("216.35.68.215", proquint.decode("tobog-higil").ipToString())
        assertEquals("216.68.232.21", proquint.decode("todah-vobij").ipToString())
        assertEquals("198.81.129.136", proquint.decode("sinid-makam").ipToString())
        assertEquals("12.110.110.204", proquint.decode("budov-kuras").ipToString())
    }

    @Test
    fun `encode empty data`() {
        assertEquals("", proquint.encodeToString(byteArrayOf()))
    }

    @Test
    fun `encode single word`() {
        val data = byteArrayOf(0x2D.toByte(), 0x7F.toByte()) // 0x2D7F
        assertEquals("fujuz", proquint.encodeToString(data))
    }

    @Test
    fun `invalid characters`() {
        assertFailsWith<IllegalArgumentException> {
            proquint.decode("abcde") // 'x' not in consonant list
        }
    }

    @Test
    fun `complex encoding`() {
        val data = byteArrayOf(0x00, 0x00)
        assertEquals("babab", proquint.encodeToString(data))
    }

    @Test
    fun `very complex encoding`() {
        val textToEncode = listOf(
            "Hello World!",
            "https://www.compubotics.tech"
        )
        textToEncode.forEach {
            val encoded = proquint.encodeToString(it.encodeToByteArray())
            val decoded = proquint.decode(encoded)
            assertEquals(it, decoded.decodeToString())
        }
    }
}
