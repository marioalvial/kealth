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
    open var componentContext: CoroutineContext = EmptyCoroutineContext

    /**
     * Handle response of `healthCheck()` method
     * @return HealthStatus
     */
    suspend fun health(): HealthInfo = measureTimeMillisAndReturn {
        withContext(componentContext) {
            runCatching { doHealthCheck() }
                .fold(
                    onSuccess = {
                        it
                    },
                    onFailure = {
                        launch { handleFailure(it) }

                        HealthStatus.UNHEALTHY
                    }
                )
        }
    }.let { HealthInfo(it.first, criticalLevel, it.second) }

    /**
     * If doHealthCheck() throws exception executes `handleFailure()` logic.
     * @param throwable Throwable
     */
    abstract fun handleFailure(throwable: Throwable)

    protected abstract fun doHealthCheck(): HealthStatus
}
