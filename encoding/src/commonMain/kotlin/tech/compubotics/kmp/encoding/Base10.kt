package tech.compubotics.kmp.encoding

/**
 * The `Base10` object is an implementation of the `Coder` interface, designed to provide
 * encoding and decoding functionalities for binary data using a Base10 format.
 *
 * Encoding converts binary data into a decimal string representation where each byte is
 * translated into a three-digit decimal string (e.g., 000 to 255). Decoding reverses this
 * process, restoring the original binary data.
 *
 * This class ensures strict validation of input formats during encoding and decoding
 * operations, guaranteeing compatibility with the Base10 encoding scheme.
 */
object Base10 : Coder {
    /**
     * Encodes the given binary data into a transformed byte array representation.
     *
     * The method uses `encodeToString` to first convert the input data into a string representation,
     * and subsequently encodes this string into a byte array.
     *
     * @param data The input ByteArray to be encoded.
     * @return A ByteArray containing the encoded representation of the input data.
     */
    override fun encode(data: ByteArray): ByteArray {
        return encodeToString(data).encodeToByteArray()
    }

    /**
     * Encodes the given binary data into a string representation.
     *
     * This method converts each byte in the provided ByteArray into its unsigned decimal
     * representation, formatted as a 3-digit string (e.g., 000 to 255), and concatenates
     * these values into a single string without spaces or separators.
     *
     * @param data The input ByteArray to be encoded. Each byte will be transformed into a
     *             3-character decimal string.
     * @return A String representing the encoded form of the data. Each byte will be
     *         represented as a 3-digit decimal string, concatenated together.
     */
    override fun encodeToString(data: ByteArray): String {
        return data.joinToString("") { byte ->
            // Convert to unsigned byte (0-255) and format as 3-digit decimal
            val unsignedValue = byte.toInt() and 0xFF
            unsignedValue.toString().padStart(3, '0')
        }
    }

    /**
     * Decodes a Base10-encoded string into its original binary representation as a ByteArray.
     *
     * The method processes the input string by removing spaces and interpreting each
     * three-digit decimal chunk as a Byte value. It validates the input string to ensure
     * it conforms to the Base10 encoding rules before decoding.
     *
     * @param data The Base10-encoded string to decode. It must contain only valid decimal
     *             characters ('0'-'9') and have a length that is a multiple of 3.
     * @return A ByteArray containing the decoded binary data, where each chunk is
     *         converted into its corresponding Byte value.
     * @throws IllegalArgumentException If the input string violates the Base10 encoding constraints.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: String): ByteArray {
        val withoutSpaces = data.replace(" ", "")
        validateBase10String(withoutSpaces)
        return withoutSpaces.chunked(3).map { chunk ->
            chunk.toInt().toByte() // Handles signed conversion (e.g., 255 → -1)
        }.toByteArray()
    }

    /**
     * Decodes an encoded ByteArray back into its original form.
     *
     * This method interprets the provided encoded ByteArray as a UTF-8 encoded string,
     * validates the string representation to ensure it is in a valid format, and then
     * decodes it back into its original binary data as a ByteArray.
     *
     * @param data The encoded ByteArray to be decoded. It is expected to represent a valid
     *             Base10-encoded numeric string when decoded to a string.
     * @return A ByteArray containing the decoded binary data.
     *         If the input array is empty, an empty ByteArray is returned.
     * @throws IllegalArgumentException If the UTF-8 string representation of the input
     *         ByteArray does not comply with the expected format.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: ByteArray): ByteArray {
        return decode(data.decodeToString())
    }

    /**
     * Validates that a given string represents a correctly formatted Base10 encoded sequence.
     *
     * This method ensures that:
     * - The length of the string is a multiple of 3.
     * - The string contains only valid decimal characters ('0'-'9').
     * - Each group of three digits (chunk) represents a number within the byte range (0-255).
     *
     * @param data The input string to be validated. It must adhere to the following constraints:
     *             - Length must be a multiple of 3.
     *             - Must contain only valid decimal characters ('0'-'9').
     *             - Each three-digit chunk must represent a number in the range 0 to 255.
     * @throws IllegalArgumentException If the string length is not a multiple of 3.
     * @throws IllegalArgumentException If the string contains characters other than '0'-'9'.
     * @throws IllegalArgumentException If any three-digit chunk exceeds the range 0-255.
     */
    @Throws(IllegalArgumentException::class)
    private fun validateBase10String(data: String) {
        // Length check
        require(data.length % 3 == 0) {
            "Invalid Base10: Length must be multiple of 3 (got ${data.length})"
        }

        // Character check
        val invalidChars = data.filter { it !in '0'..'9' }
        require(invalidChars.isEmpty()) {
            val uniqueInvalid = invalidChars.toSet().joinToString { "'$it'" }
            "Invalid Base10: Contains illegal characters: $uniqueInvalid"
        }

        // Value range check (0-255 per chunk)
        data.chunked(3).forEach { chunk ->
            val value = chunk.toInt()
            require(value in 0..255) {
                "Invalid Base10: Chunk '$chunk' exceeds byte range (0-255)"
            }
        }
    }
}
