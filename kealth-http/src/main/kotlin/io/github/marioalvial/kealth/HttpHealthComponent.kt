package io.github.marioalvial.kealth

import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import io.github.marioalvial.kealth.core.HealthStatus.HEALTHY
import io.github.marioalvial.kealth.core.HealthStatus.UNHEALTHY
import java.net.HttpURLConnection
import java.net.URL
import java.io.DataOutputStream

/**
 * Class that executes health check of external dependency by HTTP request.
 * @property name Name of health component
 * @property url URL of the endpoint that will execute the health check
 * @property method Http Method
 * @property healthStatusCode Expected status code if verification succeeds
 * @property headers Map of headers that will be attached to request
 * @property parameters Parameters that will be attached to request
 * @property connectTimeout Time (in seconds) to wait for the connection to the server to be established
 * @property readTimeout Time (in seconds) to wait for data to be available for reading.
 */
class HttpHealthComponent(
    override val name: String,
    override val criticalLevel: String,
    private val url: String,
    private val method: String,
    private val healthStatusCode: Int = 200,
    private val headers: Map<String, String> = emptyMap(),
    private val parameters: Map<String, String> = emptyMap(),
    private val connectTimeout: Int = 3000,
    private val readTimeout: Int = 5000
) : HealthComponent() {

    override fun healthCheck(): HealthStatus {
        val connection = getConfiguredConnection()

        return when (execute(connection)) {
            healthStatusCode -> HEALTHY
            else -> UNHEALTHY
        }
    }

    /**
     * Execute the HTTP request and return the status code
     * @param connection HttpURLConnection
     * @return Int
     */
    private fun execute(connection: HttpURLConnection) = connection.responseCode

    /**
     * Configure a HttpURLConnection (add method, timeout configuration, headers and parameters)
     * @return HttpURLConnection
     */
    private fun getConfiguredConnection(): HttpURLConnection = (URL(url).openConnection() as? HttpURLConnection)?.also {
        it.requestMethod = method
        it.connectTimeout = connectTimeout
        it.readTimeout = readTimeout
        if (parameters.isNotEmpty()) setRequestParameters(it, parameters)
        headers.forEach { header -> it.setRequestProperty(header.key, header.value) }
    } ?: throw ClassCastException("It wasn't possible to execute the cast process")

    private fun setRequestParameters(connection: HttpURLConnection, parameters: Map<String, String>) {
        connection.doOutput = true
        val out = DataOutputStream(connection.outputStream)
        out.writeBytes(ParametersBuilder(parameters).buildAsString())
        out.flush()
        out.close()
    }
}
