package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.testing.HealthComponentA
import io.github.marioalvial.kealth.testing.HealthComponentB
import io.github.marioalvial.kealth.testing.HealthComponentC
import io.github.marioalvial.kealth.testing.HealthComponentD
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
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

    @Test
    fun `given health components should return only healthy status`() {
        val aggregator = HealthAggregator(listOf(componentD, componentA))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap).hasSize(2)
        assertThat(healthMap.values).allMatch { it.status == HealthStatus.HEALTHY }
        assertThat(healthMap[componentD.name]?.duration).isGreaterThan(healthMap[componentA.name]?.duration)

        coVerify(exactly = 2) { componentA.health() }
        coVerify(exactly = 2) { componentD.health() }
        coVerify(exactly = 0) { componentA.handleFailure(any()) }
        coVerify(exactly = 0) { componentD.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy components should return only unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentC, componentB))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap).hasSize(2)
        assertThat(healthMap.values).allMatch { it.status == HealthStatus.UNHEALTHY }
        assertThat(healthMap[componentC.name]?.duration).isGreaterThan(healthMap[componentB.name]?.duration)

        coVerify(exactly = 2) { componentB.health() }
        coVerify(exactly = 2) { componentC.health() }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy components with thread context should execute handleFailure passing context`() {
        val aggregator = HealthAggregator(listOf(componentB))

        assertThatCode { aggregator.aggregate() }.doesNotThrowAnyException()
    }

    @Test
    fun `throwing exception during handleFailure() execution should not cancel handleFailure() of another component`() {
        coEvery { componentB.doHealthCheck() } throws RuntimeException()
        coEvery { componentB.handleFailure(any()) } throws RuntimeException()
        coEvery { componentA.doHealthCheck() } throws RuntimeException()
        coEvery { componentC.doHealthCheck() } throws RuntimeException()
        coEvery { componentD.doHealthCheck() } throws RuntimeException()

        HealthAggregator(listOf(componentA, componentB, componentC, componentD)).aggregate()

        runBlocking { delay(5000) }

        coVerify(exactly = 1) { componentA.handleFailure(any()) }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.handleFailure(any()) }
        coVerify(exactly = 1) { componentD.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy and healthy components should return healthy and unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentD, componentB, componentA, componentC))

        val healthMap = aggregator.aggregate()

        assertThat(healthMap).hasSize(4)
        assertThat(healthMap.values).filteredOn { it.status == HealthStatus.HEALTHY }.hasSize(2)
        assertThat(healthMap.values).filteredOn { it.status == HealthStatus.UNHEALTHY }.hasSize(2)

        coVerify(exactly = 2) { componentA.health() }
        coVerify(exactly = 2) { componentB.health() }
        coVerify(exactly = 2) { componentC.health() }
        coVerify(exactly = 2) { componentD.health() }
        coVerify(exactly = 0) { componentA.handleFailure(any()) }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.handleFailure(any()) }
        coVerify(exactly = 0) { componentD.handleFailure(any()) }
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
}