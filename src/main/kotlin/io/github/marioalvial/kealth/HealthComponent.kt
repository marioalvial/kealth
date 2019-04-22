package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.HealthStatus.UNHEALTHY
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Class that abstracts a health component.
 * @property name Name of health component
 */
abstract class HealthComponent {

    abstract val name: String

    /**
     * Handle response of healthCheck() method
     * @return HealthStatus
     */
    suspend fun health(): HealthStatus = runCatching { doHealthCheck() }
        .fold(
            onSuccess = {
                if (it == UNHEALTHY) coroutineScope { launch { handleFailure() } }
                it
            },
            onFailure = {
                coroutineScope { launch { handleFailure() } }
                UNHEALTHY
            }
        )

    /**
     * If healthCheck() throws exception or return HealthStatus.UNHEALTHY executes logic to handle failure.
     * @param throwable Throwable?
     */
    protected abstract suspend fun handleFailure(throwable: Throwable? = null)

    /**
     * Execute the component's health check logic
     * @return HealthStatus
     */
    protected abstract suspend fun doHealthCheck(): HealthStatus
}
