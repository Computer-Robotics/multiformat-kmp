# KMP Encoding Library

[![badge-kotlin]][url-kotlin]
[![badge-github-release]][url-github-release]
[![badge-javadoc]][url-javadoc]
![badge-semantic-release-kotlin]

![badge-platform-android]
![badge-platform-jvm]
![badge-platform-js]
![badge-platform-js-node]
![badge-platform-wasm]
![badge-platform-linux]
![badge-platform-macos]
![badge-platform-ios]
![badge-platform-tvos]
![badge-platform-watchos]
![badge-platform-windows]
![badge-support-android-native]
![badge-support-apple-silicon]
![badge-support-js-ir]
![badge-support-linux-arm]

A Kotlin Multiplatform (KMP) library implementing common encoding/decoding schemes with RFC compliance (if exist) and
production-grade reliability.

## Supported Encoding

| Encoding          | Description                                                                                       | Usage                                                                                                                                                                                                                                                                                     | Reference | Implemented |
|-------------------|---------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|-------------|
| Base2             | Binary (01010101)                                                                                 | Binary encoding. When you need to interact directly with hardware, manipulate individual bits, or work with memory addresses at a very low level.                                                                                                                                         | N/A       | Yes         |
| Base8             | Octal                                                                                             | Compact numeric representation. Historically used in UNIX-like systems to represent file permissions (e.g., 755). You might still encounter this.                                                                                                                                         | N/A       | Yes         |
| Base10            | Decimal                                                                                           | Standard decimal encoding. This is the system we use in everyday life for counting, calculations, and communicating numerical values.                                                                                                                                                     | N/A       | Yes         |
| Base16            | Hexadecimal (lowercase)                                                                           | Hexadecimal representation. Often used to represent memory addresses because it's more compact than binary but easily convertible.                                                                                                                                                        | N/A       | Yes         |
| Base16Upper       | Hexadecimal (uppercase)                                                                           | Hexadecimal representation. Often used to represent memory addresses because it's more compact than binary but easily convertible.                                                                                                                                                        | N/A       | Yes         |
| Base32Hex         | [RFC4648](https://datatracker.ietf.org/doc/html/rfc4648.html) - no padding - highest char         | Compact Base32 encoding. Useful when you need to transmit or store data in environments where case might be lost or ignored (e.g., some legacy systems, URLs, or file systems).                                                                                                           | RFC4648   | Yes         |
| Base32HexUpper    | [RFC4648](https://datatracker.ietf.org/doc/html/rfc4648.html) - no padding - highest char         | Compact Base32 encoding. Useful when you need to transmit or store data in environments where case might be lost or ignored (e.g., some legacy systems, URLs, or file systems).                                                                                                           | RFC4648   | Yes         |
| Base32HexPad      | [RFC4648](https://datatracker.ietf.org/doc/html/rfc4648.html) - with padding                      | Base32 with padding. Useful when you need to transmit or store data in environments where case might be lost or ignored (e.g., some legacy systems, URLs, or file systems).                                                                                                               | RFC4648   | Yes         |
| Base32HexPadUpper | [RFC4648](https://datatracker.ietf.org/doc/html/rfc4648.html) - with padding                      | Base32 with padding. Useful when you need to transmit or store data in environments where case might be lost or ignored (e.g., some legacy systems, URLs, or file systems).                                                                                                               | RFC4648   | Yes         |
| Base32            | [RFC4648](https://datatracker.ietf.org/doc/html/rfc4648.html) - no padding                        | General Base32 encoding. Useful when you need to transmit or store data in environments where case might be lost or ignored (e.g., some legacy systems, URLs, or file systems).                                                                                                           | RFC4648   | Yes         |
| Base32Upper       | [RFC4648](https://datatracker.ietf.org/doc/html/rfc4648.html) - no padding                        | General Base32 encoding. Useful when you need to transmit or store data in environments where case might be lost or ignored (e.g., some legacy systems, URLs, or file systems).                                                                                                           | RFC4648   | Yes         |
| Base32Pad         | [RFC4648](https://datatracker.ietf.org/doc/html/rfc4648.html) - with padding                      | Base32 with padding. Useful when you need to transmit or store data in environments where case might be lost or ignored (e.g., some legacy systems, URLs, or file systems).                                                                                                               | RFC4648   | Yes         |
| Base32PadUpper    | [RFC4648](https://datatracker.ietf.org/doc/html/rfc4648.html) - with padding                      | Base32 with padding. Useful when you need to transmit or store data in environments where case might be lost or ignored (e.g., some legacy systems, URLs, or file systems).                                                                                                               | RFC4648   | Yes         |
| Base32z           | [Human-oriented base32 spec](https://philzimmermann.com/docs/human-oriented-base-32-encoding.txt) | Human-readable Base32. When ou need an encoding that humans can easily read, write, and type without errors.                                                                                                                                                                              | N/A       | Yes         |
| Base36            | Base36 [0-9a-z] case-insensitive - no padding                                                     | Alphanumeric encoding. Base36 is excellent when you need short, human-readable identifiers that are restricted to alphanumeric characters and are not case-sensitive. Some URL shortening services use Base36 to generate short codes.                                                    | N/A       | Yes         |
| Base36Upper       | Base36 [0-9a-z] case-insensitive - no padding                                                     | Alphanumeric encoding. Base36 is excellent when you need short, human-readable identifiers that are restricted to alphanumeric characters and are not case-sensitive. Some URL shortening services use Base36 to generate short codes.                                                    | N/A       | Yes         |
| Base45            | Base45 RFC9285                                                                                    | Compact QR code representation. QR Codes have different modes for encoding data (numeric, alphanumeric, byte, Kanji). Alphanumeric mode is more efficient than byte mode (it stores more data per module). Base45's character set aligns with the alphanumeric mode of QR Codes.          | RFC9285   | Yes         |
| Base58Btc         | Base58 Bitcoin                                                                                    | Cryptocurrency encoding. The primary goal is to make addresses and other identifiers easier for humans to read, write, and share without errors. The exclusion of ambiguous characters is key here.                                                                                       | N/A       | Yes         |
| Base58Flickr      | Base58 Flicker                                                                                    | Shortened URL representation. Flickr's primary motivation for using Base58 was to generate short URLs.                                                                                                                                                                                    | N/A       | Yes         |
| Base64            | RFC4648 no padding                                                                                | Data encoding. Base64's primary purpose is to encode arbitrary binary data into a text format that uses a set of 64 ASCII characters. This is crucial when you need to transmit or store binary data in systems that can only handle text reliably (e.g., email, XML, JSON).              | RFC4648   | No          |
| Base64Pad         | RFC4648 with padding - MIME encoding                                                              | Data encoding with padding. Base64's primary purpose is to encode arbitrary binary data into a text format that uses a set of 64 ASCII characters. This is crucial when you need to transmit or store binary data in systems that can only handle text reliably (e.g., email, XML, JSON). | RFC4648   | No          |
| Base64URL         | RFC4648 no padding                                                                                | URL-safe Base64 encoding. Base64 URL is a modification of standard Base64 specifically designed to be safe for use in URLs and filenames.                                                                                                                                                 | RFC4648   | No          |
| Base64URLPad      | RFC4648 with padding                                                                              | URL-safe Base64 with padding. Base64 URL is a modification of standard Base64 specifically designed to be safe for use in URLs and filenames.                                                                                                                                             | RFC4648   | No          |
| proquint          | Proquint [Proquint specification](https://arxiv.org/html/0901.4016)                               | Readable identifiers. Proquint identifiers are designed to be pronounceable according to a simple phonetic structure, making them easy to communicate verbally.                                                                                                                           | N/A       | Yes         |
| Base256emoji      | base256 with custom alphabet using variable-sized-codepoints                                      | Novel and fun representation                                                                                                                                                                                                                                                              | N/A       | No          |

## Base64

Base64 is not implemented as it already exist
in [Kotlin](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.io.encoding/-base64/) since v1.8

<!-- TAG_PLATFORMS -->
[badge-javadoc]: https://javadoc.io/badge2/tech.compubotics/encoding/javadoc.svg
[badge-kotlin]: https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin
[badge-semantic-release-kotlin]: https://img.shields.io/badge/semantic--release-kotlin-blue?logo=semantic-release
[badge-github-release]: https://img.shields.io/github/v/release/Computer-Robotics/multiformat-kmp?label=Latest%20Release
[badge-platform-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-platform-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-platform-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-platform-js-node]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat
[badge-platform-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat
[badge-platform-macos]: http://img.shields.io/badge/-macos-111111.svg?style=flat
[badge-platform-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-platform-tvos]: http://img.shields.io/badge/-tvos-808080.svg?style=flat
[badge-platform-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat
[badge-platform-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg?style=flat
[badge-platform-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-support-android-native]: http://img.shields.io/badge/support-[AndroidNative]-6EDB8D.svg?style=flat
[badge-support-apple-silicon]: http://img.shields.io/badge/support-[AppleSilicon]-43BBFF.svg?style=flat
[badge-support-js-ir]: https://img.shields.io/badge/support-[js--IR]-AAC4E0.svg?style=flat
[badge-support-linux-arm]: http://img.shields.io/badge/support-[LinuxArm]-2D3F6C.svg?style=flat

<!-- URLs -->
[url-javadoc]: https://javadoc.io/doc/tech.compubotics/encoding
[url-kotlin]: http://kotlinlang.org
[url-github-release]: https://github.com/Computer-Robotics/multiformat-kmp/releases
