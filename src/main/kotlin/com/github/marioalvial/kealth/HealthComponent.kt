package com.github.marioalvial.kealth

import com.github.marioalvial.kealth.HealthStatus.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class HealthComponent {

    abstract val name: String

    suspend fun health(): HealthStatus = withContext(Default) {
        runCatching { isHealth() }
            .fold(
                onSuccess = { it },
                onFailure = {
                    launch { handleFailure(it) }
                    UNHEALTHY
                }
            )
    }

    protected abstract suspend fun handleFailure(throwable: Throwable)

    protected abstract suspend fun isHealth(): HealthStatus
}