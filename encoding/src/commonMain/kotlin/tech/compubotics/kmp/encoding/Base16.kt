package tech.compubotics.kmp.encoding

/**
 * A sealed class representing a Base16 encoder/decoder.
 *
 * This class provides the functionality to encode and decode data using
 * a Base16 (hexadecimal) encoding scheme. The class is abstract and
 * requires derived classes to define the specific character set for hex
 * digits (`hexChars`).
 *
 * The Base16 encoding scheme represents binary data by encoding it into
 * a string format consisting of hexadecimal characters (0-9 and A-F or a-f).
 * Each byte of data is split into two 4-bit nibbles, which are then converted
 * into their corresponding hex characters.
 *
 * The following encodings are supported:
 * - Encoding raw `ByteArray` into Base16-encoded string or `ByteArray`.
 * - Decoding Base16-encoded string or `ByteArray` back into raw binary data.
 *
 * This class also validates the Base16 format for input strings during decoding to
 * ensure they are well-formed and only contain valid characters.
 *
 * The methods provided are:
 * - `encode`: Converts raw binary data (`ByteArray`) into Base16-encoded `ByteArray`.
 * - `encodeToString`: Encodes binary data into a Base16 string representation.
 * - `decode`: Converts Base16-encoded `String` or `ByteArray` back into its binary form.
 */
sealed class Base16Base : Coder {
    /**
     * Represents a string of hexadecimal characters used as the base for encoding
     * and decoding operations in Base16 (hexadecimal) encoding. This value defines
     * the character set used during the transformation between raw binary data and
     * its encoded hexadecimal string representation.
     */
    protected abstract val hexChars: String

    /**
     * Encodes the given binary data into a Base16-encoded byte array representation.
     *
     * This method utilizes the `encodeToString` function to convert the input binary data
     * into a Base16 string, then encodes that string into a byte array.
     *
     * @param data The input ByteArray to be encoded.
     * @return A ByteArray containing the Base16-encoded representation of the input data.
     */
    override fun encode(data: ByteArray): ByteArray {
        return encodeToString(data).encodeToByteArray()
    }

    /**
     * Encodes the given binary data into a Base16-encoded string representation.
     *
     * This method processes each byte of the input ByteArray, converting it into a two-character
     * hexadecimal representation. The resulting characters are concatenated into a single string.
     *
     * @param data The input ByteArray to be encoded into a Base16 string.
     * @return A String containing the Base16-encoded representation of the input binary data.
     */
    override fun encodeToString(data: ByteArray): String {
        return data.joinToString("") { byte ->
            val high = (byte.toInt() shr 4) and 0x0F
            val low = byte.toInt() and 0x0F
            "${hexChars[high]}${hexChars[low]}"
        }
    }

    /**
     * Decodes a Base16-encoded string into its original binary representation as a ByteArray.
     *
     * This method processes the input string by interpreting each pair of hexadecimal characters
     * into their corresponding byte value. It validates the input to ensure it adheres to the Base16
     * format before decoding.
     *
     * @param data The Base16-encoded string to decode. It must contain only valid hexadecimal characters
     *             and have an even length.
     * @return A ByteArray containing the decoded binary data.
     * @throws IllegalArgumentException If the input string is not a valid Base16 representation.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: String): ByteArray {
        validateBase16String(data)
        return data.chunked(2).map { pair ->
            val high = hexChars.indexOf(pair[0]).takeIf { it != -1 } ?: throw IllegalArgumentException("Invalid character '${pair[0]}'")
            val low = hexChars.indexOf(pair[1]).takeIf { it != -1 } ?: throw IllegalArgumentException("Invalid character '${pair[1]}'")
            ((high shl 4) or low).toByte()
        }.toByteArray()
    }

    /**
     * Decodes a Base16-encoded ByteArray into its original binary representation as a ByteArray.
     *
     * This method utilizes the `decodeToString` function to convert the input ByteArray
     * into a string, which is then decoded back into the original binary representation.
     *
     * @param data The Base16-encoded ByteArray to decode. The ByteArray must represent a
     *             valid Base16 string when decoded to a string.
     * @return A ByteArray containing the decoded binary data.
     * @throws IllegalArgumentException If the input ByteArray does not represent a valid
     *                                  Base16-encoded string.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: ByteArray): ByteArray {
        return decode(data.decodeToString())
    }

    /**
     * Decodes a Base16-encoded string into its original binary representation as a ByteArray.
     *
     * This override ensures the input string is normalized to lowercase before decoding.
     *
     * @param data A Base16-encoded string to decode. The input string must have an even
     *             length and only contain valid hexadecimal characters (0-9, a-f).
     * @return A ByteArray containing the decoded binary data.
     * @throws IllegalArgumentException If the input string is not a valid Base16 representation.
     */
    @Throws(IllegalArgumentException::class)
    fun decodeCaseInsensitive(data: String): ByteArray {
        val normalizedData = data.lowercase()
        return decode(normalizedData)
    }

