package tech.compubotics.kmp.encoding

/**
 * The Decoder interface defines methods for decoding encoded data back to its original form.
 *
 * This interface provides two decoding functionalities:
 * - Decoding a ByteArray into a ByteArray representation of the original data.
 * - Decoding a ByteArray into a String representation of the original data.
 *
 * The decoding strategy is determined by the class implementing this interface.
 */
interface Decoder {
    /**
     * Decodes the given encoded string into its original ByteArray representation.
     *
     * This method processes the input string to remove spaces and interprets the resulting data
     * as a sequence of binary values. Each 8-character binary sequence is converted into its
     * corresponding Byte value to form the original ByteArray. The input string must adhere to
     * the constraints of the decoding implementation, such as being a valid binary sequence
     * with a length that is a multiple of 8 characters.
     *
     * @param data The encoded string to be decoded. It may contain spaces that will be removed
     *             during the decoding process.
     * @return A ByteArray representing the original binary data.
     * @throws IllegalArgumentException If the input string is not a valid binary sequence or
     *                                  its length is not a multiple of 8 characters.
     */
    @Throws(IllegalArgumentException::class)
    fun decode(data: String): ByteArray

    /**
     * Decodes an encoded ByteArray back into its original binary representation.
     *
     * This method interprets the input ByteArray by treating its content as binary
     * data encoded in a specific format. It ensures that the input ByteArray adheres
     * to the expected format, such as being a multiple of 8 bytes, to allow correct decoding.
     *
     * @param data The encoded ByteArray to be decoded. Each 8-byte segment represents a binary-encoded value.
     * @return A ByteArray containing the original decoded binary data. Returns an empty ByteArray if the input is empty.
     * @throws IllegalArgumentException If the input ByteArray does not follow the expected format
     *                                  (e.g., size is not a multiple of 8).
     */
    @Throws(IllegalArgumentException::class)
    fun decode(data: ByteArray): ByteArray
}
