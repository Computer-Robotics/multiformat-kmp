package tech.compubotics.kmp.encoding

object Proquint : Coder {
    private val consonants = listOf('b', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'v', 'z')
    private val vowels = listOf('a', 'i', 'o', 'u')

    override fun encode(data: ByteArray): ByteArray = encodeToString(data).encodeToByteArray()

    override fun encodeToString(data: ByteArray): String {
        if (data.isEmpty()) return ""
        return data.asList()
            .chunked(2) { pair ->
                val value = when {
                    pair.size == 2 -> (pair[0].toInt() and 0xFF shl 8) or (pair[1].toInt() and 0xFF)
                    else -> (pair[0].toInt() and 0xFF shl 8)
                }
                encodeWord(value)
            }
            .joinToString("-")
    }

    private fun encodeWord(value: Int): String {
        return buildString {
            append(consonants[(value ushr 12) and 0x0F])  // C3
            append(vowels[(value ushr 10) and 0x03])      // V2
            append(consonants[(value ushr 6) and 0x0F])   // C2
            append(vowels[(value ushr 4) and 0x03])       // V1
            append(consonants[value and 0x0F])            // C1
        }
    }

    override fun decode(data: ByteArray): ByteArray = decode(data.decodeToString())

    override fun decode(data: String): ByteArray {
        return data.split("-").flatMap { word ->
            require(word.length == 5) { "Invalid quint: $word" }

            val c3 = consonants.indexOf(word[0])
            val v2 = vowels.indexOf(word[1])
            val c2 = consonants.indexOf(word[2])
            val v1 = vowels.indexOf(word[3])
            val c1 = consonants.indexOf(word[4])

            require(c3 != -1 && v2 != -1 && c2 != -1 && v1 != -1 && c1 != -1) {
                "Invalid characters in quint: $word (valid consonants=${consonants}, vowels=${vowels})"
            }

            val value = (c3 shl 12) or (v2 shl 10) or (c2 shl 6) or (v1 shl 4) or c1
            listOf((value ushr 8).toByte(), value.toByte())
        }.toByteArray()
    }
}
