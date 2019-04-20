package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.testing.HealthComponentA
import io.github.marioalvial.kealth.testing.HealthComponentB
import io.github.marioalvial.kealth.testing.HealthComponentC
import io.github.marioalvial.kealth.testing.HealthComponentD
import io.mockk.Called
import io.mockk.coVerifyAll
import io.mockk.coVerifyOrder
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class HealthAggregatorTest {

    private val componentA = spyk<HealthComponentA>()
    private val componentB = spyk<HealthComponentB>()
    private val componentC = spyk<HealthComponentC>()
    private val componentD = spyk<HealthComponentD>()

    @Test
    fun `given health components should return only health status`() {
        val aggregator = HealthAggregator(listOf(componentA, componentD))

        val healthMap = runBlocking { aggregator.health() }

        assertThat(healthMap)
            .hasSize(2)
            .doesNotContainValue(HealthStatus.UNHEALTHY)

        coVerifyAll {
            componentA.handleFailure() wasNot Called
            componentD.handleFailure() wasNot Called
        }
    }

    @Test
    fun `given unhealthy components should return only unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentB, componentC))

        val healthMap = runBlocking { aggregator.health() }

        assertThat(healthMap)
            .hasSize(2)
            .doesNotContainValue(HealthStatus.HEALTHY)

        coVerifyOrder {
            componentC.handleFailure()
            componentB.handleFailure()
        }
    }
}