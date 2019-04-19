package br.com.ms.kealth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class HealthIndicator {

    abstract val componentName: String

    suspend fun health(): HealthStatus = withContext(Dispatchers.Default) {
        runCatching { isHealth() }
            .fold(
                onSuccess = { it },
                onFailure = {
                    GlobalScope.launch { handleFailure(it) }
                    HealthStatus.UNHEALTHY
                }
            )
    }

    protected abstract suspend fun handleFailure(throwable: Throwable)

    protected abstract suspend fun isHealth(): HealthStatus
}