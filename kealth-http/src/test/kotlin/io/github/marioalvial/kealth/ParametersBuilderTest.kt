package io.github.marioalvial.kealth

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test

class ParametersBuilderTest {

    @Test
    fun `given not empty parameters map should create parameters string successfully`() {
        val parameters = mapOf("first" to "firstValue", "second" to "secondValue")

        assertThat(ParametersBuilder(parameters).buildAsString()).isEqualTo("first=firstValue&second=secondValue")
    }

    @Test
    fun `given empty parameters map should throw IllegalArgumentException`() {
        val parameters = emptyMap<String, String>()

        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { ParametersBuilder(parameters).buildAsString() }
            .withMessage("Cannot build parameters string for empty parameters map")
    }
}