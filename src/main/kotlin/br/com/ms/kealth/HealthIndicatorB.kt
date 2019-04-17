package br.com.ms.kealth

import kotlinx.coroutines.delay

class HealthIndicatorB : HealthIndicator(){

    override val criticalLevel: CriticalLevel = CriticalLevel.LOW

    override val componentName = "Component B"

    override suspend fun handleFailure(throwable: Throwable) {
        delay(1500)
        println("COMPONENTE B FALHOU")
    }
    override suspend fun isHealth(): Boolean {
        delay(3000)
        println("Executando health check do componente B")
        return true
    }
}