package tech.compubotics.kmp.encoding

/**
 * Base32Base is an abstract sealed class providing the core functionality
 * for encoding and decoding Base32 representations, including support for
 * optional padding and variant-specific alphabets.
 *
 * This class implements the [Coder] interface and provides facilities
 * to encode binary data into Base32 strings and decode Base32 strings
 * back into byte arrays. Subclasses are expected to define the specific
 * Base32 alphabet, padding character, and whether padding is used.
 *
 * The Base32 encoding splits binary data into chunks of 5 bits and maps
 * them to characters using the specified alphabet. Padding characters can
 * be optionally added to ensure the encoded output conforms to a specific
 * length multiple.
 *
 * Properties:
 * - `alphabet`: Represents the Base32 alphabet, specifying the characters
 *   to be used during encoding and decoding.
 * - `padChar`: The padding character used in the encoding output for alignment,
 *   if `usePadding` is enabled.
 * - `usePadding`: A boolean flag indicating whether padding is applied to the
 *   encoded output to ensure length alignment.
 *
 * Methods:
 * - `encode(data: ByteArray): ByteArray`: Encodes raw binary data into Base32 representation
 *   and returns the result as a byte array.
 * - `encodeToString(data: ByteArray): String`: Encodes raw binary data into Base32 representation
 *   and returns the result as a string.
 * - `decode(data: String): ByteArray`: Decodes a Base32 encoded string back into the original
 *   byte array representation, throwing an exception for invalid characters.
 * - `decode(data: ByteArray): ByteArray`: Decodes a Base32 encoded byte array back into the
 *   original byte array representation.
 *
 * Companion Object Constants:
 * - `GROUP_BITS`: The number of bits per Base32 group, which is 5.
 * - `BYTE_BITS`: The number of bits in a standard byte, which is 8.
 * - `BASE32_CHAR_COUNT`: The number of unique characters in the Base32 alphabet, which is 32.
 *
 * Subclasses must implement the abstract properties to define the specifics
 * of the encoding/decoding behavior based on the desired Base32 variant.
 */
sealed class Base32Base : Coder {
    /**
     * Represents the character set used for encoding and decoding operations in Base32-related implementations.
     *
     * This abstract value determines the specific symbols utilized for representing encoded binary data
     * in a Base32 format. Different implementations of this property can define custom character sets,
     * enabling variations of Base32 encoding/decoding schemes.
     */
    protected abstract val alphabet: String

    /**
     * The character used as padding when encoding data.
     * This value determines the symbol appended to the encoded output
     * to ensure proper alignment or to meet specific encoding
     * length requirements, if padding is enabled.
     *
     * This property must be implemented in classes deriving from
     * this base class to define the appropriate padding character
     * for the encoding scheme in use.
     */
    protected abstract val padChar: Char

    /**
     * Indicates whether padding should be used in the encoding or decoding process.
     *
     * This property determines if padding characters are included when encoding
     * data and if they are expected during decoding. If `true`, padding is enabled,
     * and encodings will include a padding character (e.g., '=' for Base32/64) to
     * ensure a complete block of encoded data. If `false`, padding is not used.
     *
     * This configuration is common in situations where padding might be unnecessary
     * or undesirable, such as in streaming scenarios or when the data size is always
     * known and divisible by the encoding block size.
     */
    protected abstract val usePadding: Boolean

    /**
     * Encodes the given binary data into a Base32-encoded byte array.
     *
     * This method transforms the input ByteArray into a Base32 string representation
     * using the `encodeToString` method, and then converts the resulting string into
     * a ByteArray.
     *
     * @param data The input ByteArray to be encoded.
     * @return A ByteArray containing the Base32-encoded representation of the input data.
     */
    override fun encode(data: ByteArray): ByteArray {
        return encodeToString(data).encodeToByteArray()
    }

