package br.com.ms.kealth

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class HealthAggregator(
    private val indicators: List<HealthIndicator>
) {

    suspend fun health(): List<UnhealthyComponentResponse> {

        val unhealthyIndicators = indicators.filter { it.health() == HealthStatus.UNHEALTHY }
        if (unhealthyIndicators.any { it.criticalLevel == CriticalLevel.HIGH }) {
            throw UnhealthyException(unhealthyIndicators)
        }
        return unhealthyIndicators.map { UnhealthyComponentResponse(it.componentName, it.criticalLevel.name) }
    }
}

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.map { it.await() }
}

//suspend fun <T, B> Iterable<T>.pfilter(predicate: suspend (T) -> Boolean): List<T> {//coroutineScope {scope: CoroutineScope ->
//    val eae: Iterable<T> = this
//    val channel = Channel<Deferred<Boolean>>()
//    this.forEach {
//        val deferred: Deferred<Boolean> = withContext(Dispatchers.Default) {
//            async { predicate(it) }
//        }
//        channel.send(deferred)
//    }
//    return channel.receive()
//    return this.map { withContext(Dispatchers.Default) { async { predicate(it) } }}.filter { it }

//}