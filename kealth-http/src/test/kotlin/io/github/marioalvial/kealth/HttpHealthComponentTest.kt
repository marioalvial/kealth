package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthStatus
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.net.HttpURLConnection

class HttpHealthComponentTest {

    private val name = "http-component"
    private val url = "https://google.com"
    private val method = "GET"

    @Test
    fun `given valid connection attributes should execute validation successfully and return health info`() {
        val httpComponent = spyk(HttpHealthComponent(name, CriticalLevel.HIGH, url, method), recordPrivateCalls = true)

        every { httpComponent invoke "execute" withArguments listOf(any<HttpURLConnection>()) } returns 200

        val healthInfo = runBlocking { httpComponent.health() }

        assertThat(httpComponent.name).isEqualTo("http-component")
        assertThat(httpComponent.criticalLevel).isEqualTo("HIGH")
        assertThat(healthInfo.status).isEqualTo(HealthStatus.HEALTHY)

        verify(exactly = 0) { httpComponent.handleFailure(any()) }
    }

    @Test
    fun `given invalid url should return UNHEALTHY status`() {
        val url = "https://jdasuhjdsoasdajoasd.com"
        val httpComponent = spyk(HttpHealthComponent(name, CriticalLevel.HIGH, url, method), recordPrivateCalls = true)

        val healthInfo = runBlocking { httpComponent.health() }

        assertThat(httpComponent.name).isEqualTo("http-component")
        assertThat(httpComponent.criticalLevel).isEqualTo("HIGH")
        assertThat(healthInfo.status).isEqualTo(HealthStatus.UNHEALTHY)

        verify(exactly = 1) { httpComponent.handleFailure(any()) }
    }

    @Test
    fun `given invalid response should return UNHEALTHY status`() {
        val httpComponent = spyk(HttpHealthComponent(name, CriticalLevel.HIGH, url, method), recordPrivateCalls = true)

        every { httpComponent invoke "execute" withArguments listOf(any<HttpURLConnection>()) } returns 500

        val healthInfo = runBlocking { httpComponent.health() }

        assertThat(httpComponent.name).isEqualTo("http-component")
        assertThat(httpComponent.criticalLevel).isEqualTo("HIGH")
        assertThat(healthInfo.status).isEqualTo(HealthStatus.UNHEALTHY)

        verify(exactly = 1) { httpComponent.handleFailure(any()) }
    }
}
