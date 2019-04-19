package br.com.ms.kealth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class   HealthAggregator(
    private val components: List<HealthComponent>
) {

    suspend fun health(): List<HealthResponse> = withContext(Dispatchers.Default) {
        components
            .map { it.name to async { it.health() } }
            .map { HealthResponse(it.first, it.second.await()) }
    }
}