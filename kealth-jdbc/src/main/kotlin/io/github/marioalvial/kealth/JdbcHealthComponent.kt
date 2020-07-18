package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import io.github.marioalvial.kealth.core.HealthStatus.HEALTHY
import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import java.sql.DriverManager

/**
 * Class that executes health check of the database connection.
 * @property name Name of health component
 * @property jdbcUrl JDBC Url
 * @property dbUsername Database Username
 * @property dbPassword Database Password
 * @property timeout Timeout in seconds that the connection check may last
 */
class JdbcHealthComponent(
    override val name: String,
    override val criticalLevel: String,
    private val jdbcUrl: String,
    private val dbUsername: String,
    private val dbPassword: String,
    private val timeout: Int = 3
) : HealthComponent() {

    override fun healthCheck(): HealthStatus {
        val connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)

        return when (connection.isValid(timeout)) {
            true -> HEALTHY
            false -> UNHEALTHY
        }.also { connection.close() }
    }
}
