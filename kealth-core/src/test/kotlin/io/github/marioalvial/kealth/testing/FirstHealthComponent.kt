package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus

class FirstHealthComponent : HealthComponent() {

    override val name = "first-component"
    override val criticalLevel = CriticalLevel.HIGH

    override fun healthCheck(): HealthStatus {
        println("Starting isHealth of $name")

        Thread.sleep(100)

        println("Finish isHealth of $name - 100ms")

        return HealthStatus.HEALTHY
    }
}
