package tech.compubotics.kmp.encoding

/**
 * Base45 is an implementation of the Coder interface for encoding and decoding
 * data using the Base45 encoding scheme. Base45 is a text encoding mechanism
 * designed to represent binary data using only 45 characters, making it
 * suitable for systems with constraints on character sets.
 *
 * The encoding process transforms binary data into a Base45-encoded string,
 * while the decoding process reverses this, restoring the original binary data.
 *
 * Key features and behaviors:
 * - Base45 uses an alphabet consisting of digits, uppercase Latin letters, and
 *   a small set of special symbols.
 * - Each group of two bytes of binary data is encoded into three Base45 characters,
 *   and any remaining single byte is encoded into two characters.
 * - The decoding process ensures that the input string adheres to the Base45 format
 *   and validates all encoded characters.
 *
 * This implementation handles encoding to and decoding from both String and ByteArray
 * representations and ensures correct grouping and padding during encoding or decoding.
 */
object Base45 : Coder {
    /**
     * A constant string representing the characters used for encoding and decoding
     * in a custom encoding scheme. The string includes digits (0-9), uppercase
     * English letters (A-Z), and a specific set of special characters
     * (`$ % * + - . / :`) required for the encoding logic.
     */
    private const val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ \$%*+-./:"

    /**
     * A constant value used as the base reference for calculations or encoding operations.
     *
     * This value is utilized in the `Base2` encoding/decoding process to establish
     * the foundational base level for operation logic.
     */
    private const val BASE = 45

    /**
     * Defines the number of bytes in a single encoding group
     * used for binary data representation within Base2 encoding.
     *
     * Each encoding group processes a fixed number of bytes,
     * contributing to the structured transformation of binary data
     * into its encoded form and vice versa. The value of this constant
     * influences how data is grouped and managed during the encoding
     * and decoding processes.
     */
    private const val GROUP_SIZE_BYTES = 2 // Number of bytes per encoding group

    /**
     * Defines the number of characters in each group for encoding operations.
     *
     * This constant specifies the size of a single encoded group, typically used to
     * segment or process binary data during encoding and decoding operations within
     * the Base2 binary representation system.
     *
     * For example, in Base2 encoding, data may be grouped into chunks to improve
     * readability, establish consistent formatting, or facilitate decoding logic. This
     * value represents the fixed number of characters per segment to enforce during such operations.
     */
    private const val GROUP_SIZE_CHARS = 3 // Number of characters per encoded group

    /**
     * Encodes the provided binary data into a byte array using a specific transformation.
     *
     * The method internally uses the `encodeToString` function to generate a string representation
     * of the input data and then converts this string into a byte array.
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
     * This method processes a ByteArray, grouping bytes where applicable,
     * and encodes them into a string using a predefined alphabet and base.
     * If the input ByteArray is empty, an empty string is returned.
     *
     * @param data The input ByteArray to be encoded.
     *             Each byte or group of bytes will be transformed into its string representation.
     * @return A String representing the encoded form of the data.
     *         Returns an empty string if the input ByteArray is empty.
     */
    override fun encodeToString(data: ByteArray): String {
        if (data.isEmpty()) return ""

        val output = StringBuilder()
        var i = 0

        while (i < data.size) {
            // Try to process full groups first
            if (i + GROUP_SIZE_BYTES <= data.size) {
                val byte1 = data[i].toInt() and 0xFF
                val byte2 = data[i + 1].toInt() and 0xFF
                val value = byte1 * 256 + byte2

                val c = value % BASE
                val temp1 = value / BASE
                val d = temp1 % BASE
                val e = temp1 / BASE

                output.append(ALPHABET[c])
                output.append(ALPHABET[d])
                output.append(ALPHABET[e])

                i += GROUP_SIZE_BYTES
            } else {
                // Handle remaining single byte
                val value = data[i].toInt() and 0xFF
                val c = value % BASE
                val d = value / BASE

                output.append(ALPHABET[c])
                output.append(ALPHABET[d])
                i += 1
            }
        }

        return output.toString()
    }

