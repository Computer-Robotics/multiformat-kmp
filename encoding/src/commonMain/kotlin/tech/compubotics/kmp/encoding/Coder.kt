package tech.compubotics.kmp.encoding

/**
 * The Coder interface combines encoding and decoding functionalities
 * by extending both the Encoder and Decoder interfaces.
 *
 * Implementations of this interface are responsible for providing methods
 * to transform data between its raw binary representation and an encoded form,
 * as well as restoring the original data through decoding operations.
 *
 * This interface acts as a unified contract for classes that need to
 * handle both encoding and decoding logic.
 */
interface Coder : Encoder, Decoder
