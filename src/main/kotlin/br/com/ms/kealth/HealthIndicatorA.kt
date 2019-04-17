package br.com.ms.kealth

import kotlinx.coroutines.delay

class HealthIndicatorA : HealthIndicator(){

    override val criticalLevel: CriticalLevel = CriticalLevel.HIGH

    override val componentName = "Component A"

    override suspend fun handleFailure(throwable: Throwable) {
        delay(4000)
        println("COMPONENTE A FALHOU")
    }
    override suspend fun isHealth(): Boolean {
        delay(5000)
        println("Executando health check do componente A")
        return true
    }
}