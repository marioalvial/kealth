package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.core.HealthStatus.HEALTHY
import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import io.github.marioalvial.kealth.testing.*
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class HealthComponentTest {

    private val componentA = spyk(HealthComponentA())
    private val componentB = spyk(HealthComponentB())
    private val componentC = spyk(HealthComponentC())
    private val componentD = spyk(HealthComponentD())
    private val componentE = spyk(HealthComponentE())

    @Test
    fun `when execute health method of component A should execute successfully and return health info`() {
        val healthInfo = runBlocking { componentA.health() }

        assertThat(componentA.name).isEqualTo("component A")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(100).isLessThan(200)
        assertThat(healthInfo.status).isEqualTo(HEALTHY)

        verify(exactly = 0) { componentA.handleFailure(any()) }
    }

    @Test
    fun `when execute health method of component B should throw exception`() {
        val healthInfo = runBlocking { componentB.health() }

        assertThat(componentB.name).isEqualTo("component B")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(200).isLessThan(300)
        assertThat(healthInfo.status).isEqualTo(UNHEALTHY)

        verify(exactly = 1) { componentB.handleFailure(any()) }
    }

    @Test
    fun `when execute health method of component C should throw exception`() {
        val healthInfo = runBlocking { componentC.health() }

        assertThat(componentC.name).isEqualTo("component C")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(300).isLessThan(400)
        assertThat(healthInfo.status).isEqualTo(UNHEALTHY)

        verify(exactly = 1) { componentC.handleFailure(any()) }
    }

    @Test
    fun `when execute health method of component D should execute successfully and return health info`() {
        val healthInfo = runBlocking { componentD.health() }

        assertThat(componentD.name).isEqualTo("component D")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(400).isLessThan(500)
        assertThat(healthInfo.status).isEqualTo(HEALTHY)

        verify(exactly = 0) { componentD.handleFailure(any()) }
    }

    @Test
    fun `when execute health method of component E should throw exception`() {
        val healthInfo = runBlocking { componentE.health() }

        assertThat(componentE.name).isEqualTo("component E")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(500).isLessThan(600)
        assertThat(healthInfo.status).isEqualTo(UNHEALTHY)

        verify(exactly = 1) { componentE.handleFailure(any()) }
    }
}