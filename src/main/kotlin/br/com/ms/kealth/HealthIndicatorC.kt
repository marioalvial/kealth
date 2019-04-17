package br.com.ms.kealth

import kotlinx.coroutines.delay

class HealthIndicatorC : HealthIndicator(){

    override val criticalLevel: CriticalLevel = CriticalLevel.HIGH

    override val componentName = "Component C"

    override suspend fun handleFailure(throwable: Throwable) {
        delay(6000)
        println("COMPONENTE C FALHOU")
    }
    override suspend fun isHealth(): Boolean {
        delay(4000)
        println("Executando health check do componente C")
        return false
    }
}