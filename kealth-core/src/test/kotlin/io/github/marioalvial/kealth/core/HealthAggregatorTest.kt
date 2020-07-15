package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.testing.HealthComponentA
import io.github.marioalvial.kealth.testing.HealthComponentB
import io.github.marioalvial.kealth.testing.HealthComponentC
import io.github.marioalvial.kealth.testing.HealthComponentD
import io.github.marioalvial.kealth.testing.HealthComponentE
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.Test

class HealthAggregatorTest {

    private val componentA = spyk<HealthComponentA>()
    private val componentB = spyk<HealthComponentB>()
    private val componentC = spyk<HealthComponentC>()
    private val componentD = spyk<HealthComponentD>()
    private val componentE = spyk<HealthComponentE>()

    @Test
    fun `given healthy components should return only healthy status`() {
        val aggregator = HealthAggregator(listOf(componentD, componentA))

        val results = aggregator.aggregate()

        assertThat(results)
            .hasSize(2)
            .allMatch { it.status == HealthStatus.HEALTHY }

        coVerify(exactly = 1) { componentA.health() }
        coVerify(exactly = 1) { componentD.health() }
        coVerify(exactly = 0) { componentA.handleFailure(any()) }
        coVerify(exactly = 0) { componentD.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy components should return only unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentC, componentB))

        val results = aggregator.aggregate()

        assertThat(results)
            .hasSize(2)
            .allMatch { it.status == HealthStatus.UNHEALTHY }

        coVerify(exactly = 1) { componentB.health() }
        coVerify(exactly = 1) { componentC.health() }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy components with thread context should execute handleFailure passing context`() {
        val aggregator = HealthAggregator(listOf(componentB))

        assertThatCode { aggregator.aggregate() }.doesNotThrowAnyException()

        verify(exactly = 1) { componentB.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy component without context should throw exception when trying to access threadLocal context`() {
        val aggregator = HealthAggregator(listOf(componentE))

        assertThatCode { aggregator.aggregate() }.doesNotThrowAnyException()

        verify(exactly = 1) { componentE.handleFailure(any()) }
    }

    @Test
    fun `throwing exception during handleFailure() execution should not cancel handleFailure() of another component`() {
        coEvery { componentB.handleFailure(any()) } throws IllegalArgumentException("Something went veeeery wrong")

        HealthAggregator(listOf(componentA, componentB, componentC, componentD)).aggregate()

        runBlocking { delay(5000) }

        coVerify(exactly = 1) { componentA.health() }
        coVerify(exactly = 1) { componentB.health() }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.health() }
        coVerify(exactly = 1) { componentD.health() }
    }

    @Test
    fun `given unhealthy and healthy components should return healthy and unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentA, componentC))

        val results = aggregator.aggregate()

        assertThat(results)
            .hasSize(4)
            .filteredOn { it.status == HealthStatus.HEALTHY }.hasSize(2)
            .filteredOn { it.status == HealthStatus.UNHEALTHY }.hasSize(2)

        coVerify(exactly = 1) { componentA.health() }
        coVerify(exactly = 1) { componentB.health() }
        coVerify(exactly = 1) { componentC.health() }
        coVerify(exactly = 1) { componentD.health() }
        coVerify(exactly = 0) { componentA.handleFailure(any()) }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.handleFailure(any()) }
        coVerify(exactly = 0) { componentD.handleFailure(any()) }
    }

    @Test
    fun `given filter by criticalLevel should only execute health method of components that matched the predicate`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentC, componentE, componentA))

        val results = aggregator.aggregateWithFilter { _, criticalLevel -> CriticalLevel.HIGH == criticalLevel }

        assertThat(results.size).isEqualTo(3)

        coVerify(exactly = 1) { componentB.health() }
        coVerify(exactly = 1) { componentE.health() }
        coVerify(exactly = 1) { componentA.health() }
    }

    @Test
    fun `given filter by name should only execute health method of components that matched the predicate`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentC, componentE, componentA))

        val results = aggregator.aggregateWithFilter { name, _ -> "component A" == name }

        assertThat(results.size).isEqualTo(1)

        coVerify(exactly = 1) { componentA.health() }
    }

    @Test
    fun `given filterBlock that matches nothing should not execute any health`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentC, componentE, componentA))

        val results = aggregator.aggregateWithFilter { name, criticalLevel ->
            name == "component A" && criticalLevel == CriticalLevel.MEDIUM
        }

        assertThat(results.size).isEqualTo(0)

        coVerify(exactly = 0) { componentA.health() }
        coVerify(exactly = 0) { componentB.health() }
        coVerify(exactly = 0) { componentC.health() }
        coVerify(exactly = 0) { componentD.health() }
        coVerify(exactly = 0) { componentE.health() }
    }
}
