package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.extensions.measureTimeMillisAndReturn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Interface that abstracts a health component.
 * @property name Name of health component
 * @property criticalLevel Critical component level
 * @property componentContext Set the context that `health()` will run
 */
abstract class HealthComponent {

    abstract val name: String
    abstract val criticalLevel: String
    protected open var componentContext: CoroutineContext = EmptyCoroutineContext

    /**
     * Executes Component Health Check
     */
    protected abstract fun healthCheck(): HealthStatus

    /**
     * If doHealthCheck() throws exception executes `handleFailure()` logic.
     * @param throwable Throwable
     */
    protected open fun handleFailure(throwable: Throwable) = Unit

    /**
     * If doHealthCheck() returns HealthStatus.UNHEALTHY executes `handleUnhealthy()` logic.
     */
    protected open fun handleUnhealthy() = Unit

    /**
     * Handle response of `healthCheck()` method
     * @return HealthStatus
     */
    suspend fun health(): HealthInfo = measureTimeMillisAndReturn {
        val context = componentContext

        withContext(context) {
            runCatching { healthCheck() }
                .fold(
                    onSuccess = {
                        if (it == HealthStatus.UNHEALTHY) launch { handleUnhealthy() }

                        it
                    },
                    onFailure = {
                        launch { handleFailure(it) }

                        HealthStatus.UNHEALTHY
                    }
                )
        }
    }.let { HealthInfo(it.first, criticalLevel, it.second) }
}
