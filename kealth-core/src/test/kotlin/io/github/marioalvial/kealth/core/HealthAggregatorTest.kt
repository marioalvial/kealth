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
    fun `given health components should return only healthy status`() {
        val aggregator = HealthAggregator(listOf(componentD, componentA))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap).hasSize(2)
        assertThat(healthMap.values).allMatch { it.status == HealthStatus.HEALTHY }
        assertThat(healthMap[componentD.name]?.duration).isGreaterThan(healthMap[componentA.name]?.duration)

        coVerify(exactly = 1) { componentA.health() }
        coVerify(exactly = 1) { componentD.health() }
        coVerify(exactly = 0) { componentA.handleException(any()) }
        coVerify(exactly = 0) { componentD.handleException(any()) }
    }

    @Test
    fun `given unhealthy components should return only unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentC, componentB))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap).hasSize(2)
        assertThat(healthMap.values).allMatch { it.status == HealthStatus.UNHEALTHY }
        assertThat(healthMap[componentC.name]?.duration).isGreaterThan(healthMap[componentB.name]?.duration)

        coVerify(exactly = 1) { componentB.health() }
        coVerify(exactly = 1) { componentC.health() }
        coVerify(exactly = 1) { componentB.handleException(any()) }
        coVerify(exactly = 1) { componentC.handleException(any()) }
    }

    @Test
    fun `given unhealthy components with thread context should execute handleFailure passing context`() {
        val aggregator = HealthAggregator(listOf(componentB))

        assertThatCode { aggregator.aggregate() }.doesNotThrowAnyException()

        verify(exactly = 1) { componentB.handleException(any()) }
    }

    @Test
    fun `given unhealthy component without context should throw exception when trying to access threadLocal context`() {
        val aggregator = HealthAggregator(listOf(componentE))

        assertThatCode { aggregator.aggregate() }.doesNotThrowAnyException()

        verify(exactly = 1) { componentE.handleException(any()) }
    }

    @Test
    fun `throwing exception during handleFailure() execution should not cancel handleFailure() of another component`() {
        coEvery { componentB.handleException(any()) } throws IllegalArgumentException("Something went veeeery wrong")

        HealthAggregator(listOf(componentA, componentB, componentC, componentD)).aggregate()

        runBlocking { delay(5000) }

        coVerify(exactly = 1) { componentA.health() }
        coVerify(exactly = 1) { componentB.health() }
        coVerify(exactly = 1) { componentB.handleException(any()) }
        coVerify(exactly = 1) { componentB.handleCoroutineException(any(), any()) }
        coVerify(exactly = 1) { componentC.health() }
        coVerify(exactly = 1) { componentD.health() }
    }

    @Test
    fun `given unhealthy and healthy components should return healthy and unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentA, componentC))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap).hasSize(4)
        assertThat(healthMap.values).filteredOn { it.status == HealthStatus.HEALTHY }.hasSize(2)
        assertThat(healthMap.values).filteredOn { it.status == HealthStatus.UNHEALTHY }.hasSize(2)

        coVerify(exactly = 1) { componentA.health() }
        coVerify(exactly = 1) { componentB.health() }
        coVerify(exactly = 1) { componentC.health() }
        coVerify(exactly = 1) { componentD.health() }
        coVerify(exactly = 0) { componentA.handleException(any()) }
        coVerify(exactly = 1) { componentB.handleException(any()) }
        coVerify(exactly = 1) { componentC.handleException(any()) }
        coVerify(exactly = 0) { componentD.handleException(any()) }
    }

    @Test
    fun `given healthMap assert that component A duration is less than all others components`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentA, componentC))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap[componentA.name]?.duration)
            .isLessThan(healthMap[componentB.name]?.duration)
            .isLessThan(healthMap[componentC.name]?.duration)
            .isLessThan(healthMap[componentD.name]?.duration)
    }

    @Test
    fun `given healthMap assert that component B duration is less than all others components`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentC))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap[componentB.name]?.duration)
            .isLessThan(healthMap[componentC.name]?.duration)
            .isLessThan(healthMap[componentD.name]?.duration)
    }

    @Test
    fun `given healthMap assert that component C duration is less than all others components`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentC))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap[componentC.name]?.duration)
            .isLessThan(healthMap[componentD.name]?.duration)
    }

    @Test
    fun `given filter by criticalLevel should only execute health method of components that matched the predicate`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentC, componentE, componentA))

        val healthMap = aggregator.aggregateWithFilter { _, criticalLevel -> CriticalLevel.HIGH == criticalLevel }

        assertThat(healthMap.size).isEqualTo(3)

        coVerify(exactly = 1) { componentB.health() }
        coVerify(exactly = 1) { componentE.health() }
        coVerify(exactly = 1) { componentA.health() }
    }

    @Test
    fun `given filter by name should only execute health method of components that matched the predicate`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentC, componentE, componentA))

        val healthMap = aggregator.aggregateWithFilter { name, _ -> "component A" == name }

        assertThat(healthMap.size).isEqualTo(1)

        coVerify(exactly = 1) { componentA.health() }
    }

    @Test
    fun `given filterBlock that matches nothing should not execute any health`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentC, componentE, componentA))

        val healthMap = aggregator.aggregateWithFilter { name, criticalLevel ->
            name == "component A" && criticalLevel == CriticalLevel.MEDIUM
        }

        assertThat(healthMap.size).isEqualTo(0)

        coVerify(exactly = 0) { componentA.health() }
        coVerify(exactly = 0) { componentB.health() }
        coVerify(exactly = 0) { componentC.health() }
        coVerify(exactly = 0) { componentD.health() }
        coVerify(exactly = 0) { componentE.health() }
    }
}