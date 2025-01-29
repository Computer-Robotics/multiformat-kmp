@file:JvmName("ByteArrayExt")
package tech.compubotics.kmp.encoding

import kotlin.jvm.JvmName

/**
 * Converts a ByteArray representing an IP address to its string representation.
 *
 * The method interprets the ByteArray as a sequence of unsigned bytes and formats
 * them as decimal values separated by dots. Each byte is treated as an 8-bit
 * unsigned integer (0-255).
 *
 * @receiver ByteArray containing the raw bytes of the IP address.
 * @return A string in the format "x.x.x.x", where each `x` represents the decimal value
 *         of the corresponding byte in the array.
 */
fun ByteArray.ipToString(): String {
    return this.joinToString(".") { "${it.toInt() and 0xFF}" }
}
