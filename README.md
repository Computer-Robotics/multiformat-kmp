# Multiformat-KMP

THIS IS LIB IS STILL A WORK IN PROGRESS BUT WHAT HAS BEEN IMPLEMENTED SO FAR IS WORKING AND FULLY TESTED BUT WILL BE
PUBLISHED ON MAVEN CENTRAL ON A LATER DATE.

[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

![Computer Robotics Logo](Logo.png "Computer Robotics Logo")

Self-describing values for Future-proofing. Every choice in computing has a tradeoff. This includes formats, algorithms,
encodings, and so on. And even with a great deal of planning, decisions may lead to breaking changes down the road, or
to solutions which are no longer optimal. Allowing systems to evolve and grow is important.

Protocol implementation of multiformat as per [multiformats.io](https://multiformats.io/).

## Multiformat protocols

The Multiformats-KMP Project describes a series of protocols as described
on [multiformats.io](https://multiformats.io/). Currently, the following protocols exist:

* **multiaddr** (WIP) - self-describing network addresses
* **multibase** (WIP) - self-describing base encodings
* **multicodec** - self-describing serialization
* **multihash** - self-describing hashes

## Multibase

Multibase is a protocol for disambiguating the "base encoding" used to express binary data in text formats (e.g.,
base32, base36, base64, base58, etc.) from the expression alone.

When text is encoded as bytes, we can usually use a one-size-fits-all encoding (UTF-8) because we're always encoding to
the same set of 256 bytes (+/- the NUL byte). When that doesn't work, usually for historical or performance reasons, we
can usually infer the encoding from the context.

However, when bytes are encoded as text (using a base encoding), the choice of base encoding (and alphabet, and other
factors) is often restricted by the context. Worse, these restrictions can change based on where the data appears in the
text. In some cases, we can only use [a-z0-9]; in others, we can use a larger set of characters but need a compact
encoding. This has lead to a large set of "base encodings", almost one for every use-case. Unlike the case of encoding
text to bytes, it is impractical to standardize widely around a single base encoding because there is no optimal
encoding for all cases.

As data travels beyond its context, it becomes quite hard to ascertain which base encoding of the many possible ones
were used; that's where multibase comes in. Where the data has been prefixed before leaving its context behind, it
answers the question:

> Given binary data `d` encoded into text `s`, what base `b` was used to encode it?

To answer this question, a single code point is prepended to `s` at time of encoding, which signals in that new context
which `b` can be used to reconstruct `d`.

| Unicode | character | encoding          | description                                                  | status       | implemented |
|---------|-----------|-------------------|--------------------------------------------------------------|--------------|-------------|
| U+0000  | NUL       | none              | (No base encoding)                                           | reserved     | Yes         |
| U+0030  | 0         | base2             | Binary (01010101)                                            | experimental | Yes         |
| U+0031  | 1         | none              | (No base encoding)                                           | reserved     | Yes         |
| U+0037  | 7         | base8             | Octal                                                        | draft        | Yes         |
| U+0039  | 9         | base10            | Decimal                                                      | draft        | Yes         |
| U+0066  | f         | base16            | Hexadecimal (lowercase)                                      | final        | Yes         |
| U+0046  | F         | base16upper       | Hexadecimal (uppercase)                                      | final        | Yes         |
| U+0076  | v         | base32hex         | RFC4648 case-insensitive - no padding - highest char         | experimental | Yes         |
| U+0056  | V         | base32hexupper    | RFC4648 case-insensitive - no padding - highest char         | experimental | Yes         |
| U+0074  | t         | base32hexpad      | RFC4648 case-insensitive - with padding                      | experimental | Yes         |
| U+0054  | T         | base32hexpadupper | RFC4648 case-insensitive - with padding                      | experimental | Yes         |
| U+0062  | b         | base32            | RFC4648 case-insensitive - no padding                        | final        | Yes         |
| U+0042  | B         | base32upper       | RFC4648 case-insensitive - no padding                        | final        | Yes         |
| U+0063  | c         | base32pad         | RFC4648 case-insensitive - with padding                      | draft        | Yes         |
| U+0043  | C         | base32padupper    | RFC4648 case-insensitive - with padding                      | draft        | Yes         |
| U+0068  | h         | base32z           | z-base-32 (used by Tahoe-LAFS)                               | draft        | Yes         |
| U+006b  | k         | base36            | Base36 [0-9a-z] case-insensitive - no padding                | draft        | Yes         |
| U+004b  | K         | base36upper       | Base36 [0-9a-z] case-insensitive - no padding                | draft        | Yes         |
| U+0052  | R         | base45            | Base45 RFC9285                                               | draft        | Yes         |
| U+007a  | z         | base58btc         | Base58 Bitcoin                                               | final        | Yes         |
| U+005a  | Z         | base58flickr      | Base58 Flicker                                               | experimental | Yes         |
| U+006d  | m         | base64            | RFC4648 no padding                                           | final        | Yes         |
| U+004d  | M         | base64pad         | RFC4648 with padding - MIME encoding                         | experimental | Yes         |
| U+0075  | u         | base64url         | RFC4648 no padding                                           | final        | Yes         |
| U+0055  | U         | base64urlpad      | RFC4648 with padding                                         | final        | Yes         |
| U+0070  | p         | proquint          | [Proquint](https://arxiv.org/html/0901.4016)                 | experimental | Yes         |
| U+0051  | Q         | none              | (no base encoding)                                           | reserved     | Yes         |
| U+002F  | /         | none              | (no base encoding)                                           | reserved     | Yes         |
| U+1F680 | 🚀        | base256emoji      | base256 with custom alphabet using variable-sized-codepoints | experimental | No          |

For more details, please check the [Specs](https://github.com/multiformats/multibase/)
