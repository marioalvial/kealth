package br.com.ms.kealth

import br.com.ms.kealth.HealthStatus.*
import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

abstract class HealthIndicator {

    abstract val criticalLevel: CriticalLevel
    abstract val componentName: String

    private lateinit var result: Deferred<Boolean>

    suspend fun health(): HealthStatus = coroutineScope {
        try {
            result = async { isHealth() }
        } catch (throwable: Throwable) {
            launch { handleFailure(throwable) }
        }
        if (result.await()) HEALTH else UNHEALTHY
    }

    protected abstract suspend fun handleFailure(throwable: Throwable)

    protected abstract suspend fun isHealth(): Boolean
}