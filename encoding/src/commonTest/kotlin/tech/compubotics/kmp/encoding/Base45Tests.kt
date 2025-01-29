package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base45Tests {
    @Test
    fun `encode empty data`() {
        assertEquals("", Base45.encodeToString(byteArrayOf()))
    }

    @Test
    fun `encode two bytes AB as BB8`() {
        val data = byteArrayOf(0x41, 0x42) // "AB"
        assertEquals("BB8", Base45.encodeToString(data))
    }

    @Test
    fun `decode BB8 to AB`() {
        val decoded = Base45.decode("BB8")
        assertContentEquals(byteArrayOf(0x41, 0x42), decoded)
    }

    @Test
    fun `encode single byte 0x61 as 72`() {
        val data = byteArrayOf(0x61) // 97
        assertEquals("72", Base45.encodeToString(data))
    }

    @Test
    fun `decode 72 to 0x61`() {
        val decoded = Base45.decode("72")
        assertContentEquals(byteArrayOf(0x61), decoded)
    }

    @Test
    fun `reject invalid length`() {
        assertFailsWith<IllegalArgumentException> {
            Base45.decode("B")
        }
    }

    @Test
    fun `reject invalid characters`() {
        assertFailsWith<IllegalArgumentException> {
            Base45.decode("B@8")
        }
    }

    @Test
    fun `roundtrip complex data`() {
        val original = "Hello Base45!".encodeToByteArray()
        val encoded = Base45.encodeToString(original)
        val decoded = Base45.decode(encoded)
        assertContentEquals(original, decoded)
    }
}
