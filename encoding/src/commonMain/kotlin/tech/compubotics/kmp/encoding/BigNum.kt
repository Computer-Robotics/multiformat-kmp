package tech.compubotics.kmp.encoding

/**
 * Represents an arbitrary-precision number using a list of integers as its digits.
 *
 * This class provides methods to perform basic arithmetic operations (addition, division, multiplication),
 * as well as utility operations such as checking for zero, converting to a byte array, and comparing equality.
 *
 * @note: Arbitrary-precision number helper class - Very simple implementation of BigInteger
 */
internal class BigNum(val digits: List<Int> = emptyList()) {
    /**
     * Checks whether the current BigNum object represents the value zero.
     *
     * The method evaluates if the digits list is either empty or consists solely of zeros.
     *
     * @return `true` if the BigNum is zero, determined by an empty digits list or all elements
     *         being zero; otherwise, `false`.
     */
    fun isZero(): Boolean {
        return digits.isEmpty() || digits.all { it == 0 }
    }

    /**
     * Divides the current BigNum object by the specified divisor.
     *
     * This method performs division on the BigNum object's internal representation,
     * returning both the quotient and the remainder as a pair. The quotient is
     * represented as a new BigNum, and the remainder is returned as an integer.
     *
     * @param divisor The integer value by which to divide the BigNum.
     *                Must not be zero; division by zero is undefined behavior.
     * @return A `Pair` where the first element is the quotient as a BigNum
     *         and the second element is the remainder as an integer.
     */
    fun divide(divisor: Int): Pair<BigNum, Int> {
        var carry = 0
        val quotient = mutableListOf<Int>()
        digits.forEach { byte ->
            val current = carry * 256 + byte
            quotient.add(current / divisor)
            carry = current % divisor
        }
        return Pair(BigNum(quotient.dropWhile { it == 0 }), carry)
    }

    /**
     * Multiplies the current BigNum instance by the provided factor.
     *
     * This method iterates through the digits of the BigNum in reverse order,
     * performing multiplication with the specified factor and managing carry-over
     * between digit positions. The result is a new BigNum representing the product.
     *
     * @param factor The integer by which the current BigNum is to be multiplied.
     * @return A new BigNum representing the product of the current value and the factor.
     */
    fun multiply(factor: Int): BigNum {
        var carry = 0
        val result = mutableListOf<Int>()
        digits.reversed().forEach { byte ->
            val product = byte * factor + carry
            result.add(product % 256)
            carry = product / 256
        }
        if (carry > 0) {
            result.add(carry)
        }
        return BigNum(
            result.reversed().let {
                val cleaned = it.dropWhile { digit -> digit == 0 }
                cleaned.ifEmpty { listOf(0) }
            }
        )
    }

    /**
     * Adds a given integer value to the current BigNum instance.
     *
     * This method modifies the internal `digits` list of the BigNum by adding the specified
     * value, handling carry-over between digit positions as needed. If there is remaining
     * carry after processing all existing digits, it appends a new digit to the front of the list.
     *
     * @param value The integer value to be added to the BigNum.
     * @return A new BigNum representing the result of the addition.
     */
    fun add(value: Int): BigNum {
        if (value == 0) return this
        val result = digits.toMutableList()
        var carry = value
        var index = digits.lastIndex
        while (carry > 0 && index >= 0) {
            val sum = result[index] + carry
            result[index] = sum % 256
            carry = sum / 256
            index--
        }
        if (carry > 0) result.add(0, carry)
        return BigNum(result)
    }

    /**
     * Converts the internal representation of the BigNum object into a byte array.
     *
     * The conversion removes leading zeros from the internal digits list, ensuring
     * the resulting byte array has no unnecessary leading zero bytes. If the BigNum
     * represents zero, the method returns a byte array containing a single zero byte.
     *
     * @return A ByteArray representing the BigNum value. If the value is zero, it returns
     *         a single zero byte. Otherwise, it contains the non-zero digits converted
     *         to their respective byte values.
     */
    fun toByteArray(): ByteArray {
        val nonZeroDigits = digits.dropWhile { it == 0 }
        return when {
            nonZeroDigits.isEmpty() -> byteArrayOf(0)
            else -> nonZeroDigits.map { it.toByte() }.toByteArray()
        }
    }

    /**
     * Compares the current BigNum object with another object for equality.
     *
     * This method checks whether the provided object is of type `BigNum` and
     * whether its `digits` list is equal to the `digits` list of the current instance.
     *
     * @param other The object to compare with the current instance. May be null.
     * @return `true` if the specified object is a `BigNum` and its `digits`
     *         list is equal to the `digits` list of the current instance; otherwise, `false`.
     */
    override fun equals(other: Any?): Boolean {
        return (other as? BigNum)?.digits == this.digits
    }

    /**
     * Computes a hash code for the `BigNum` object.
     *
     * The hash code is derived from the `digits` property of the `BigNum` instance.
     *
     * @return An integer value representing the hash code of the `BigNum` instance.
     */
    override fun hashCode(): Int {
        return digits.hashCode()
    }

    companion object {
        /**
         * Converts a given byte array into a BigNum representation.
         *
         * This method removes leading zero bytes from the input byte array
         * and maps the remaining bytes to their corresponding unsigned integer values.
         *
         * @param bytes The byte array to be converted. Leading zero bytes will be ignored.
         * @return A BigNum instance representing the value of the input byte array.
         */
        fun fromBytes(bytes: ByteArray): BigNum {
            val nonZero = bytes.dropWhile { it == 0.toByte() }
            return BigNum(nonZero.map { it.toInt() and 0xFF })
        }
    }
}
