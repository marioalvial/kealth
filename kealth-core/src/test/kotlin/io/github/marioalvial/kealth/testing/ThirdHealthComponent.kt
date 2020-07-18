package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus

class ThirdHealthComponent(private val stub: Stub) : HealthComponent() {

    override val name = "third-component"
    override val criticalLevel = CriticalLevel.LOW

    override fun healthCheck(): HealthStatus {
        println("Starting isHealth of $name")

        Thread.sleep(300)

        return HealthStatus.UNHEALTHY
    }

    override fun handleUnhealthy() {
        stub.nothing()

        println("Handling unhealthy status of $name")

        Thread.sleep(300)

        println("Finish unhealthy logic of $name - 300ms")
    }

    override fun handleFailure(throwable: Throwable) {
        stub.nothing()
    }
}
