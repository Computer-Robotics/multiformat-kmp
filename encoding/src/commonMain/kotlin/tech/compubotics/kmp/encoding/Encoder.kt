package tech.compubotics.kmp.encoding

/**
 * The Encoder interface defines methods for encoding raw binary data.
 *
 * This interface provides two encoding functionalities:
 * - Encoding a ByteArray into a ByteArray representation.
 * - Encoding a ByteArray into a String representation.
 *
 * The actual encoding strategy is determined by the implementing class.
 */
interface Encoder {
    /**
     * Encodes the given binary data into a transformed ByteArray representation.
     *
     * @param data The input ByteArray to be encoded.
     * @return A ByteArray containing the encoded representation of the input data.
     */
    fun encode(data: ByteArray): ByteArray

    /**
     * Encodes the given binary data into its string representation based on the implementing class's encoding strategy.
     *
     * @param data The input ByteArray to be encoded.
     * @return A String containing the encoded representation of the input data.
     */
    fun encodeToString(data: ByteArray): String
}
