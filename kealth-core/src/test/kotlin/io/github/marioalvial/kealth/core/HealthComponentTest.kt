package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.core.HealthStatus.HEALTHY
import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import io.github.marioalvial.kealth.testing.HealthComponentA
import io.github.marioalvial.kealth.testing.HealthComponentB
import io.github.marioalvial.kealth.testing.HealthComponentC
import io.github.marioalvial.kealth.testing.HealthComponentD
import io.github.marioalvial.kealth.testing.HealthComponentE
import io.github.marioalvial.kealth.testing.HealthComponentF
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.Test

class HealthComponentTest {

    private val componentA = spyk(HealthComponentA())
    private val componentB = spyk(HealthComponentB())
    private val componentC = spyk(HealthComponentC())
    private val componentD = spyk(HealthComponentD())
    private val componentE = spyk(HealthComponentE())
    private val componentF = spyk(HealthComponentF())

    @Test
    fun `when execute health method of component A should execute successfully and return health info`() {
        val healthInfo = runBlocking { componentA.health() }

        assertThat(componentA.name).isEqualTo("component A")
        assertThat(componentA.criticalLevel).isEqualTo("HIGH")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(100)
        assertThat(healthInfo.status).isEqualTo(HEALTHY)

        verify(exactly = 0) { componentA.handleException(any()) }
    }

    @Test
    fun `when execute health method of component B should throw exception`() {
        val healthInfo = runBlocking { componentB.health() }

        assertThat(componentB.name).isEqualTo("component B")
        assertThat(componentB.criticalLevel).isEqualTo("HIGH")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(200)
        assertThat(healthInfo.status).isEqualTo(UNHEALTHY)

        verify(exactly = 1) { componentB.handleException(any()) }
    }

    @Test
    fun `when execute health method of component B should be able to access threadLocal context`() {
        assertThatCode { runBlocking { withContext(Dispatchers.IO) { componentB.health() } } }.doesNotThrowAnyException()

        verify(exactly = 1) { componentB.handleException(any()) }
    }

    @Test
    fun `when execute health method of component C should throw exception`() {
        val healthInfo = runBlocking { componentC.health() }

        assertThat(componentC.name).isEqualTo("component C")
        assertThat(componentC.criticalLevel).isEqualTo("LOW")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(300)
        assertThat(healthInfo.status).isEqualTo(UNHEALTHY)

        verify(exactly = 1) { componentC.handleException(any()) }
    }

    @Test
    fun `when execute health method of component D should execute successfully and return health info`() {
        val healthInfo = runBlocking { componentD.health() }

        assertThat(componentD.name).isEqualTo("component D")
        assertThat(componentD.criticalLevel).isEqualTo("LOW")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(400)
        assertThat(healthInfo.status).isEqualTo(HEALTHY)

        verify(exactly = 0) { componentD.handleException(any()) }
    }

    @Test
    fun `when execute health method of component E should throw exception`() {
        val healthInfo = runBlocking { withContext(Dispatchers.Default) { componentE.health() } }

        assertThat(componentE.name).isEqualTo("component E")
        assertThat(componentE.criticalLevel).isEqualTo("HIGH")
        assertThat(healthInfo.duration).isGreaterThanOrEqualTo(500)
        assertThat(healthInfo.status).isEqualTo(UNHEALTHY)

        verify(exactly = 1) { componentE.handleException(any()) }
    }

    @Test
    fun `when execute health method of component F should execute handleUnhealthyStatus()`() {
        val healthInfo = runBlocking { componentF.health() }

        assertThat(componentF.name).isEqualTo("component F")
        assertThat(componentF.criticalLevel).isEqualTo("HIGH")
        assertThat(healthInfo.duration).isGreaterThan(350)
        assertThat(healthInfo.status).isEqualTo(UNHEALTHY)

        verify(exactly = 1) { componentF.handleUnhealthyStatus() }
    }
}