    /**
     * Encodes the given binary data into a Base32-encoded string representation.
     *
     * This method uses a custom alphabet, applying Base32 encoding to the input
     * ByteArray. The resulting string may include optional padding based on
     * specified parameters.
     *
     * @param data The input ByteArray to be encoded. Each byte will be processed
     *             to produce a corresponding Base32-encoded character sequence.
     * @return A String containing the Base32-encoded representation of the input
     *         data. Returns an empty string if the input ByteArray is empty.
     */
    override fun encodeToString(data: ByteArray): String {
        if (data.isEmpty()) return ""

        val output = StringBuilder()
        var buffer = 0
        var bitsLeft = 0
        var paddingCount = 0

        for (b in data) {
            buffer = (buffer shl BYTE_BITS) or (b.toInt() and 0xFF)
            bitsLeft += BYTE_BITS

            while (bitsLeft >= GROUP_BITS) {
                bitsLeft -= GROUP_BITS
                val index = (buffer shr bitsLeft) and 0x1F
                output.append(alphabet[index])
            }
        }

        if (bitsLeft > 0) {
            val index = (buffer shl (GROUP_BITS - bitsLeft)) and 0x1F
            output.append(alphabet[index])
            paddingCount =
                when (bitsLeft) {
                    1 -> 6
                    2 -> 4
                    3 -> 3
                    4 -> 1
                    else -> 0
                }
        }

        if (usePadding) {
            output.append(padChar.toString().repeat(paddingCount))
        }

        return output.toString()
    }

    /**
     * Decodes a Base32-encoded string into its original binary representation as a ByteArray.
     *
     * The method processes the input string by interpreting each Base32 character
     * according to the specified variant's alphabet. The input is normalized to the
     * required case based on the variant type and validated to ensure it contains
     * only valid Base32 characters. The padding character, if present, will be ignored.
     *
     * @param data The Base32-encoded string to decode. It must only contain valid
     *             characters defined in the current variant's alphabet or the padding character.
     * @return A ByteArray containing the decoded binary data. Returns an empty ByteArray
     *         if the input string contains no valid characters after filtering.
     * @throws IllegalArgumentException If the input string contains invalid Base32 characters.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: String): ByteArray {
        // Normalize input case based on variant type
        val normalizedData =
            when (this) {
                is Base32Hex, is Base32HexPad -> data.lowercase()
                is Base32HexUpper, is Base32HexPadUpper -> data.uppercase()
                is Base32, is Base32Pad -> data.lowercase()  // Lowercase for standard Base32
                is Base32Upper, is Base32PadUpper -> data.uppercase()
                else -> data.lowercase()
            }

        require(normalizedData.all { it == padChar || it in alphabet }) {
            "Invalid Base32 characters detected"
        }

        val input = normalizedData.filter { it != padChar }
        if (input.isEmpty()) return byteArrayOf()

        val output = ByteArray((input.length * GROUP_BITS) / BYTE_BITS)
        var buffer = 0
        var bitsStored = 0
        var outputIndex = 0

        for (c in input) {
            val value = alphabet.indexOf(c)
            require(value != -1) { "Invalid character: $c" }

            buffer = (buffer shl GROUP_BITS) or value
            bitsStored += GROUP_BITS

            if (bitsStored >= BYTE_BITS) {
                bitsStored -= BYTE_BITS
                output[outputIndex++] = (buffer shr bitsStored).toByte()
                buffer = buffer and ((1 shl bitsStored) - 1)
            }
        }

        return output
    }

    /**
     * Decodes a Base32-encoded byte array into its original binary representation as a ByteArray.
     *
     * This method transforms the input ByteArray into a Base32-encoded string, then processes
     * the string by interpreting each Base32 character according to the specified variant's
     * alphabet. The input is normalized to the required case and validated to ensure it contains
     * only valid Base32 characters. The padding character, if present, will be ignored.
     *
     * @param data The Base32-encoded byte array to decode. The byte array will be converted into
     *             a string representation for further processing. It must only contain valid
     *             ASCII characters that map to the appropriate Base32 alphabet.
     * @return A ByteArray containing the decoded binary data. Returns an empty ByteArray
     *         if the input string contains no valid characters after filtering.
     * @throws IllegalArgumentException If the input string contains invalid Base32 characters
     * derived from the ByteArray.
     */
    @Throws(IllegalArgumentException::class)
    override fun decode(data: ByteArray): ByteArray {
        return decode(data.decodeToString())
    }

