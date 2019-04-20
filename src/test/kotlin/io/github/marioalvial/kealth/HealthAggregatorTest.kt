package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.testing.HealthComponentA
import io.github.marioalvial.kealth.testing.HealthComponentB
import io.github.marioalvial.kealth.testing.HealthComponentC
import io.github.marioalvial.kealth.testing.HealthComponentD
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.lang.invoke.MethodHandles.catchException

class HealthAggregatorTest {

    private val componentA = spyk<HealthComponentA>(recordPrivateCalls = true)
    private val componentB = spyk<HealthComponentB>(recordPrivateCalls = true)
    private val componentC = spyk<HealthComponentC>(recordPrivateCalls = true)
    private val componentD = spyk<HealthComponentD>(recordPrivateCalls = true)

    @Test
    fun `given health components should return only healthy status`() {
        val aggregator = HealthAggregator(listOf(componentA, componentD))

        val healthMap = runBlocking { aggregator.aggregate() }

        assertThat(healthMap)
            .hasSize(2)
            .doesNotContainValue(HealthStatus.UNHEALTHY)

        coVerify { componentA.health() }
        coVerify { componentD.health() }
    }

    @Test
    fun `given unhealthy components should return only unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentB, componentC))

        val healthMap = runBlocking { aggregator.aggregate() }

        assertThat(healthMap)
            .hasSize(2)
            .doesNotContainValue(HealthStatus.HEALTHY)

        coVerify { componentB.health() }
        coVerify { componentC.health() }
    }

    @Test
    fun `given unhealthy and healthy components should return healthy and unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentA, componentB, componentC, componentD))

        val healthMap = runBlocking { aggregator.aggregate() }

        assertThat(healthMap)
            .hasSize(4)
            .containsValues(HealthStatus.HEALTHY, HealthStatus.UNHEALTHY)

        coVerify { componentA.health() }
        coVerify { componentB.health() }
        coVerify { componentC.health() }
        coVerify { componentD.health() }
    }
}