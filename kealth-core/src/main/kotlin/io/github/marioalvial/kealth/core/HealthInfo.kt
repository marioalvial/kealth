package io.github.marioalvial.kealth.core

/**
 * Class that aggregates information about the execution of component's health check
 * @property status Status of health component
 * @property criticalLevel Critical component level
 * @property duration Duration of component's health check execution
 */
data class HealthInfo(
    val status: HealthStatus,
    val criticalLevel: String,
    val duration: Long
)