    /**
     * A companion object containing constants used in the Base32Base implementation.
     *
     * These constants define bit manipulation properties and character set information
     * specific to Base32 encoding and decoding processes.
     */
    protected companion object {
        /**
         * Defines the number of bits in a group used for Base32 encoding.
         *
         * This constant specifies the size of the bit groups that are processed
         * during Base32 encoding and decoding operations. Each group contains
         * 5 bits, which corresponds to the Base32 alphabet's 32 unique characters.
         *
         * It serves as a fundamental parameter for the encoding and decoding
         * algorithms, ensuring proper extraction and interpretation of data
         * within the Base32 encoding scheme.
         */
        const val GROUP_BITS = 5

        /**
         * The number of bits in a single byte.
         *
         * This constant is used to define the base unit of binary data representation,
         * which is commonly 8 bits per byte. It serves as a standard value in various
         * encoding and decoding operations, including Base32 implementation, where data
         * is often processed at the byte level.
         */
        const val BYTE_BITS = 8
    }
}

/**
 * Represents a sealed interface for variants of the Base32Hex encoding format.
 *
 * The `Base32HexVariant` interface is designed to define specialized behaviors
 * or configurations associated with the Base32Hex encoding standard, a variation
 * of Base32 introduced by RFC 4648. Base32Hex differs from standard Base32 in its
 * ordering of characters to enhance lexical ordering properties.
 *
 * This interface is intended for internal use and encapsulates distinct variations,
 * if any, of the Base32Hex encoding implementation.
 */
private sealed interface Base32HexVariant

/**
 * An object that implements encoding and decoding of data using the Base32Hex encoding scheme.
 * Base32Hex is a variant of Base32 that uses a custom alphabet suited for hexadecimal representation.
 *
 * The alphabet used in this implementation is: "0123456789abcdefghijklmnopqrstuv".
 * Padding is disabled (`usePadding = false`), meaning encoded output will not include padding characters.
 * The padding character is defined as '=' but remains unused due to disabled padding.
 */
object Base32Hex : Base32Base(), Base32HexVariant {
    /**
     * Represents the alphabet used for encoding and decoding in a specific base scheme.
     *
     * This alphabet contains the characters "0123456789abcdefghijklmnopqrstuv" and is
     * utilized for operations where data is encoded into or decoded from a custom numeral
     * system. The ordering of characters in this string determines the value of each
     * symbol in the respective encoding or decoding process.
     */
    override val alphabet = "0123456789abcdefghijklmnopqrstuv"

    /**
     * The character used for padding purposes during encoding operations.
     *
     * This value is typically utilized when a particular encoding scheme requires
     * padding to ensure that the encoded data maintains a consistent structure or length.
     */
    override val padChar = '='

    /**
     * Indicates whether padding should be applied during encoding or decoding operations.
     *
     * In contexts where binary data is interpreted or transformed, `usePadding` determines
     * if extra padding characters are included to align the data to a specific length
     * or format. When set to `true`, padding may be applied according to the defined
     * encoding or decoding rules. When set to `false`, padding is omitted.
     */
    override val usePadding = false
}

/**
 * An object representing the Base32Hex encoding variant with uppercase alphanumeric characters.
 *
 * This encoding scheme is a hexadecimal variant of Base32 that utilizes uppercase
 * letters in its alphabet for encoding and decoding operations. It is particularly
 * useful in contexts where case-insensitivity or standardized hexadecimal representation
 * is required.
 */
object Base32HexUpper : Base32Base(), Base32HexVariant {
    /**
     * Represents the set of characters used in the encoding or decoding process.
     *
     * This `alphabet` specifically contains uppercase alphanumeric characters (0-9, A-V)
     * and is typically utilized as the encoding character set for Base32 encoding schemes.
     * Each character in the alphabet corresponds to a specific value in the encoding process,
     * allowing for consistent translation between raw data and encoded strings.
     */
    override val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUV"

    /**
     * The character used to pad the encoded string representation.
     *
     * This padding character is utilized to ensure the encoded output
     * adheres to the expected format, aligning with specific requirements
     * of the encoding scheme. It is typically added to fill to a specific
     * length when necessary.
     */
    override val padChar = '='

    /**
     * A flag indicating whether padding should be applied during encoding or decoding.
     *
     * In the context of `Base2` operations, this property determines if additional
     * formatting, such as space-separated binary strings, should be considered.
     * By default, padding is disabled (`false`), meaning no extra spaces or padding
     * are added or expected during binary encoding/decoding operations.
     */
    override val usePadding = false
}

