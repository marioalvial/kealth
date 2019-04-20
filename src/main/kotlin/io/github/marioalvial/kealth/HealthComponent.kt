package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.HealthStatus.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Interface that abstracts a health component.
 * @property name Name of health component
 */
interface HealthComponent {

    val name: String

    /**
     * Handle response of isHealth() method
     * @return HealthStatus
     */
    suspend fun health(): HealthStatus = withContext(Default) {
        runCatching { isHealth() }
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
     * If health() throws exception or return HealthStatus.UNHEALTHY executes logic to handle failure.
     * @param throwable Throwable?
     */
    suspend fun handleFailure(throwable: Throwable? = null)

    /**
     * Execute the health check of component
      * @return HealthStatus
     */
    suspend fun isHealth(): HealthStatus
}