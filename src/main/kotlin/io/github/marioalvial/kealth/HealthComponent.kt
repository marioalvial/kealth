package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.HealthStatus.UNHEALTHY
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.measureTimeMillis

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
    suspend fun health(): HealthInfo {
        val context = context()

        return measureTimeMillisAndReturn {
            runCatching { withContext(Default + context) { doHealthCheck() } }
                .fold(
                    onSuccess = { it },
                    onFailure = {
                        GlobalScope.launch(context) { handleFailure(it) }
                        UNHEALTHY
                    }
                )
        }
            .let { HealthInfo(it.first, it.second) }
    }

    /**
     * If healthCheck() throws exception or return HealthStatus.UNHEALTHY executes logic to handle failure.
     * @param throwable Throwable?
     */
    abstract fun handleFailure(throwable: Throwable)

    /**
     * Execute the component's health check logic
     * @return HealthStatus
     */
    abstract fun doHealthCheck(): HealthStatus

    /**
     * Set shared context between threads
     * @return CoroutineContext
     */
    protected open fun context(): CoroutineContext = EmptyCoroutineContext
}
