package io.github.marioalvial.kealth.components

import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import io.github.marioalvial.kealth.core.HealthStatus.*
import java.sql.SQLTimeoutException
import javax.sql.DataSource

abstract class JdbcHealthComponent(
    override val name: String,
    private val datasource: DataSource,
    val timeout: Int = 3
) : HealthComponent {

    val metadata by lazy { datasource.connection.metaData }

    override fun doHealthCheck(): HealthStatus =
        if(datasource.connection.isValid(timeout))
            HEALTHY
        else
            throw SQLTimeoutException("Could not connect to database")
}