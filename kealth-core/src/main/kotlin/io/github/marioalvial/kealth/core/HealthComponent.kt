package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import io.github.marioalvial.kealth.extensions.measureTimeMillisAndReturn
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Interface that abstracts a health component.
 * @property name Name of health component
 * @property criticalLevel Critical component level
 */
interface HealthComponent {

    val name: String
    val criticalLevel: String

    /**
     * Handle response of healthCheck() method
     * @return HealthStatus
     */
    suspend fun health(): HealthInfo {
        val context = context() + Dispatchers.IO

        return measureTimeMillisAndReturn {
            runCatching { withContext(context) { doHealthCheck() } }
                .fold(
                    onSuccess = { it },
                    onFailure = {
                        val job = GlobalScope.launch(context) { handleFailure(it) }
                        job.invokeOnCompletion { runBlocking { job.join() } }
                        UNHEALTHY
                    }
                )
        }
            .let { HealthInfo(it.first, criticalLevel, it.second) }
    }

    /**
     * If healthCheck() throws exception or return HealthStatus.UNHEALTHY executes logic to handle failure.
     * @param throwable Throwable
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
