package tech.compubotics.kmp.encoding

/**
 * Abstract base class for encoding and decoding data using the Base58 algorithm.
 *
 * This class provides a foundation for creating specific Base58 encoders and decoders.
 * It includes methods to encode raw data to Base58 format and to decode Base58 formatted
 * data back to its original binary form. The implementation also handles
 * data normalization such as managing leading zeros in the input and output.
 *
 * Implementations of this class must define their own specific alphabet and the zero
 * character, which are used to encode and decode the data.
 *
 * Core functionality provided by this class includes:
 * - Encoding binary data into a Base58-encoded string or byte array.
 * - Decoding a Base58-encoded string or byte array back into its original binary form.
 * - Validation of input strings to ensure they contain only characters defined in the alphabet.
 */
sealed class Base58Base : Coder {
    /**
     * The `alphabet` property defines the set of valid characters used for encoding and decoding
     * in the Base58Base implementation. Each character in the string represents a unique value
     * in the Base58 encoding scheme and corresponds to its position in the encoding index.
     *
     * The characters in `alphabet` must be unique and follow the Base58 specifications,
     * typically excluding characters that are visually similar or ambiguous (e.g., '0', 'O', 'l', 'I').
     *
     * This property is immutable and serves as the basis for converting binary data into a human-readable
     * Base58-encoded string representation and vice versa.
     */
    protected abstract val alphabet: String

    /**
     * Represents the character used to denote the "zero" value in the encoding scheme.
     *
     * This character is essential for identifying and handling the most significant
     * concept of zero in the corresponding encoding or decoding logic. It is commonly
     * used to pad or initialize encoded data structures or process zero-equivalent values.
     */
    protected abstract val zeroChar: Char

    /**
     * Encodes the given binary data into a byte array representation using the Base58 encoding scheme.
     *
     * This method transforms the input ByteArray by converting it into a Base58-encoded string
     * using the `encodeToString` function. The resulting string is then converted back
     * into a ByteArray using UTF-8 encoding.
     *
     * @param data The input ByteArray to be encoded.
     * @return A ByteArray containing the Base58-encoded representation of the input data.
     */
    override fun encode(data: ByteArray): ByteArray {
        return encodeToString(data).encodeToByteArray()
    }

    /**
     * Encodes the given binary data into a Base58-encoded string representation.
     *
     * This method processes the input ByteArray by removing leading zero bytes and
     * converting the remaining data into a Base58-encoded string. The encoding involves
     * mapping the data into the Base58 alphabet. For input data that is empty,
     * an empty string is returned.
     *
     * @param data The binary data to encode as a ByteArray.
     *             Each byte in the array is part of the input for Base58 transformation.
     * @return A String containing the Base58-encoded representation of the input data.
     *         Returns an empty string if the input array is empty.
     */
    override fun encodeToString(data: ByteArray): String {
        if (data.isEmpty()) return ""

        // Count leading zeros
        var leadingZeros = 0
        while (leadingZeros < data.size && data[leadingZeros] == 0.toByte()) {
            leadingZeros++
        }

        // Convert to BigNum (automatically strips leading zeros)
        val number = BigNum.fromBytes(data.copyOfRange(leadingZeros, data.size))

        val digits = mutableListOf<Int>()
        var current = number
        while (!current.isZero()) {
            val (quotient, remainder) = current.divide(BASE)
            digits.add(remainder)
            current = quotient
        }

        return buildString {
            repeat(leadingZeros) { append(zeroChar) }
            digits.reversed().forEach { append(alphabet[it]) }
        }
    }

