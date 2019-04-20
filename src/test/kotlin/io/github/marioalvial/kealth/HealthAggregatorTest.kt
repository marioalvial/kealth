package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.testing.HealthComponentA
import io.github.marioalvial.kealth.testing.HealthComponentB
import io.github.marioalvial.kealth.testing.HealthComponentC
import io.github.marioalvial.kealth.testing.HealthComponentD
import io.mockk.*
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
    }


    @Test
    fun `given health components should not execute handleFailure methods`() {
        val aggregator = HealthAggregator(listOf(componentA, componentD))

        runBlocking { aggregator.health() }

        coVerify { componentA.health() }
        coVerify { componentD.health() }
        coVerify { componentA.handleFailure() wasNot Called }
        coVerify { componentD.handleFailure() wasNot Called }
    }

    @Test
    fun `given unhealthy components should return only unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentB, componentC))

        val healthMap = runBlocking { aggregator.health() }

        assertThat(healthMap)
            .hasSize(2)
            .doesNotContainValue(HealthStatus.HEALTHY)
    }

    @Test
    fun `given unhealthy components should execute handleFailure method of each component`() {
        val aggregator = HealthAggregator(listOf(componentB, componentC))

        runBlocking { aggregator.health() }

        coVerifyOrder {
            componentC.handleFailure(any())
            componentB.handleFailure()
        }
    }
}