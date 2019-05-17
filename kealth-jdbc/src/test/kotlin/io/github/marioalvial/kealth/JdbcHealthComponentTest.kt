package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.HealthStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.sql.SQLException
import javax.sql.DataSource

class JdbcHealthComponentTest {

    private val datasource = mockk<DataSource>()
    private val jdbcComponent = spyk(JdbcHealthComponent("jdbc-component", datasource))

    @Test
    fun `given valid connection with DB should execute validation successfully and return health info`() {
        every { datasource.connection.isValid(3) } returns true

        val healthInfo = runBlocking { jdbcComponent.health() }

        assertThat(jdbcComponent.name).isEqualTo("jdbc-component")
        assertThat(healthInfo.duration).isLessThan(200)
        assertThat(healthInfo.status).isEqualTo(HealthStatus.HEALTHY)

        verify(exactly = 0) { jdbcComponent.handleFailure(any()) }
    }

    @Test
    fun `given invalid connection with DB should throw exception and execute handle failure`() {
        every { datasource.connection } throws SQLException()

        val healthInfo = runBlocking { jdbcComponent.health() }

        assertThat(jdbcComponent.name).isEqualTo("jdbc-component")
        assertThat(healthInfo.duration).isLessThan(200)
        assertThat(healthInfo.status).isEqualTo(HealthStatus.UNHEALTHY)

        verify(exactly = 1) { jdbcComponent.handleFailure(any()) }
    }
}