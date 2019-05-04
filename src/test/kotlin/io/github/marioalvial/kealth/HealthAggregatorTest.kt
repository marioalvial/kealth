package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.testing.*
import io.mockk.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.lang.Exception

class HealthAggregatorTest {

    private val componentA = spyk<HealthComponentA>()
    private val componentB = spyk<HealthComponentB>()
    private val componentC = spyk<HealthComponentC>()
    private val componentD = spyk<HealthComponentD>()

    @Test
    fun `given health components should return only healthy status`() {
        val aggregator = HealthAggregator(listOf(componentD, componentA))

        val healthMap = runBlocking { aggregator.aggregate() }

        assertThat(healthMap)
            .hasSize(2)
            .doesNotContainValue(HealthStatus.UNHEALTHY)

        coVerify(exactly = 2) { componentA.health() }
        coVerify(exactly = 2) { componentD.health() }
        coVerify(exactly = 0) { componentA.handleFailure(any()) }
        coVerify(exactly = 0) { componentD.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy components should return only unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentC, componentB))

        val healthMap = runBlocking { aggregator.aggregate() }

        assertThat(healthMap)
            .hasSize(2)
            .doesNotContainValue(HealthStatus.HEALTHY)

        coVerify(exactly = 2) { componentB.health() }
        coVerify(exactly = 2) { componentC.health() }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy components with thread context should execute handleFailure passing context`() {
        val aggregator = HealthAggregator(listOf(componentB))

        assertThatCode { runBlocking { aggregator.aggregate() } }.doesNotThrowAnyException()
    }

    @Test
    fun `throwing exception during handleFailure() execution should not cancel handleFailure() of another component`() {
        coEvery { componentB.doHealthCheck() } throws RuntimeException()
        coEvery { componentB.handleFailure(any()) } throws RuntimeException()
        coEvery { componentA.doHealthCheck() } throws RuntimeException()
        coEvery { componentC.doHealthCheck() } throws RuntimeException()
        coEvery { componentD.doHealthCheck() } throws RuntimeException()

        runBlocking { HealthAggregator(listOf(componentA, componentB, componentC, componentD)).aggregate() }

        runBlocking { delay(5000) }

        coVerify(exactly = 1) { componentA.handleFailure(any()) }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.handleFailure(any()) }
        coVerify(exactly = 1) { componentD.handleFailure(any()) }
    }

    @Test
    fun `given unhealthy and healthy components should return healthy and unhealthy status`() {
        val aggregator = HealthAggregator(listOf(componentA, componentB, componentC, componentD))

        val healthMap = runBlocking { aggregator.aggregate() }

        assertThat(healthMap)
            .hasSize(4)
            .containsValues(HealthStatus.HEALTHY, HealthStatus.UNHEALTHY)

        coVerify(exactly = 2) { componentA.health() }
        coVerify(exactly = 2) { componentB.health() }
        coVerify(exactly = 2) { componentC.health() }
        coVerify(exactly = 2) { componentD.health() }
        coVerify(exactly = 0) { componentA.handleFailure(any()) }
        coVerify(exactly = 1) { componentB.handleFailure(any()) }
        coVerify(exactly = 1) { componentC.handleFailure(any()) }
        coVerify(exactly = 0) { componentD.handleFailure(any()) }
    }
}