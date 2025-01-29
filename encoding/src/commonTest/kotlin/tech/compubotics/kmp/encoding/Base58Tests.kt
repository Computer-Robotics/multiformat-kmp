package tech.compubotics.kmp.encoding

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base58Tests {
    @Test
    fun `encode empty data`() {
        assertEquals("", Base58Bitcoin.encodeToString(byteArrayOf()))
    }

    @Test
    fun `encode basic Bitcoin value`() {
        val data = "Hello World".encodeToByteArray()
        assertEquals("JxF12TrwUP45BMd", Base58Bitcoin.encodeToString(data))
    }

    @Test
    fun `decode basic Bitcoin value`() {
        val decoded = Base58Bitcoin.decode("JxF12TrwUP45BMd")
        assertContentEquals("Hello World".encodeToByteArray(), decoded)
    }

    @Test
    fun `handle leading zeros`() {
        val data = byteArrayOf(0, 0, 0x61)
        val encoded = Base58Bitcoin.encodeToString(data)
        assertEquals("112g", encoded)
        assertContentEquals(data, Base58Bitcoin.decode(encoded))
    }

    @Test
    fun `reject invalid characters`() {
        assertFailsWith<IllegalArgumentException> {
            Base58Bitcoin.decode("JxF12TrwUP45BMd@")
        }
    }

    @Test
    fun `Base58 Bitcoin roundtrip complex data`() {
        val original = "Kotlin Multiplatform Rocks!".encodeToByteArray()
        val encoded = Base58Bitcoin.encodeToString(original)
        val decoded = Base58Bitcoin.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `Base58 Flickr roundtrip complex data`() {
        val original = "Kotlin Multiplatform Rocks!".encodeToByteArray()
        val encoded = Base58Flickr.encodeToString(original)
        val decoded = Base58Flickr.decode(encoded)
        assertContentEquals(original, decoded)
    }

    @Test
    fun `encode basic Flickr value`() {
        val data = "Hello World".encodeToByteArray()
        assertEquals("iXf12sRWto45bmC", Base58Flickr.encodeToString(data))
    }

    @Test
    fun `decode basic Flickr value`() {
        val decoded = Base58Flickr.decode("iXf12sRWto45bmC")
        assertContentEquals("Hello World".encodeToByteArray(), decoded)
    }
}
