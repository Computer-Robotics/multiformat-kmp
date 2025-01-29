package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base32zTests {
    @Test
    fun `test encode empty input`() {
        assertEquals("", Base32z.encodeToString(byteArrayOf()))
    }

    @Test
    fun `test roundtrip basic data`() {
        val original = "Hello Tahoe-LAFS!".encodeToByteArray()
        val encoded = Base32z.encodeToString(original)
        val decoded = Base32z.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `test single byte`() {
        val data = byteArrayOf(0x61) // 'a'
        val encoded = Base32z.encodeToString(data)
        assertEquals("cr", encoded)
        assertContentEquals(data, Base32z.decode(encoded))
    }

    @Test
    fun `test case-insensitive decoding`() {
        val encoded = "YbNdRfG8EjKmCPQxOT1UwISzA345H769"
        val decoded = Base32z.decode(encoded)
        val reEncoded = Base32z.encodeToString(decoded)
        assertEquals(encoded.lowercase(), reEncoded)
    }

    @Test
    fun `test partial byte group`() {
        val data = byteArrayOf(0x61, 0x62) // "ab"
        val encoded = Base32z.encodeToString(data)
        assertEquals("cfty", encoded) // No padding in z-base-32
        assertContentEquals(data, Base32z.decode(encoded))
    }

    @Test
    fun `test full integration test`() {
        val original = "The quick brown fox jumps over the lazy dog".encodeToByteArray()
        val encoded = Base32z.encodeToString(original)
        val decoded = Base32z.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `test reject invalid chars`() {
        assertFailsWith<IllegalArgumentException> {
            Base32z.decode("ybndrfg8l") // 'l' is invalid
        }
    }

    @Test
    fun `test reject invalid characters`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Base32z.decode("ybndrfg8l") // 'l' is invalid
        }
        assertEquals("Invalid Base32z characters: l", exception.message)
    }

    @Test
    fun `test known test vector`() {
        val data = byteArrayOf(0x61, 0x62, 0x63) // "abc"
        val encoded = Base32z.encodeToString(data)
        assertEquals("cftgg", encoded)
    }

    @Test
    fun `test partial byte handling`() {
        val data = byteArrayOf(0x61) // "a"
        val encoded = Base32z.encodeToString(data)
        assertEquals("cr", encoded) // 8 bits → 2*5 bits = 10 bits → 2 characters
        val decoded = Base32z.decode(encoded)
        assertContentEquals(data, decoded)
    }
}
