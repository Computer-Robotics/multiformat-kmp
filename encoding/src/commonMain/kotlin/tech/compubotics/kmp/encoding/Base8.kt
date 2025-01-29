package tech.compubotics.kmp.encoding

/**
 * An object that implements the `Coder` interface providing encoding and decoding
 * operations using Base8 (octal) encoding format.
 *
 * This implementation converts binary data to a string or byte array representation
 * using octal encoding and supports reversing the process through decoding methods.
 */
object Base8 : Coder {
    /**
     * Encodes the given binary data into a transformed ByteArray representation.
     *
     * This method converts the input ByteArray into an octal-encoded string
     * representation using the `encodeToString` method, and then encodes
     * the resulting string into a ByteArray.
     *
     * @param data The input ByteArray to be encoded.
     * @return A ByteArray containing the encoded representation of the input data.
     */
    override fun encode(data: ByteArray): ByteArray {
        return encodeToString(data).encodeToByteArray()
    }

    /**
     * Encodes the given binary data into a string representation using octal encoding.
     *
     * This method converts each byte in the input ByteArray into an unsigned integer
     * and formats it as a 3-digit octal representation (base 8). The octal values
     * are concatenated into a single string without any separators.
     *
     * @param data The input ByteArray to be encoded. Each byte will be transformed
     *             into a 3-character octal string.
     * @return A String representing the octal-encoded form of the input data.
     *         Returns an empty string if the input ByteArray is empty.
     */
    override fun encodeToString(data: ByteArray): String {
        return data.joinToString("") { byte ->
            // Convert byte to unsigned integer (0-255), then to 3-digit octal
            val unsignedValue = byte.toInt() and 0xFF
            unsignedValue.toString(8).padStart(3, '0')
        }
    }

    /**
     * Decodes a Base8-encoded string into its original binary representation as a ByteArray.
     *
     * This method processes the input string by interpreting each 3-character octal chunk
     * into its corresponding Byte value. It validates the input to ensure it adheres to
     * the Base8 format before decoding.
     *
     * @param data The Base8-encoded string to decode. It must contain only valid octal characters
     *             ('0' to '7') and have a length that is a multiple of 3.
     * @return A ByteArray containing the decoded binary data.
     * @throws IllegalArgumentException If the input string is not a valid Base8 representation.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: String): ByteArray {
        val withoutSpaces = data.replace(" ", "")
        validateBase8String(withoutSpaces)
        return withoutSpaces.chunked(3).map { octalChunk ->
            octalChunk.toInt(8).toByte()
        }.toByteArray()
    }

    /**
     * Decodes an encoded ByteArray back into its original form.
     *
     * This method processes the input ByteArray by interpreting its content
     * as an encoded string and then converts it back to its binary representation.
     * The input is validated to ensure it conforms to the expected format for
     * the specific encoding scheme.
     *
     * @param data The encoded ByteArray to be decoded. It must conform to the expected
     *             format of the encoding scheme.
     * @return A ByteArray containing the original binary data after decoding.
     * @throws IllegalArgumentException If the input ByteArray does not represent
     *                                  a valid encoded format.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: ByteArray): ByteArray {
        return decode(data.decodeToString())
    }

    /**
     * Validates that the given string represents a valid Base8-encoded sequence.
     *
     * The method ensures that:
     * - The length of the string is a multiple of 3.
     * - The string contains only valid octal characters ('0' to '7').
     *
     * @param data The input string to be validated as a Base8 sequence.
     *             The string must meet the following requirements:
     *             - Length must be a multiple of 3.
     *             - Only characters in the range '0' to '7' are allowed.
     * @throws IllegalArgumentException If the string length is not a multiple of 3.
     * @throws IllegalArgumentException If the string contains characters outside the range '0' to '7'.
     */
    @Throws(IllegalArgumentException::class)
    private fun validateBase8String(data: String) {
        require(data.length % 3 == 0) {
            "Invalid Base8: Length must be multiple of 3 (got ${data.length})"
        }

        val invalidChars = data.filter { it !in '0'..'7' }
        require(invalidChars.isEmpty()) {
            val uniqueInvalid = invalidChars.toSet().joinToString { "'$it'" }
            "Invalid Base8: Contains illegal characters: $uniqueInvalid"
        }
    }
}
