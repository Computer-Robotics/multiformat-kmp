package tech.compubotics.kmp.multiformat.multibase

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import tech.compubotics.kmp.encoding.Base10
import tech.compubotics.kmp.encoding.Base16
import tech.compubotics.kmp.encoding.Base16CaseInsensitive
import tech.compubotics.kmp.encoding.Base16Upper
import tech.compubotics.kmp.encoding.Base2
import tech.compubotics.kmp.encoding.Base32
import tech.compubotics.kmp.encoding.Base32CaseInsensitive
import tech.compubotics.kmp.encoding.Base32Hex
import tech.compubotics.kmp.encoding.Base32HexCaseInsensitive
import tech.compubotics.kmp.encoding.Base32HexPad
import tech.compubotics.kmp.encoding.Base32HexPadUpper
import tech.compubotics.kmp.encoding.Base32HexUpper
import tech.compubotics.kmp.encoding.Base32Pad
import tech.compubotics.kmp.encoding.Base32PadUpper
import tech.compubotics.kmp.encoding.Base32Upper
import tech.compubotics.kmp.encoding.Base32z
import tech.compubotics.kmp.encoding.Base36
import tech.compubotics.kmp.encoding.Base36CaseInsensitive
import tech.compubotics.kmp.encoding.Base36Upper
import tech.compubotics.kmp.encoding.Base45
import tech.compubotics.kmp.encoding.Base58Bitcoin
import tech.compubotics.kmp.encoding.Base58Flickr
import tech.compubotics.kmp.encoding.Base8
import tech.compubotics.kmp.encoding.Proquint

/**
 * Utility object for performing multibase encoding and decoding operations.
 * Multibase is a protocol for distinguishing base encodings and decoding encoded data.
 * Each encoding is represented by a prefix that identifies the base to be used.
 * @see [Specification](https://github.com/multiformats/multibase)
 */
object Multibase {
    /**
     * Encodes the given byte array into a string representation based on the specified encoding scheme.
     *
     * @param encoding The encoding scheme to use for the conversion. This determines the format and style of the output.
     * @param data The input data in the form of a byte array that needs to be encoded.
     * @return A string representation of the input data based on the specified encoding scheme, prefixed with the encoding prefix.
     */
    @OptIn(ExperimentalEncodingApi::class)
    @Throws(UnsupportedEncodingException::class)
    fun encode(encoding: Encoding, data: ByteArray): String {
        val encodedData =
            when (encoding) {
                // Call out to your base encoding implementations here
                Encoding.NULL -> data.decodeToString()
                Encoding.IDENTITY -> data.decodeToString()
                Encoding.BASE2 -> Base2.encodeToString(data)
                Encoding.BASE8 -> Base8.encodeToString(data)
                Encoding.BASE10 -> Base10.encodeToString(data)
                Encoding.BASE16 -> Base16.encodeToString(data)
                Encoding.BASE16UPPER -> Base16Upper.encodeToString(data)
                Encoding.BASE32 -> Base32.encodeToString(data)
                Encoding.BASE32UPPER -> Base32Upper.encodeToString(data)
                Encoding.BASE32HEX -> Base32Hex.encodeToString(data)
                Encoding.BASE32HEXUPPER -> Base32HexUpper.encodeToString(data)
                Encoding.BASE32HEXPAD -> Base32HexPad.encodeToString(data)
                Encoding.BASE32HEXPADUPPER -> Base32HexPadUpper.encodeToString(data)
                Encoding.BASE32PAD -> Base32Pad.encodeToString(data)
                Encoding.BASE32PADUPPER -> Base32PadUpper.encodeToString(data)
                Encoding.BASE32Z -> Base32z.encodeToString(data)
                Encoding.BASE36 -> Base36.encodeToString(data)
                Encoding.BASE36UPPER -> Base36Upper.encodeToString(data)
                Encoding.BASE45 -> Base45.encodeToString(data)
                Encoding.BASE58BTC -> Base58Bitcoin.encodeToString(data)
                Encoding.BASE58FLICKR -> Base58Flickr.encodeToString(data)
                Encoding.BASE64 -> Base64.Default.withPadding(Base64.PaddingOption.ABSENT).encode(data)
                Encoding.BASE64PAD -> Base64.Default.withPadding(Base64.PaddingOption.PRESENT).encode(data)
                Encoding.BASE64URL -> Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(data)
                Encoding.BASE64URLPAD -> Base64.UrlSafe.withPadding(Base64.PaddingOption.PRESENT).encode(data)
                Encoding.PROQUINT -> Proquint.encodeToString(data)
                Encoding.BASE256EMOJI -> throw UnsupportedEncodingException("Base256Emoji encoding not yet implemented")
            }
        return encoding.prefix + encodedData
    }