/**
 * Represents an implementation of the Base32Hex encoding with padding, where the encoded output uses
 * a specific character set suitable for hexadecimal representation and includes padding when applicable.
 *
 * This object adheres to the Base32 encoding rules and uses the "Extended Hex Alphabet" as the character
 * set, which consists of digits ('0'-'9') followed by lowercase alphabet letters ('a'-'v'). It also includes
 * functionality for managing optional padding in the encoded output.
 *
 * This implementation is immutable and stateless, making it safe for use in concurrent environments.
 */
object Base32HexPad : Base32Base(), Base32HexVariant {
    /**
     * The `alphabet` property defines the set of characters used for encoding and decoding operations.
     * It is a fixed string of 32 characters comprising digits ('0'-'9') followed by lowercase English
     * alphabet letters ('a'-'v'). This character set is typically used in a Base32 encoding scheme.
     *
     * This property is immutable and is used by the implementation to lookup corresponding encoded
     * or decoded values during transformations between raw binary data and their encoded representation.
     */
    override val alphabet = "0123456789abcdefghijklmnopqrstuv"

    /**
     * The `padChar` property represents the character used to pad Base2-encoded output.
     *
     * This is typically utilized during encoding to ensure that the output adheres to specific length requirements
     * or standards, by adding the `padChar` to the output when necessary. In the context of Base2 encoding, the
     * default pad character is '='.
     */
    override val padChar = '='

    /**
     * Indicates whether padding should be applied during encoding or decoding operations.
     *
     * This property determines if padding is used when encoding or decoding binary data to
     * ensure proper alignment or conformity with expected formats. Padding might be required
     * in certain scenarios, such as when the binary representation's length does not align
     * with the expected segment size (e.g., 8 bits for Base2 encoding).
     *
     * A value of `true` means padding will be added or considered where necessary, while `false`
     * indicates no padding is used.
     */
    override val usePadding = true
}

/**
 * An object representing the Base32 encoding variant with Hexadecimal alphabet,
 * using padding and uppercase characters.
 *
 * This implementation adheres to the Base32 Hexadecimal encoding specification
 * as defined in RFC 4648. It utilizes a specific alphabet in uppercase and
 * applies padding when needed.
 */
object Base32HexPadUpper : Base32Base(), Base32HexVariant {
    /**
     * The alphabet utilized for Base32 encoding and decoding operations.
     *
     * This string defines the ordered characters used to represent the encoded data
     * in Base32 format. Each character corresponds to a 5-bit binary value, with a
     * total of 32 unique symbols ranging from `0` to `9` and `A` to `V`.
     *
     * Base32 encoding transforms binary data into this human-readable alphabet,
     * ensuring that the encoded output is case-insensitive and easily transmittable
     * in textual contexts.
     *
     * The sequence of the characters in this alphabet directly influences the
     * encoding and decoding operations, and it must adhere to the standard Base32
     * specification to maintain interoperability across implementations.
     */
    override val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUV"

    /**
     * The character used for padding in data encoding or formatting operations.
     *
     * In base encoding or similar processes, padding characters are often used to
     * fill the data to meet specific alignment or length requirements. This variable
     * specifies the character used for that purpose.
     */
    override val padChar = '='

    /**
     * Indicates whether padding should be used in the Base2 encoding and decoding process.
     *
     * If `true`, padding may be applied to ensure the encoded string has a specific
     * format or alignment. This setting can influence how encoded or decoded data
     * aligns with expected binary formats and structures. When `false`, no additional
     * padding is applied, and the data is processed as is.
     */
    override val usePadding = true
}

/**
 * Object `Base32` provides functionality for encoding and decoding data using the Base32 encoding scheme.
 *
 * Base32 is a method of encoding binary data into a text representation using a specific set of characters.
 * It is often used in scenarios where a compact, reusable, and URL-safe representation of binary data is required.
 * The encoding uses a 32-character alphabet defined by the `alphabet` property and optionally applies padding
 * determined by the `usePadding` flag.
 */
object Base32 : Base32Base() {
    /**
     * Represents the character set used for encoding and decoding operations.
     *
     * This string contains the alphabetic characters 'a' through 'z' followed by
     * the numeric digits '2', '3', '4', '5', '6', and '7'. It is typically used
     * in encoding schemes where a specific set of characters is required for
     * generating the encoded output.
     */
    override val alphabet = "abcdefghijklmnopqrstuvwxyz234567"

