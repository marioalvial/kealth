package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import io.github.marioalvial.kealth.extensions.measureTimeMillisAndReturn
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Interface that abstracts a health component.
 * @property name Name of health component
 */
interface HealthComponent {

    val name: String

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
    fun handleFailure(throwable: Throwable)

    /**
     * Execute the component's health check logic
     * @return HealthStatus
     */
    fun doHealthCheck(): HealthStatus

    /**
     * Set shared context between threads
     * @return CoroutineContext
     */
    fun context(): CoroutineContext = EmptyCoroutineContext
}
