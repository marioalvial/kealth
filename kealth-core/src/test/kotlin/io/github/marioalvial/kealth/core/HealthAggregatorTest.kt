package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.core.CriticalLevel.HIGH
import io.github.marioalvial.kealth.core.CriticalLevel.MEDIUM
import io.github.marioalvial.kealth.core.HealthStatus.HEALTHY
import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import io.github.marioalvial.kealth.testing.FirstHealthComponent
import io.github.marioalvial.kealth.testing.FourthHealthComponent
import io.github.marioalvial.kealth.testing.SecondComponent
import io.github.marioalvial.kealth.testing.Stub
import io.github.marioalvial.kealth.testing.ThirdHealthComponent
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class HealthAggregatorTest {

    private val first = spyk(FirstHealthComponent())
    private val second = spyk(SecondComponent())
    private val stub = mockk<Stub>()
    private val third = spyk(ThirdHealthComponent(stub))
    private val fourth = spyk(FourthHealthComponent(stub))

    @Test
    fun `given healthy components should return only healthy status`() {
        val aggregator = HealthAggregator(listOf(first, second))

        val results = aggregator.aggregate()

        assertThat(results)
            .hasSize(2)
            .allMatch { it.status == HEALTHY }

        coVerify(exactly = 1) { first.health() }
        coVerify(exactly = 1) { second.health() }
    }

    @Test
    fun `given unhealthy components should return only unhealthy status`() {
        val aggregator = HealthAggregator(listOf(third, fourth))

        every { stub.nothing() } just Runs

        val results = aggregator.aggregate()

        assertThat(results)
            .hasSize(2)
            .allMatch { it.status == UNHEALTHY }

        coVerify(exactly = 2) { third.health() }
        coVerify(exactly = 2) { fourth.health() }
    }

    @Test
    fun `given filter by criticalLevel should only execute health method of components that matched the predicate`() {
        val aggregator = HealthAggregator(listOf(fourth, second, third, first))

        val results = aggregator.aggregateWithFilter { _, criticalLevel -> HIGH == criticalLevel }

        assertThat(results).hasSize(2)

        coVerify(exactly = 1) { second.health() }
        coVerify(exactly = 1) { first.health() }
    }

    @Test
    fun `given filter by name should only execute health method of components that matched the predicate`() {
        val aggregator = HealthAggregator(listOf(fourth, second, third, first))

        val results = aggregator.aggregateWithFilter { name, _ -> "first-component" == name }

        assertThat(results).hasSize(1)

        coVerify(exactly = 1) { first.health() }
    }

    @Test
    fun `given filter that matches nothing should not execute any health`() {
        val aggregator = HealthAggregator(listOf(fourth, second, third, first))

        val results = aggregator.aggregateWithFilter { name, level -> name == "first-component" && level == MEDIUM }

        assertThat(results).hasSize(0)
    }
}
