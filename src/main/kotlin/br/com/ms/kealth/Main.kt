package br.com.ms.kealth

import kotlinx.coroutines.runBlocking

fun main() {
    val a = HealthIndicatorA()
    val b = HealthIndicatorB()
    val c = HealthIndicatorC()
    val aggregator = HealthAggregator(listOf(a, b, c))

    runBlocking { println(aggregator.health()) }
}