    /**
     * Decodes a Base45-encoded string into its binary representation as a ByteArray.
     *
     * This method processes the input Base45 string to reconstruct the original binary data.
     * It validates input format, ensures proper grouping, and converts Base45 encoded segments
     * into their corresponding byte representation.
     *
     * @param data The Base45-encoded string to be decoded. It must be correctly formatted
     *             according to Base45 encoding rules.
     * @return A ByteArray containing the decoded binary data.
     * @throws IllegalArgumentException If the input string is invalid or contains unsupported characters.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: String): ByteArray {
        validate(data)
        val output = mutableListOf<Byte>()
        var i = 0

        while (i < data.length) {
            // Process full groups first
            if (i + GROUP_SIZE_CHARS <= data.length) {
                val triplet = data.substring(i, i + GROUP_SIZE_CHARS)
                val c = ALPHABET.indexOf(triplet[0])
                val d = ALPHABET.indexOf(triplet[1])
                val e = ALPHABET.indexOf(triplet[2])

                requireValidIndexes(c, d, e)
                val value = c + d * BASE + e * BASE * BASE
                require(value <= 0xFFFF) { "Invalid triplet value: $value" }

                output.add((value / 256).toByte())
                output.add((value % 256).toByte())
                i += GROUP_SIZE_CHARS
            } else {
                // Handle remaining pair
                require(data.length - i == 2) { "Invalid trailing characters" }
                val pair = data.substring(i, i + 2)
                val c = ALPHABET.indexOf(pair[0])
                val d = ALPHABET.indexOf(pair[1])

                requireValidIndexes(c, d)
                val value = c + d * BASE
                require(value < 256) { "Invalid pair value: $value" }

                output.add(value.toByte())
                i += 2
            }
        }

        return output.toByteArray()
    }

    /**
     * Decodes a Base45-encoded ByteArray into its original binary representation.
     *
     * This method converts the Base45-encoded data into a String internally and then decodes it
     * into the original ByteArray. It validates the input data for proper encoding format and may
     * throw an exception if the input is invalid.
     *
     * @param data The Base45-encoded ByteArray to decode. It should represent a properly formatted
     *             Base45 encoded binary sequence.
     * @return A ByteArray containing the decoded binary data.
     * @throws IllegalArgumentException If the input ByteArray contains invalid data or is not properly
     *                                   formatted according to Base45 encoding rules.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: ByteArray): ByteArray {
        return decode(data.decodeToString())
    }

    /**
     * Validates that all provided indexes are within the acceptable range for Base45 encoding.
     * The method ensures that each index is within the range `0` (inclusive) to `BASE` (exclusive).
     *
     * @param indexes A variable number of integer indexes to validate. Each index must fall
     *                within the range `0` to `BASE - 1`. If any index is outside this range,
     *                an `IllegalArgumentException` is thrown.
     * @throws IllegalArgumentException If one or more indexes are out of the valid range.
     */
    @Throws(IllegalArgumentException::class)
    private fun requireValidIndexes(vararg indexes: Int) {
        require(indexes.all { it in 0..<BASE }) {
            "Contains invalid Base45 characters"
        }
    }

    /**
     * Validates a given string to ensure it adheres to specific requirements
     * for Base45 encoding.
     *
     * @param data The string to validate. The input must either have a length of 0,
     *             2, or a multiple of 3, and must only consist of valid Base45
     *             characters defined in the internal `ALPHABET`.
     * @throws IllegalArgumentException If the input string length is not 0, 2,
     *                                  or a multiple of 3, or if it contains
     *                                  characters not defined in `ALPHABET`.
     */
    @Throws(IllegalArgumentException::class)
    private fun validate(data: String) {
        require(data.length.let { it == 0 || it % 3 == 0 || it % 3 == 2 }) {
            "Invalid Base45 length: ${data.length}. Must be 0, 2, or multiple of 3"
        }
        require(data.all { it in ALPHABET }) {
            val invalid = data.filter { it !in ALPHABET }.toSet()
            "Invalid characters: $invalid"
        }
    }
}
