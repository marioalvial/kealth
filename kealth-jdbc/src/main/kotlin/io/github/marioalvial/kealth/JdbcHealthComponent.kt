package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import io.github.marioalvial.kealth.core.HealthStatus.HEALTHY
import java.sql.SQLTimeoutException
import javax.sql.DataSource

/**
 * Class that executes health check of the database connection.
 * @property name Name of health component
 * @property datasource Datasource that manages the connection between application and Database
 * @property timeout Timeout in seconds that the connection check may last
 */
class JdbcHealthComponent(
    override val name: String,
    private val datasource: DataSource,
    private val timeout: Int = 3
) : HealthComponent {

    override fun doHealthCheck(): HealthStatus = when (datasource.connection.isValid(timeout)) {
        true -> HEALTHY
        false -> throw SQLTimeoutException("Could not connect to database")
    }

    override fun handleFailure(throwable: Throwable) {
    }
}