    /**
     * Decodes a multibase encoded string into its original byte array representation.
     *
     * @param data The multibase encoded string to decode. Must not be empty.
     * @return A pair consisting of the detected encoding type and the decoded byte array.
     * @throws IllegalArgumentException if the input string is empty.
     * @throws UnknownPrefixException if the encoding prefix in the input string is unrecognized.
     */
    @OptIn(ExperimentalEncodingApi::class)
    @Throws(IllegalArgumentException::class, UnknownPrefixException::class, UnsupportedEncodingException::class)
    fun decode(data: String): Pair<Encoding, ByteArray> {
        if (data.isEmpty()) {
            return Pair(Encoding.NULL, ByteArray(0))
        }
        val encoding =
            Encoding.entries.find { data.startsWith(it.prefix) }
                ?: throw UnknownPrefixException("Unknown Multibase prefix in: $data")

        val encodedData = data.substring(encoding.prefix.length)

        val decodedData =
            when (encoding) {
                // Call out to your base decoding implementations here
                Encoding.NULL -> encodedData.encodeToByteArray()
                Encoding.IDENTITY -> encodedData.encodeToByteArray()
                Encoding.BASE2 -> Base2.decode(encodedData)
                Encoding.BASE8 -> Base8.decode(encodedData)
                Encoding.BASE10 -> Base10.decode(encodedData)
                Encoding.BASE16 -> Base16CaseInsensitive.decode(encodedData)
                Encoding.BASE16UPPER -> Base16CaseInsensitive.decode(encodedData)
                Encoding.BASE32 -> Base32CaseInsensitive.decode(encodedData)
                Encoding.BASE32UPPER -> Base32CaseInsensitive.decode(encodedData)
                Encoding.BASE32HEX -> Base32HexCaseInsensitive.decode(encodedData)
                Encoding.BASE32HEXUPPER -> Base32HexCaseInsensitive.decode(encodedData)
                Encoding.BASE32HEXPAD -> Base32HexCaseInsensitive.decode(encodedData)
                Encoding.BASE32HEXPADUPPER -> Base32HexCaseInsensitive.decode(encodedData)
                Encoding.BASE32PAD -> Base32CaseInsensitive.decode(encodedData)
                Encoding.BASE32PADUPPER -> Base32CaseInsensitive.decode(encodedData)
                Encoding.BASE32Z -> Base32z.decode(encodedData)
                Encoding.BASE36 -> Base36CaseInsensitive.decode(encodedData)
                Encoding.BASE36UPPER -> Base36CaseInsensitive.decode(encodedData)
                Encoding.BASE45 -> Base45.decode(encodedData)
                Encoding.BASE58BTC -> Base58Bitcoin.decode(encodedData)
                Encoding.BASE58FLICKR -> Base58Flickr.decode(encodedData)
                Encoding.BASE64 -> Base64.Default.withPadding(Base64.PaddingOption.ABSENT).decode(encodedData)
                Encoding.BASE64PAD -> Base64.Default.withPadding(Base64.PaddingOption.PRESENT).decode(encodedData)
                Encoding.BASE64URL -> Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).decode(encodedData)
                Encoding.BASE64URLPAD -> Base64.UrlSafe.withPadding(Base64.PaddingOption.PRESENT).decode(encodedData)
                Encoding.PROQUINT -> Proquint.decode(encodedData)
                Encoding.BASE256EMOJI -> throw UnsupportedEncodingException("Base256Emoji encoding not yet implemented")
            }
        return Pair(encoding, decodedData)
    }
}