    /**
     * The `padChar` variable specifies the character used to pad encoded binary strings in the `Base2` encoding scheme.
     *
     * This character is used when the encoded representation requires padding for alignment.
     * In the context of `Base2`, it ensures consistent formatting and compatibility with various
     * processing or decoding systems that may expect a specific padding character.
     */
    override val padChar = '='

    /**
     * A boolean flag indicating whether padding should be applied when encoding
     * binary data to its string or byte array representation.
     *
     * Padding can be used to ensure that the encoded output adheres to a specific
     * format or length requirement. When `usePadding` is `true`, padding is applied
     * to the output where necessary; when `false`, no padding is added.
     *
     * For the `Base2` implementation, padding is typically not used, as the binary
     * representation does not require additional formatting to meet length or
     * alignment constraints.
     */
    override val usePadding = false
}

/**
 * Object for Base32 encoding and decoding using an uppercase alphabet.
 *
 * This object extends the `Base32Base` class and provides functionality
 * specific to Base32 encoding and decoding using a predefined uppercase
 * alphabet and specific configuration settings.
 */
object Base32Upper : Base32Base() {
    /**
     * Represents the alphabet used for encoding in the Base32 format.
     *
     * This alphabet is a predefined string containing the 32 characters:
     * "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", which are used for Base32 encoding
     * and decoding operations. The characters consist of uppercase English letters
     * and digits 2 through 7, adhering to the Base32 standard.
     */
    override val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    /**
     * Represents the padding character used for encoding or formatting processes.
     *
     * In the context of the `Base2` object, the `padChar` is utilized to provide uniform
     * structure or alignment where necessary, depending on the encoding requirements or formatting
     * constraints of the binary data representation.
     */
    override val padChar = '='

    /**
     * Indicates whether padding should be applied when encoding or decoding
     * binary data within the `Base2` implementation.
     *
     * This property determines if additional formatting or padding operations
     * are performed during the encoding or decoding process. When `true`,
     * encoding or decoding may include padding or spacing. When `false`,
     * no padding or additional transformations are applied, resulting in a
     * more compact representation.
     *
     * By default, this property is set to `false`, meaning no padding is applied.
     */
    override val usePadding = false
}

/**
 * An implementation of Base32 encoding with padding utilizing the standardized Base32 character set
 * as defined in RFC 4648. This object specializes in encoding and decoding operations where
 * padding is enabled to ensure data alignment.
 *
 * Base32Pad inherits from Base32Base and customizes the behavior to include the appropriate
 * padding character and the standard Base32 alphabet.
 */
object Base32Pad : Base32Base() {
    /**
     * The `alphabet` property defines the set of characters used for encoding and decoding
     * within the Base32 encoding scheme implemented by this coder. The character set includes
     * all 26 lowercase letters of the English alphabet followed by the digits '2' through '7',
     * complying with the standard Base32 character set as per RFC 4648.
     */
    override val alphabet = "abcdefghijklmnopqrstuvwxyz234567"

    /**
     * The character used as a padding placeholder in Base2 encoding or decoding operations.
     *
     * This value is overridden and set to the '=' character to indicate padding in cases
     * where the encoded binary string needs to maintain alignment or fill required structure
     * during processing.
     */
    override val padChar = '='

    /**
     * Indicates whether padding should be applied during encoding or decoding operations.
     *
     * When `usePadding` is set to `true`, padding can be applied depending on specific
     * encoding/decoding requirements to ensure completeness of the binary data representation.
     * If `false`, padding is omitted, which may lead to encoding/decoding that strictly adheres
     * to the raw input/output without additional formatting adjustments.
     */
    override val usePadding = true
}

/**
 * The `Base32PadUpper` object provides an implementation of the Base32 encoding scheme.
 *
 * This object uses a specific encoding alphabet consisting of uppercase English letters
 * (A-Z) and digits (2-7), as defined by the Base32 standard in RFC 4648. It also includes
 * support for optional padding using the '=' character.
 *
 * The implementation provides configuration options such as defining whether padding will
 * be applied during the encoding or decoding process.
 */
