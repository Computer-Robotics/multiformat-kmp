package tech.compubotics.kmp.multiformat.multibase

/**
 * Represents the base exception type for errors encountered within the Multibase module.
 * This sealed class serves as a common superclass for all specific exceptions related
 * to multibase operations, providing a standardized way to report and handle errors.
 *
 * @constructor Initializes the exception with a given error message.
 * @param message A descriptive message providing details about the exception.
 */
sealed class MultibaseException(message: String) : RuntimeException(message)

/**
 * Exception thrown to indicate that a specified encoding is not supported.
 *
 * This specific exception is a subclass of MultibaseException and is typically
 * raised when an attempt is made to use an unsupported or unrecognized encoding
 * within the context of multibase operations.
 *
 * @constructor Creates an UnsupportedEncodingException with a detailed error message.
 * @param message A message describing the context or reason for the exception.
 */
class UnsupportedEncodingException(message: String) : MultibaseException(message)

/**
 * Exception thrown when an unknown or invalid encoding prefix is encountered during
 * a multibase operation.
 *
 * The prefix is a character used to identify the encoding type, and this exception
 * indicates that the provided prefix does not match any of the known encoding schemes
 * defined in the `Encoding` enum.
 *
 * This exception extends `MultibaseException`, allowing it to be used as part of
 * the standardized error handling for the multibase module.
 *
 * @constructor Initializes the exception with a given error message.
 * @param message A descriptive message providing details about the unknown prefix error.
 */
class UnknownPrefixException(message: String) : MultibaseException(message)
