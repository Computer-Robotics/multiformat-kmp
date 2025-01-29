package tech.compubotics.kmp.multiformat.multibase

/**
 * Enumeration representing various encoding formats, along with their associated metadata.
 *
 * Each encoding format includes a prefix character and a description that provides a brief explanation
 * of the encoding type. These formats represent different ways of encoding data using standards like
 * RFC4648 and others.
 *
 * @property prefix A character or string used as a prefix to identify the encoding.
 * @property description A textual description explaining the encoding format.
 */
enum class Encoding(val prefix: String, val description: String) {
    // Reserved
    NULL("\u0000", "(No base encoding)"),
    IDENTITY("\u0001", "(No base encoding)"),

    // Active
    BASE2("0", "Binary (01010101)"),
    BASE8("7", "Octal"),
    BASE10("9", "Decimal"),
    BASE16("f", "Hexadecimal (lowercase)"),
    BASE16UPPER("F", "Hexadecimal (uppercase)"),
    BASE32HEX("v", "RFC4648 - no padding - highest char"),
    BASE32HEXUPPER("V", "RFC4648 - no padding - highest char"),
    BASE32HEXPAD("t", "RFC4648 - with padding"),
    BASE32HEXPADUPPER("T", "RFC4648 - with padding"),
    BASE32("b", "RFC4648 - no padding"),
    BASE32UPPER("B", "RFC4648 - no padding"),
    BASE32PAD("c", "RFC4648 - with padding"),
    BASE32PADUPPER("C", "RFC4648 - with padding"),
    BASE32Z("h", "z-base-32 (used by Tahoe-LAFS)"),
    BASE36("k", "Base36 [0-9a-z] - no padding"),
    BASE36UPPER("K", "Base36 [0-9A-Z] - no padding"),
    BASE45("R", "Base45 RFC9285"),
    BASE58BTC("z", "Base58 Bitcoin"),
    BASE58FLICKR("Z", "Base58 Flicker"),
    BASE64("m", "RFC4648 no padding"),
    BASE64PAD("M", "RFC4648 with padding - MIME encoding"),
    BASE64URL("u", "RFC4648 no padding"),
    BASE64URLPAD("U", "RFC4648 with padding"),
    PROQUINT("p", "Proquint"),
    BASE256EMOJI("🚀", "base256 with custom alphabet using variable-sized-codepoints")
}
