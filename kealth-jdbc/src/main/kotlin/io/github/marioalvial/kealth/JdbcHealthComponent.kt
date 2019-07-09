package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import io.github.marioalvial.kealth.core.HealthStatus.HEALTHY
import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import javax.sql.DataSource

/**
 * Class that executes health check of the database connection.
 * @property name Name of health component
 * @property datasource Datasource that manages the connection between application and Database
 * @property timeout Timeout in seconds that the connection check may last
 */
class JdbcHealthComponent(
    override val name: String,
    override val criticalLevel: String,
    private val datasource: DataSource,
    private val timeout: Int = 3
) : HealthComponent {

    override fun doHealthCheck(): HealthStatus {
        val connection = datasource.connection
        val result = when (connection.isValid(timeout)) {
            true -> HEALTHY
            false -> UNHEALTHY
        }
        connection.close()
        return result
    }

    override fun handleFailure(throwable: Throwable) = Unit
}