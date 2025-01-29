package tech.compubotics.kmp.encoding

/**
 * The `Base32z` object provides encoding and decoding functionalities
 * for Base32z encoding scheme. Base32z is a variant of the Base32 algorithm
 * optimized for certain applications, using a unique character set for encoding.
 *
 * Implements the `Coder` interface, which combines methods for both encoding and decoding.
 */
object Base32z : Coder {
    /**
     * A constant string representing the custom alphabet used for encoding
     * and decoding operations in the Base32 implementation. This alphabet
     * is a sequence of 32 unique characters specifically ordered for encoding.
     *
     * It includes lowercase letters, digits, and custom placements to align
     * with the encoding logic and decoding requirements. The order is critical
     * for correct mapping between encoded characters and their binary values.
     */
    private const val ALPHABET = "ybndrfg8ejkmcpqxot1uwisza345h769"

    /**
     * Defines the number of bits used to group data during binary encoding or decoding operations.
     *
     * In the context of Base2 encoding, this constant is used to segment data into
     * manageable parts, facilitating operations like conversions, validations, or transformations.
     * Specifically, it determines the size of binary groups, impacting how data is processed
     * in terms of alignment and organization during encoding or decoding workflows.
     */
    private const val GROUP_BITS = 5

    /**
     * Represents the number of bits in a single byte.
     *
     * This constant is used in the context of binary encoding and decoding
     * to ensure consistent handling of byte-sized binary data, particularly
     * in contexts like Base2 encoding where each byte is represented as
     * an 8-bit binary string.
     */
    private const val BYTE_BITS = 8

    /**
     * Encodes the input binary data into its byte array representation.
     *
     * This method utilizes `encodeToString` to convert the input ByteArray into
     * a string representation and then encodes the resultant string to a ByteArray.
     *
     * @param data The input ByteArray to be encoded.
     * @return A ByteArray containing the encoded representation of the input data.
     */
    override fun encode(data: ByteArray): ByteArray {
        return encodeToString(data).encodeToByteArray()
    }

    /**
     * Encodes the provided binary data into a string format using a custom encoding scheme.
     *
     * This method processes the input ByteArray by sequentially encoding each byte
     * into the specified character set (`ALPHABET`), grouping bits together as defined
     * by `GROUP_BITS`. It ensures proper handling of remaining bits by applying padding.
     *
     * @param data The input ByteArray to be encoded. Each byte will contribute
     *             its bits to form the encoded string.
     * @return A String containing the encoded representation of the input ByteArray.
     *         Returns an empty string if the input ByteArray is empty.
     */
    override fun encodeToString(data: ByteArray): String {
        if (data.isEmpty()) return ""

        var buffer = 0L
        var bitsStored = 0
        val output = StringBuilder()

        for (byte in data) {
            buffer = (buffer shl 8) or (byte.toLong() and 0xFF)
            bitsStored += 8

            while (bitsStored >= 5) {
                bitsStored -= 5
                val index = (buffer shr bitsStored).toInt() and 0x1F
                output.append(ALPHABET[index])
            }
        }

        // Handle remaining bits
        if (bitsStored > 0) {
            val index = (buffer shl (5 - bitsStored)).toInt() and 0x1F
            output.append(ALPHABET[index])
        }

        return output.toString()
    }

    /**
     * Decodes a string of encoded data into its original binary representation as a ByteArray.
     *
     * This method parses the input string and interprets each character into its respective binary value.
     * Characters must belong to a predefined alphabet, and the method validates the input to ensure
     * that only valid characters are processed. The decoded output is returned as a ByteArray.
     *
     * @param data The encoded string to decode. It must only contain valid characters from the defined alphabet.
     * @return A ByteArray containing the binary representation of the decoded data.
     * @throws IllegalArgumentException If the input string contains invalid characters.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: String): ByteArray {
        val normalized = data.lowercase()
        val output = mutableListOf<Byte>()
        var buffer = 0L
        var bitsCollected = 0

        for (c in normalized) {
            val value = ALPHABET.indexOf(c)
            require(value != -1) { "Invalid Base32z characters: $c" }

            buffer = (buffer shl GROUP_BITS) or value.toLong()
            bitsCollected += GROUP_BITS

            while (bitsCollected >= BYTE_BITS) {
                bitsCollected -= BYTE_BITS
                val byte = (buffer shr bitsCollected).toByte()
                output.add(byte)
            }
        }

        return output.toByteArray()
    }

    /**
     * Decodes an encoded ByteArray back into its original form.
     *
     * This method interprets the input ByteArray as a UTF-8 encoded string, replaces any spaces, and decodes it
     * into the original binary data. The input is validated to ensure it adheres to the expected format.
     *
     * @param data The encoded ByteArray to be decoded. It must represent a valid binary sequence encoded in UTF-8.
     * @return A ByteArray containing the original binary data after being decoded.
     * @throws IllegalArgumentException If the input does not represent valid encoded binary data.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: ByteArray): ByteArray {
        return decode(data.decodeToString())
    }
}