    /**
     * Validates that the given string is a correctly formatted Base16 (hexadecimal) representation.
     *
     * This method ensures the following conditions:
     * - The input string length must be even.
     * - The string may only contain valid hexadecimal characters (0-9, A-F, a-f).
     *
     * @param data The input string to be validated as a Base16-encoded string.
     *             The string must meet the following constraints:
     *             - Must have an even length.
     *             - Must contain only valid hexadecimal characters.
     * @throws IllegalArgumentException If the string length is not even or contains invalid characters.
     */
    @Throws(IllegalArgumentException::class)
    private fun validateBase16String(data: String) {
        require(data.length % 2 == 0) {
            "Invalid Base16: Length must be even (got ${data.length})"
        }

        val invalidChars = data.filter { it !in hexChars }
        require(invalidChars.isEmpty()) {
            val uniqueInvalid = invalidChars.toSet().joinToString { "'$it'" }
            "Invalid Base16: Contains illegal characters: $uniqueInvalid"
        }
    }
}

/**
 * A singleton object implementing Base16 encoding and decoding using lowercase hexadecimal characters.
 *
 * This object inherits from the `Base16Base` class and provides the specific
 * hexadecimal character set for encoding and decoding operations. The hexadecimal
 * characters used in this implementation are `0123456789abcdef` (lowercase).
 *
 * It enables data transformation between binary (ByteArray) and its Base16 representation,
 * utilizing the defined character set for consistency and compliance with the encoding
 * scheme. This object adheres to the Base16 format as defined by RFC 4648.
 */
object Base16 : Base16Base() {
    /**
     * Represents the hexadecimal characters used in encoding or decoding operations.
     *
     * This string contains all valid hexadecimal characters in lowercase, ranging
     * from `0` to `9` followed by `a` to `f`. It is typically used for converting
     * data between binary, hexadecimal, or other representations, where each
     * character corresponds to a 4-bit nibble of binary data.
     */
    override val hexChars: String = "0123456789abcdef"
}

/**
 * Represents a Base16 (hexadecimal) encoder/decoder using uppercase characters (0-9, A-F).
 *
 * This object is a specific implementation of the Base16Base class, which defines the
 * hexadecimal character set as uppercase digits and letters (0123456789ABCDEF). It supports
 * encoding and decoding operations for binary data into Base16-encoded formats and vice versa.
 *
 * The `Base16Upper` object ensures the use of uppercase hex characters for all transformations,
 * adhering to a standard formatting convention for Base16 encoding.
 */
object Base16Upper : Base16Base() {
    /**
     * A string containing hexadecimal character constants used for encoding and decoding operations.
     *
     * The characters represent the hexadecimal digits from 0 to F:
     * - '0'-'9': Represent decimal values 0 through 9.
     * - 'A'-'F': Represent hexadecimal values 10 through 15.
     *
     * This constant is typically used in encoding and decoding algorithms that require mapping
     * binary data or numeric values to their hexadecimal text representations and vice versa.
     */
    override val hexChars: String = "0123456789ABCDEF"
}

/**
 * A Base16 decoder that handles both uppercase and lowercase hexadecimal strings.
 *
 * This object extends [Base16Base] and overrides the decoding process to convert the input
 * string to lowercase before validation and decoding. This allows it to accept both uppercase
 * and lowercase Base16-encoded strings.
 */
object Base16CaseInsensitive : Base16Base() {
    override val hexChars: String = "0123456789abcdef"

    /**
     * Decodes a Base16-encoded string into its original binary representation as a ByteArray.
     *
     * This override ensures the input string is normalized to lowercase before decoding.
     *
     * @param data A Base16-encoded string to decode. The input string must have an even
     *             length and only contain valid hexadecimal characters (0-9, a-f).
     * @return A ByteArray containing the decoded binary data.
     * @throws IllegalArgumentException If the input string is not a valid Base16 representation.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: String): ByteArray {
        val normalizedData = data.lowercase()
        return super.decode(normalizedData)
    }
}
