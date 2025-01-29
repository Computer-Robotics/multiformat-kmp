@file:JvmName("StringExt")
package tech.compubotics.kmp.encoding

import kotlin.jvm.JvmName

/**
 * Converts an IPv4 address from its string representation into a ByteArray.
 *
 * The input string must be in the standard IPv4 format, with four decimal octets
 * separated by periods (e.g., "192.168.0.1"). Each octet is parsed as an integer
 * and converted to a byte, resulting in a ByteArray of size 4.
 *
 * @return A ByteArray containing four bytes representing the IPv4 address.
 *         Each byte corresponds to one of the octets in the input string.
 *         Throws an exception if the input string is not in a valid IPv4 format.
 */
fun String.ipToBytes(): ByteArray {
    return split(".").map { it.toInt().toByte() }.toByteArray()
}
