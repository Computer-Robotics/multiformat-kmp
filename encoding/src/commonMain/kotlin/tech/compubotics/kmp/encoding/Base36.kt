package tech.compubotics.kmp.encoding

/**
 * Represents a base representation of Base36 encoding and decoding logic.
 * This sealed class provides a common structure for encoding and decoding
 * operations in Base36 format using a specified alphabet of 36 characters.
 *
 * Subclasses of this sealed class are expected to define the specific Base36
 * alphabet to be used, which can include a mix of digits and letters.
 *
 * The implementation handles converting data between a `ByteArray` and its
 * Base36 representation, as well as validating the data's compatibility
 * with the Base36 character set being used.
 */
sealed class Base36Base : Coder {
    /**
     * Defines the character set used for encoding and decoding operations in the Base36 encoding implementation.
     *
     * The `alphabet` is an abstract property that specifies the valid symbols for the encoding. It consists of
     * 36 characters, typically including digits (0-9) and letters (a-z or A-Z). The concrete value of this property
     * must be provided by subclasses implementing the `Base36Base` class.
     *
     * The choice of characters in the `alphabet` affects the encoding process and should ensure that all values
     * can be uniquely represented and decoded. Subclasses may customize the `alphabet` to fit specific encoding
     * needs or constraints.
     */
    protected abstract val alphabet: String // 36 characters (0-9, a-z or A-Z)

    /**
     * Encodes the given binary data into a transformed byte array representation.
     *
     * This method converts the input data into a string representation using the `encodeToString` method,
     * then encodes the resulting string into a byte array.
     *
     * @param data The input ByteArray to be encoded.
     * @return A ByteArray containing the encoded representation of the input data.
     */
    override fun encode(data: ByteArray): ByteArray {
        return encodeToString(data).encodeToByteArray()
    }

    /**
     * Encodes the given binary data into a Base36-encoded string representation.
     *
     * This method converts the input ByteArray into an unsigned integer list and
     * performs a division-based encoding process to represent the data in the
     * Base36 format using the defined `alphabet`. The resulting encoded string
     * does not have padding and will represent the numerical interpretation of
     * the data. If the input ByteArray is empty, an empty string is returned.
     *
     * @param data The input ByteArray to be encoded. Each byte is treated as part
     *             of a binary number to compute its Base36 representation.
     * @return A Base36-encoded string that represents the input ByteArray. Returns
     *         an empty string if the input ByteArray is empty.
     */
    override fun encodeToString(data: ByteArray): String {
        if (data.isEmpty()) return ""
        val number = data.toUnsignedIntList()
        if (number.isZero()) return "0"

        val remainders = mutableListOf<Int>()
        var current = number
        while (!current.isZero()) {
            val (quotient, remainder) = current.divide(BASE)
            remainders.add(remainder)
            current = quotient
        }
        return remainders.reversed().map { alphabet[it] }.joinToString("")
    }

    /**
     * Decodes a Base36-encoded string into its binary representation as a ByteArray.
     *
     * This method transforms the input string by mapping its characters to corresponding
     * indices in the predefined Base36 alphabet, then performs calculations to convert
     * the resulting sequence of indices into the original binary data. If the input string
     * is empty, the method returns an empty ByteArray.
     *
     * @param data The Base36-encoded string to decode. Must only contain characters from the defined alphabet.
     * @return A ByteArray containing the decoded binary data.
     *         Returns an empty ByteArray if the input string is empty.
     */
    override fun decode(data: String): ByteArray {
        validate(data)
        val digits = data.map { alphabet.indexOf(it) }
        if (digits.isEmpty()) return byteArrayOf()

        var number = BigNum()
        digits.forEach { digit ->
            number = number.multiply(BASE).add(digit)
        }
        return number.toByteArray()
    }

    /**
     * Decodes a Base36-encoded ByteArray into its binary representation as a ByteArray.
     *
     * This method first converts the input ByteArray into a string representation using UTF-8 decoding
     * and then applies the `decode` method that operates on strings to retrieve the original binary data.
     *
     * @param data The Base36-encoded ByteArray to decode. The ByteArray must represent a valid encoded string
     *             in the Base36 format.
     * @return A ByteArray containing the decoded binary data.
     *         Returns an empty ByteArray if the input ByteArray is empty or decodes to an empty string.
     */
    override fun decode(data: ByteArray): ByteArray {
        return decode(data.decodeToString())
    }

    /**
     * Validates that the given string contains only valid characters from the defined alphabet.
     *
     * The method checks each character in the input string to ensure it exists within the predefined
     * alphabet. If any invalid characters are found, an exception is thrown listing the invalid characters.
     *
     * @param data The input string to be validated. This string must exclusively
     *             contain characters from the allowed alphabet.
     * @throws IllegalArgumentException If the input string contains characters not
     *                                  present in the defined alphabet.
     */
    private fun validate(data: String) {
        require(data.all { it in alphabet }) {
            val invalid = data.filter { it !in alphabet }.toSet()
            "Invalid Base36 characters: ${invalid.joinToString()}"
        }
    }

