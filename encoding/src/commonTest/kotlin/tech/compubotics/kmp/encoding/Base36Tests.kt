package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base36Tests {
    @Test
    fun `test encode empty input`() {
        assertEquals("", Base36.encodeToString(byteArrayOf()))
    }

    @Test
    fun `test encode single zero byte`() {
        assertEquals("0", Base36.encodeToString(byteArrayOf(0)))
    }

    @Test
    fun `test encode basic value`() {
        // 0x61 (97) → 2*36 +25 =97 → "2p"
        assertEquals("2p", Base36.encodeToString(byteArrayOf(0x61)))
    }

    @Test
    fun `test encode multi-byte value`() {
        // 0x6162 (24930) → 24930 = 19*36² + 8*36 + 18 → "j8i"
        val data = byteArrayOf(0x61, 0x62)
        assertEquals("j8i", Base36.encodeToString(data))
    }

    @Test
    fun `test decode empty string`() {
        assertContentEquals(byteArrayOf(), Base36.decode(""))
    }

    @Test
    fun `test decode zero`() {
        assertContentEquals(byteArrayOf(0), Base36.decode("0"))
    }

    @Test
    fun `test decode basic value`() {
        // 2*36 + 25 = 97 → '2p' (not '2z')
        val decoded = Base36.decode("2p")
        assertContentEquals(byteArrayOf(0x61), decoded)
    }

    @Test
    fun `test decode multi-byte value`() {
        val decoded = Base36.decode("j8i") // 19*36² +8*36 +18 =24930 → 0x6162
        assertContentEquals(byteArrayOf(0x61, 0x62), decoded)
    }

    @Test
    fun `test case sensitivity`() {
        assertFailsWith<IllegalArgumentException> {
            Base36.decode("J8I") // Uppercase not allowed in Base36
        }
        assertContentEquals(byteArrayOf(0x61, 0x62), Base36Upper.decode("J8I"))
    }

    @Test
    fun `test invalid characters`() {
        assertFailsWith<IllegalArgumentException> {
            Base36.decode("ab!")
        }
    }

    @Test
    fun `test roundtrip random data`() {
        val original = "Kotlin Multiplatform".encodeToByteArray()
        val encoded = Base36.encodeToString(original)
        val decoded = Base36.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `test roundtrip with leading zeros`() {
        val original = byteArrayOf(0, 0, 0x61)
        val encoded = Base36.encodeToString(original)
        assertEquals("2p", encoded) // Updated expectation
        assertContentEquals(byteArrayOf(0x61), Base36.decode(encoded))
    }

    @Test
    fun `test case insensitivity`() {
        val original = "Hello World"
        val encoded1 = Base36.encodeToString(original.encodeToByteArray())
        val encoded2 = Base36Upper.encodeToString(original.encodeToByteArray())

        assertEquals(original, Base36CaseInsensitive.decode(encoded1).decodeToString())
        assertEquals(original, Base36CaseInsensitive.decode(encoded2).decodeToString())
    }
}
