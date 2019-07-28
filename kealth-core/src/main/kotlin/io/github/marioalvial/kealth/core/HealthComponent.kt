package io.github.marioalvial.kealth.core

import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import io.github.marioalvial.kealth.extensions.measureTimeMillisAndReturn
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
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
    private val parameters: MutableMap<Any, Any> = mutableMapOf()
    private val context by lazy {
        CoroutineExceptionHandler { ctx, exception -> handleCoroutineException(ctx, exception) } + componentContext
    }

    /**
     * Handle response of `healthCheck()` method
     * @return HealthStatus
     */
    suspend fun health(): HealthInfo = measureTimeMillisAndReturn {
        runCatching { withContext(componentContext) { doHealthCheck() } }
            .fold(
                onSuccess = {
                    if (it == UNHEALTHY) handleUnhealthyStatus()

                    it
                },
                onFailure = {
                    handleThrowable(it)

                    UNHEALTHY
                }
            )
    }
        .let { HealthInfo(it.first, criticalLevel, it.second) }

    private fun handleThrowable(throwable: Throwable) {
        val errorScope = CoroutineScope(IO + context)

        errorScope
            .launch { handleException(throwable) }
            .invokeOnCompletion { errorScope.cancel() }
    }

    /**
     * If doHealthCheck() throws exception executes `handleException()` logic.
     * @param throwable Throwable
     */
    abstract fun handleException(throwable: Throwable)

    /**
     * If coroutine execution throws exception should execute `handleCoroutineException()` logic.
     * @param coroutineContext CoroutineContext
     * @param exception Throwable
     */
    open fun handleCoroutineException(coroutineContext: CoroutineContext, exception: Throwable) = Unit

    /**
     * If doHealthCheck() returns UNHEALTHY status executes handleUnhealthyStatus logic.
     */
    open fun handleUnhealthyStatus() = Unit

    /**
     * Execute the component's health check logic
     * @return HealthStatus
     */
    protected abstract fun doHealthCheck(): HealthStatus

    /**
     * Set shared componentContext between threads
     * @return CoroutineContext
     */

    /**
     * Add parameter to parameters map
     * @param key Any
     * @param value Any
     */
    protected fun addParameter(key: Any, value: Any) {
        parameters[key] = value
    }

    /**
     * Get parameters map
     * @return Map<Any, Any>
     */
    protected fun parameters(): Map<Any, Any> = parameters.toMap()
}