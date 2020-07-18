package io.github.marioalvial.kealth.core

/**
 * Represent result of component's health
 * @property name Name of health component
 * @property status Status returned by health check
 * @property criticalLevel Critical component level
 * @property duration Duration of component's health check execution
 */
data class HealthComponentResult(
    val name: String,
    val status: HealthStatus,
    val criticalLevel: String,
    val duration: Long
) {

    companion object {
        /**
         * Create a HealthComponentResult.
         * @param healthComponentName Throwable
         * @param healthInfo HealthInfo
         * @return HealthComponentResult
         */
        fun create(healthComponentName: String, healthInfo: HealthInfo): HealthComponentResult = HealthComponentResult(
            name = healthComponentName,
            status = healthInfo.status,
            criticalLevel = healthInfo.criticalLevel,
            duration = healthInfo.duration
        )
    }
}
