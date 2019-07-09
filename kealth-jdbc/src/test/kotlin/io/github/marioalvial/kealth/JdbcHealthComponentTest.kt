package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthStatus
import io.mockk.every
import io.mockk.spyk
import io.mockk.just
import io.mockk.Runs
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLException
import javax.sql.DataSource

class JdbcHealthComponentTest {

    private val datasource = spyk<DataSource>()
    private val jdbcComponent = spyk(JdbcHealthComponent("jdbc-component", CriticalLevel.LOW, datasource))

    @Test
    fun `given valid connection with DB should execute validation successfully and return health info`() {
        every { datasource.connection.isValid(3) } returns true
        every { datasource.connection.close() } just Runs

        val healthInfo = runBlocking { jdbcComponent.health() }

        assertThat(jdbcComponent.name).isEqualTo("jdbc-component")
        assertThat(healthInfo.status).isEqualTo(HealthStatus.HEALTHY)

        verify(exactly = 0) { jdbcComponent.handleFailure(any()) }
    }

    @Test
    fun `given invalid connection with DB should throw exception and execute handle failure`() {
        every { datasource.connection } throws SQLException()
        every { datasource.connection.close() } just Runs

        val healthInfo = runBlocking { jdbcComponent.health() }

        assertThat(jdbcComponent.name).isEqualTo("jdbc-component")
        assertThat(jdbcComponent.criticalLevel).isEqualTo("LOW")
        assertThat(healthInfo.status).isEqualTo(HealthStatus.UNHEALTHY)

        verify(exactly = 1) { jdbcComponent.handleFailure(any()) }
    }

    @Test
    fun `given invalid connection with DB returns false`() {
        every { datasource.connection.isValid(3) } returns false
        every { datasource.connection.close() } just Runs

        val healthInfo = runBlocking { jdbcComponent.health() }

        assertThat(jdbcComponent.name).isEqualTo("jdbc-component")
        assertThat(jdbcComponent.criticalLevel).isEqualTo("LOW")
        assertThat(healthInfo.status).isEqualTo(HealthStatus.UNHEALTHY)

        verify(exactly = 0) { jdbcComponent.handleFailure(any()) }
    }
}