package io.github.marioalvial.kealth.core

class HealthComponentResult(
    val name: String,
    val status: HealthStatus,
    val criticalLevel: String,
    val duration: Long
) {

    companion object{
        fun create(healthComponentName: String, healthInfo: HealthInfo) = HealthComponentResult(
            name = healthComponentName,
            status = healthInfo.status,
            criticalLevel = healthInfo.criticalLevel,
            duration = healthInfo.duration
        )
    }
}