    /**
     * Converts the current ByteArray into a BigNum representation by interpreting each byte as an
     * unsigned integer and removing any leading zero bytes. The resulting BigNum contains
     * a list of integers, where each integer corresponds to the unsigned value of the respective byte.
     *
     * @return A BigNum object containing a list of integers representing the unsigned values of the bytes
     *         in the ByteArray, with leading zero bytes excluded.
     */
    private fun ByteArray.toUnsignedIntList(): BigNum {
        val bytes = this.dropWhile { it == 0.toByte() }
        return BigNum(bytes.map { it.toInt() and 0xFF })
    }

    companion object {
        /**
         * Defines the base value used for encoding and decoding operations in the Base36Base class.
         *
         * The `base` variable specifies the radix or numerical base to be utilized during encoding
         * and decoding processes. In this implementation, the base is set to 36, supporting alphanumeric
         * encoding schemes where inputs are converted to and from a string containing digits (0-9)
         * and letters (A-Z). This value is a fundamental aspect of Base36 encoding logic.
         */
        private const val BASE = 36
    }
}

/**
 * An object representing the Base36 encoding and decoding system.
 *
 * Base36 is a positional numeral system using 36 as its base. It includes
 * digits 0-9 and lowercase letters a-z, which means this system is case-insensitive.
 *
 * This implementation provides the ability to encode and decode data
 * using the Base36 alphabet.
 *
 * The `alphabet` property defines the characters used in this Base36 implementation.
 * It follows the standard order of characters: 0-9 followed by a-z.
 *
 * This object inherits from the `Base36Base` class and overrides the `alphabet`
 * to provide the specific character set for Base36 encoding/decoding.
 */
object Base36 : Base36Base() {
    /**
     * A string representing the set of valid characters used for encoding or decoding
     * in a specific numeral system. The `alphabet` defines the characters permitted
     * for representing values, such as digits and lowercase letters.
     *
     * In this case, the `alphabet` contains:
     * - Decimal digits ('0' to '9')
     * - Lowercase English letters ('a' to 'z')
     *
     * This property can be used to encode integers or binary data into an alphanumeric
     * representation, or decode such representations back into their original form.
     */
    override val alphabet = "0123456789abcdefghijklmnopqrstuvwxyz"
}

/**
 * An object that extends the Base36Base class and provides functionality for encoding and decoding
 * using the Base36 numeral system, restricted to uppercase alphabets.
 *
 * This implementation uses a fixed alphabet consisting of digits 0-9 and uppercase letters A-Z.
 */
object Base36Upper : Base36Base() {
    /**
     * Defines the set of characters used for encoding and decoding operations.
     * This property represents an immutable collection of alphanumeric characters
     * and digits, starting from '0' to '9' followed by uppercase English letters
     * 'A' to 'Z', inclusive.
     *
     * Typically used in base encoding schemes to map numeric values to their
     * corresponding character representations.
     */
    override val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
}

/**
 * Provides Base36 encoding and decoding functionality using a case-insensitive alphabet.
 *
 * This object subclass implements the `Base36Base` abstract class and overrides the
 * alphabet to be case-insensitive by defining it as "0123456789abcdefghijklmnopqrstuvwxyz".
 * Additionally, the `decode` method ensures the input string is normalized to lowercase
 * before decoding to handle mixed-case inputs.
 */
object Base36CaseInsensitive : Base36Base() {
    /**
     * Defines the character set used for encoding and decoding operations.
     *
     * The `alphabet` variable contains a sequence of alphanumeric characters
     * (digits 0–9 followed by lowercase letters a–z) which serve as the encoding
     * basis for specific implementations. The sequence is immutable and determines
     * the encoding scheme, providing a mapping between numeric values and their
     * corresponding character representations.
     */
    override val alphabet = "0123456789abcdefghijklmnopqrstuvwxyz"

    /**
     * Decodes a given Base36-encoded string into its binary representation as a ByteArray.
     *
     * This overridden implementation normalizes the input string by converting it to lowercase
     * before delegating the decoding operation to the parent implementation. The normalized string
     * ensures consistency when handling mixed-case inputs in the Base36 format.
     *
     * @param data The Base36-encoded string to decode. The string is case-insensitive and will
     *             be normalized to lowercase before decoding.
     * @return A ByteArray containing the decoded binary data. Returns an empty ByteArray if
     *         the input string is empty.
     */
    override fun decode(data: String): ByteArray {
        val normalized = data.lowercase()
        return super.decode(normalized)
    }
}