object Base32PadUpper : Base32Base() {
    /**
     * The `alphabet` property defines a custom set of characters used as the encoding
     * alphabet for the Base32 encoding scheme.
     *
     * This set includes uppercase English letters (A-Z) and digits (2-7).
     * It conforms to the Base32 standard as specified in RFC 4648.
     */
    override val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    /**
     * The character used for padding encoded data when necessary.
     *
     * In the context of Base2 encoding, padding may not typically be required, but this value is defined
     * to ensure uniformity with other encodings that may require padding. The specific symbol '=' is
     * used to maintain compatibility with other encoding schemes if needed in future implementations.
     */
    override val padChar = '='

    /**
     * Indicates whether padding should be used when encoding or decoding binary data.
     *
     * When set to `true`, padding may be applied or expected during the encoding or decoding
     * process, depending on the implementation of the coder. Padding typically ensures that
     * the encoded or decoded data aligns to specific byte boundaries or length requirements.
     */
    override val usePadding = true
}

/**
 * An object that provides Base32 encoding and decoding functionality, ignoring case sensitivity.
 *
 * This class extends the Base32Base class and overrides specific properties and methods to support
 * case-insensitive decoding by converting input data to lowercase before processing. The Base32 alphabet
 * used consists of lowercase English letters and digits, with optional padding support disabled by default.
 */
object Base32CaseInsensitive : Base32Base() {
    /**
     * Represents the alphabet used for encoding and decoding operations.
     *
     * This specific alphabet is a combination of lowercase English letters (a-z)
     * followed by the numerals 2-7. It is utilized for encoding schemes where
     * a limited and specific set of characters is required for data representation.
     */
    override val alphabet = "abcdefghijklmnopqrstuvwxyz234567"

    /**
     * The character used for padding encoded data when necessary.
     *
     * This character is used in scenarios where the encoded output needs to be aligned
     * to a fixed length or where padding is required to meet specific formatting rules.
     * In the context of encoding, this padding character ensures structural consistency
     * of the encoded data representation.
     */
    override val padChar = '='

    /**
     * A flag that determines whether padding should be applied during encoding or decoding operations.
     *
     * In the context of Base2 encoding, padding refers to the insertion or consideration of additional
     * characters to meet certain alignment or length requirements. This property can be overridden
     * to specify whether such padding behavior is desired.
     *
     * When set to `false`, no padding will be applied, and encoded or decoded data will not be adjusted
     * to include additional characters beyond what is strictly necessary for the operation.
     */
    override val usePadding = false

    /**
     * Decodes the given string input into a byte array.
     *
     * @param data the input string to be decoded
     * @return the decoded byte array
     */
    override fun decode(data: String): ByteArray {
        return super.decode(data.lowercase())
    }
}

/**
 * An object that represents a variant of Base32 encoding using the extended hexadecimal alphabet.
 * It is case-insensitive and allows decoding of strings with mixed-case characters by converting
 * input to lowercase before processing.
 *
 * Implements the Base32HexVariant interface and provides the necessary configuration for
 * the Base32 encoding/decoding process, including alphabet, padding character, and padding usage.
 */
object Base32HexCaseInsensitive : Base32Base(), Base32HexVariant {
    /**
     * Represents the character set used for encoding and decoding operations.
     *
     * This string defines the alphabet used in conversion processes, where each
     * character corresponds to a specific value. The ordering of the characters
     * determines their respective values during encoding and decoding.
     *
     * The `alphabet` supports a custom encoding based on a sequence of
     * characters that includes the numbers 0-9 followed by the letters a-v.
     */
    override val alphabet = "0123456789abcdefghijklmnopqrstuv"

    /**
     * The character used to pad encoded binary data to ensure proper alignment or formatting.
     *
     * In the context of Base2 encoding, padding may not be typically required since binary data
     * representation doesn't necessitate alignment adjustments that occur in formats like Base64.
     * However, this value is defined for consistency with potential interface or superclass
     * requirements that may utilize padding.
     */
    override val padChar = '='

    /**
     * Indicates whether padding should be used during encoding or decoding operations.
     *
     * This property determines if additional padding bits or characters are considered
     * in the encoded or decoded output. For the `Base2` encoder, padding is typically
     * unnecessary, as binary representation does not require it, and this property is set to `false`.
     */
    override val usePadding = false

    /**
     * Decodes the given string into a ByteArray after converting it to lowercase.
     *
     * @param data The string input to be decoded.
     * @return The decoded ByteArray.
     */
    override fun decode(data: String): ByteArray {
        return super.decode(data.lowercase())
    }
}
