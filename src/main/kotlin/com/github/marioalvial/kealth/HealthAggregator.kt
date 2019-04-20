package com.github.marioalvial.kealth

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class HealthAggregator(
    private val components: List<HealthComponent>
) {

    suspend fun health(): Map<String, HealthStatus> = withContext(Default) {
        components
            .associate { it.name to async { it.health() } }
            .mapValues { it.value.await() }
    }
}