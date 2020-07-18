package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.core.HealthStatus.HEALTHY
import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import io.github.marioalvial.kealth.testing.FirstHealthComponent
import io.github.marioalvial.kealth.testing.SecondComponent
import io.github.marioalvial.kealth.testing.ThirdHealthComponent
import io.github.marioalvial.kealth.testing.FourthHealthComponent
import io.github.marioalvial.kealth.testing.Stub
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class HealthComponentTest {

    private val first = spyk(FirstHealthComponent())
    private val second = spyk(SecondComponent())
    private val stub = mockk<Stub>()
    private val third = spyk(ThirdHealthComponent(stub))
    private val fourth = spyk(FourthHealthComponent(stub))

    @Test
    fun `when healthCheck returns HEALTHY should not execute any fallback function`() {
        val expectedHealthInfo = HealthInfo(HEALTHY, CriticalLevel.HIGH, 0)
        val healthInfo = runBlocking { first.health() }

        assertThat(healthInfo).isEqualToIgnoringGivenFields(expectedHealthInfo, "duration")
    }

    @Test
    fun `given context the health component should be able to access it`() {
        val expectedHealthInfo = HealthInfo(HEALTHY, CriticalLevel.HIGH, 0)
        val healthInfo = runBlocking { second.health() }

        assertThat(healthInfo).isEqualToIgnoringGivenFields(expectedHealthInfo, "duration")
    }

    @Test
    fun `given unhealthy status the component should executes handleUnhealthy logic`() {
        val expectedHealthInfo = HealthInfo(UNHEALTHY, CriticalLevel.LOW, 0)

        every { stub.nothing() } just Runs

        val healthInfo = runBlocking { third.health() }

        assertThat(healthInfo).isEqualToIgnoringGivenFields(expectedHealthInfo, "duration")
        verify(exactly = 1) { stub.nothing() }
    }

    @Test
    fun `when healthCheck function throws exception should executes handleFailure logic`() {
        val expectedHealthInfo = HealthInfo(UNHEALTHY, CriticalLevel.MEDIUM, 0)

        every { stub.nothing() } just Runs

        val healthInfo = runBlocking { fourth.health() }

        assertThat(healthInfo).isEqualToIgnoringGivenFields(expectedHealthInfo, "duration")

        verify(exactly = 1) { stub.nothing() }
    }
}
