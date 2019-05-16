package io.github.marioalvial.kealth.extensions

/**
 * Function that measures the duration of the block passed as a parameter
 * @param block () -> T?
 * @return Pair<T, Long>
 */
inline fun <T> measureTimeMillisAndReturn(block: () -> T): Pair<T, Long> {
    val start = System.currentTimeMillis()
    val result = block()
    val duration = System.currentTimeMillis() - start

    return result to duration
}