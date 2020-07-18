package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthInfo
import io.github.marioalvial.kealth.core.HealthStatus
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager

class JdbcHealthComponentTest {

    private val jdbcComponent = spyk(JdbcHealthComponent("jdbc-component", CriticalLevel.HIGH, "", "", ""))

    @Before
    fun setup() {
        mockkStatic(DriverManager::class)
    }

    @After
    fun tearDown() {
        mockkStatic(DriverManager::class)
    }

    @Test
    fun `given valid connection with DB should execute validation successfully and return HEALTHY HealthInfo`() {
        val connection = mockk<Connection>()
        val expectedHealthInfo = HealthInfo(HealthStatus.HEALTHY, CriticalLevel.HIGH, 0)

        every { DriverManager.getConnection(any(), any(), any()) } returns connection
        every { connection.isValid(3) } returns true
        every { connection.close() } just Runs

        val healthInfo = runBlocking { jdbcComponent.health() }

        assertThat(healthInfo).isEqualToIgnoringGivenFields(expectedHealthInfo, "duration")
    }

    @Test
    fun `given invalid connection with DB should return UNHEALTHY HealthInfo`() {
        val connection = mockk<Connection>()
        val expectedHealthInfo = HealthInfo(HealthStatus.UNHEALTHY, CriticalLevel.HIGH, 0)

        every { DriverManager.getConnection(any(), any(), any()) } returns connection
        every { connection.isValid(3) } returns false
        every { connection.close() } just Runs

        val healthInfo = runBlocking { jdbcComponent.health() }

        assertThat(healthInfo).isEqualToIgnoringGivenFields(expectedHealthInfo, "duration")
    }
}
