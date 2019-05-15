package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.extensions.measureTimeMillisAndReturn
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.assertj.core.data.Percentage
import org.junit.Test

class TimingFunctionsTest {

    @Test
    fun `given block should execute it and return the time elapsed during block execution and block return value`(){
        val expectedValue = "Value"
        val expectedDuration = 1000L

        val (actualValue, actualDuration) = measureTimeMillisAndReturn {
            Thread.sleep(1000)
            "Value"
        }

        assertThat(actualValue).isEqualTo(expectedValue)
        assertThat(actualDuration).isCloseTo(expectedDuration, Percentage.withPercentage(10.0))
        assertThat(actualDuration).isGreaterThanOrEqualTo(expectedDuration)
    }
}