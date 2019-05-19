package io.github.marioalvial.kealth

import java.lang.IllegalArgumentException
import java.net.URLEncoder

/**
 * Class that transforms the parameters map in string.
 * @property parameters Parameters that will be attached to request
 */
class ParametersBuilder(
    private val parameters: Map<String, String>
) {

    /**
     * Validate the parameters map and build it as string
     * @return String
     */
    fun buildAsString(): String {
        if (parameters.isEmpty()) {
            throw IllegalArgumentException("Cannot build parameters string for empty parameters map")
        }

        val result = build().toString()

        return if (result.isNotEmpty()) result.substring(0, result.length - 1) else result
    }

    private fun build(): StringBuilder = parameters.entries.fold(StringBuilder()) { acc, entry ->
        acc.append(URLEncoder.encode(entry.key, "UTF-8"))
        acc.append("=")
        acc.append(URLEncoder.encode(entry.value, "UTF-8"))
        acc.append("&")
    }
}