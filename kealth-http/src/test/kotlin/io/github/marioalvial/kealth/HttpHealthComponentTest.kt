package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthInfo
import io.github.marioalvial.kealth.core.HealthStatus
import io.mockk.every
import io.mockk.spyk
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
        val expectedHealthInfo = HealthInfo(HealthStatus.HEALTHY, CriticalLevel.HIGH, 0)

        every { httpComponent invoke "execute" withArguments listOf(any<HttpURLConnection>()) } returns 200

        val healthInfo = runBlocking { httpComponent.health() }

        assertThat(healthInfo).isEqualToIgnoringGivenFields(expectedHealthInfo, "duration")
    }

    @Test
    fun `given invalid url should return UNHEALTHY status`() {
        val url = "https://jdasuhjdsoasdajoasd.com"
        val expectedHealthInfo = HealthInfo(HealthStatus.UNHEALTHY, CriticalLevel.HIGH, 0)
        val httpComponent = spyk(HttpHealthComponent(name, CriticalLevel.HIGH, url, method), recordPrivateCalls = true)

        val healthInfo = runBlocking { httpComponent.health() }

        assertThat(healthInfo).isEqualToIgnoringGivenFields(expectedHealthInfo, "duration")
    }
}
