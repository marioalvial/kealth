package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.HealthStatus.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default

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
    suspend fun health(): HealthStatus = withContext(Default) {
        runCatching { doHealthCheck() }
            .fold(
                onSuccess = {
                    if (it == UNHEALTHY) GlobalScope.launch { handleFailure() }
                    it
                },
                onFailure = {
                    GlobalScope.launch { handleFailure(it) }
                    UNHEALTHY
                }
            )
    }

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