    /**
     * Decodes a Base58-encoded string into its original binary data representation.
     *
     * The method transforms the input Base58 string into a ByteArray by interpreting its characters
     * as Base58 digits. Leading zero-like characters are treated as leading zeros in the binary data.
     * The method validates the input to ensure all characters belong to the Base58 alphabet.
     *
     * @param data The Base58-encoded input string to be decoded.
     *             It is expected to only contain characters from the Base58 alphabet.
     * @return A ByteArray representing the decoded binary data.
     *         Leading zero bytes in the binary data are preserved in the output.
     * @throws IllegalArgumentException If the input contains characters not in the Base58 alphabet.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: String): ByteArray {
        validate(data)

        var leadingOnes = 0
        while (leadingOnes < data.length && data[leadingOnes] == zeroChar) {
            leadingOnes++
        }

        val digits =
            data.drop(leadingOnes).map { char ->
                alphabet.indexOf(char).also { index ->
                    require(index != -1) { "Invalid character: $char" }
                }
            }

        var number = BigNum()
        digits.forEach { digit ->
            number = number.multiply(BASE).add(digit)
        }

        return ByteArray(leadingOnes) { 0 } + number.toByteArray()
    }

    /**
     * Decodes a Base58-encoded byte array into its original byte array representation.
     *
     * This method interprets the input ByteArray as a string encoded in UTF-8, then
     * decodes the resulting Base58-encoded string to retrieve the original binary data.
     * Invalid Base58 characters in the input cause an IllegalArgumentException to be thrown.
     *
     * @param data The Base58-encoded binary data as a ByteArray. It is expected to contain
     *             a UTF-8 encoded representation of a Base58-encoded string.
     * @return A ByteArray containing the decoded binary representation of the input data.
     * @throws IllegalArgumentException If the decoded ByteArray does not represent a valid Base58 format.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: ByteArray): ByteArray {
        return decode(data.decodeToString())
    }

    /**
     * Validates that the provided string contains only characters from the Base58 alphabet.
     *
     * This method checks if all characters in the input string are present in the predefined
     * Base58 alphabet. It throws an exception if invalid characters are found.
     *
     * @param data The input string to be validated. Each character in the string is checked
     *             against the allowed Base58 alphabet.
     * @throws IllegalArgumentException If the string contains characters not present in the Base58 alphabet.
     */
    @Throws(IllegalArgumentException::class)
    private fun validate(data: String) {
        require(data.all { it in alphabet }) {
            "Contains invalid Base58 characters"
        }
    }

    /**
     * Provides constant values and utility functions specific to the Base58 encoding scheme.
     * This companion object is used internally by the `Base58Base` class to support encoding
     * and decoding operations.
     */
    companion object {
        /**
         * Represents the base value used in Base58 encoding and decoding processes.
         *
         * This constant defines the total number of distinct characters available
         * in the Base58 encoding alphabet. It is a critical parameter used in the
         * implementation of Base58 encoding and decoding algorithms within the
         * `Base58Base` class.
         */
        private const val BASE = 58
    }
}

/**
 * An implementation of the Base58 encoding algorithm specifically tailored for Bitcoin's encoding requirements.
 *
 * This object extends the functionality of the `Base58Base` class, utilizing Bitcoin's specific Base58 alphabet
 * that excludes characters which can be visually ambiguous (e.g., '0', 'O', 'l', 'I'), improving human readability.
 *
 * - `alphabet` defines the set of 58 characters used in Bitcoin's Base58 encoding scheme.
 * - `zeroChar` is the first character of the alphabet, representing zero in the encoded data.
 *
 * This implementation is commonly used for encoding and decoding Bitcoin addresses and other related data.
 */
object Base58Bitcoin : Base58Base() {
    /**
     * Represents the character set used for encoding and decoding operations.
     *
     * This is a base-58 alphabet containing numbers, uppercase letters (excluding 'I' and 'O'),
     * and lowercase letters (excluding 'l'). It is specifically designed to avoid characters
     * that could be easily confused with one another in different contexts (e.g., visually or
     * during manual transcription).
     *
     * The alphabet is most commonly used in Base58 encoding schemes, ensuring a compact
     * representation of binary data while maintaining readability and reducing the likelihood
     * of transcription errors.
     */
    override val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

    /**
     * Represents the character used to denote the "zero" value in the encoding scheme.
     *
     * This value is derived from the first character of the `alphabet`, which defines
     * the valid characters used in the encoding process. It acts as a placeholder
     * for binary `0` in the Base2 encoding representation.
     */
    override val zeroChar: Char = alphabet[0]
}

/**
 * An implementation of the Base58 encoding and decoding algorithm using the Flickr Base58 alphabet.
 *
 * The `Base58Flickr` object extends the functionality of the abstract `Base58Base` class by
 * defining a specific alphabet for the Base58 encoding scheme. The alphabet used by this
 * implementation excludes similar-looking characters and is tailored for scenarios requiring
 * the Flickr-specific Base58 encoding standard.
 *
 * Features:
 * - Defines the Flickr Base58 alphabet consisting of 58 distinct characters.
 * - Specifies the character used to represent leading zeroes during encoding.
 */
object Base58Flickr : Base58Base() {
    /**
     * The `alphabet` property defines the set of characters used for encoding in a Base58-like format.
     *
     * This property excludes easily confusable characters such as '0', 'O', 'I', and 'l' to reduce
     * the likelihood of misinterpretation. It includes digits 1-9, lowercase letters excluding 'l',
     * and uppercase letters excluding 'I' and 'O'.
     *
     * The ordering of characters in the `alphabet` is significant for encoding and decoding purposes.
     */
    override val alphabet = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"

    /**
     * Represents the character that denotes the binary digit zero ('0') in the Base2 encoding scheme.
     *
     * This character is used as the default representation of a binary zero value.
     * It is typically the first character in the encoding `alphabet` for Base2 operations.
     */
    override val zeroChar: Char = alphabet[0]
}
