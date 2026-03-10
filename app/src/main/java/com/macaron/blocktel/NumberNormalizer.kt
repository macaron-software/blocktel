package com.macaron.blocktel

/**
 * Normalizes phone numbers to local French format (0XXXXXXXXX).
 * Handles +33, 0033, and raw 0-prefixed formats.
 */
object NumberNormalizer {

    private val NON_DIGIT = Regex("[^0-9+]")

    fun normalize(number: String?): String? {
        if (number.isNullOrBlank()) return null
        val digits = number.replace(NON_DIGIT, "")
        return when {
            digits.startsWith("+33") -> "0" + digits.substring(3)
            digits.startsWith("0033") -> "0" + digits.substring(4)
            digits.startsWith("0") && digits.length == 10 -> digits
            digits.startsWith("0") && digits.length > 10 -> digits
            else -> digits
        }
    }

    fun matchesAnyPrefix(number: String?, prefixes: List<String>): String? {
        val normalized = normalize(number) ?: return null
        return prefixes.firstOrNull { normalized.startsWith(it) }
    }

    fun formatForDisplay(prefix: String): String {
        return when (prefix.length) {
            4 -> "${prefix.substring(0, 2)} ${prefix.substring(2, 4)}"
            5 -> "${prefix.substring(0, 2)} ${prefix.substring(2, 5)}"
            else -> prefix
        }
    }
}
