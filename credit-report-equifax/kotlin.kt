/**
 * Way2API - Credit Report Equifax
 * Docs: https://app.way2api.com/documentation/credit-report-equifax
 *
 * Kotlin on JVM 11+ (java.net.http, no dependencies).
 * Run:  WAY2API_KEY=your_key kotlinc kotlin.kt -include-runtime -d app.jar && java -jar app.jar
 *
 * On Android use OkHttp or Retrofit instead, and never ship the API key in
 * the app - call Way2API from your own backend.
 */

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.system.exitProcess

const val ENDPOINT = "https://app.way2api.com/api/v1/credit-report/fetch"

fun main() {
    val apiKey = System.getenv("WAY2API_KEY") ?: "YOUR_API_KEY"
    val payload = "{\"name\":\"Ananya Sharma\",\"mobile\":\"9876543210\",\"number\":\"123456789012\",\"fetch_by\":\"aadhaar\"}"

    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .build()

    val request = HttpRequest.newBuilder(URI.create(ENDPOINT))
        .header("Authorization", "Bearer $apiKey")
        .header("Content-Type", "application/json")
        .timeout(Duration.ofSeconds(30))
        .POST(HttpRequest.BodyPublishers.ofString(payload))
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    // Every Way2API response is {"success": ..., "message": ..., "data": ...}.
    // Branch on the HTTP status; parse the body to read "success".
    if (response.statusCode() >= 400) {
        System.err.println("Request failed (HTTP ${response.statusCode()})")
        System.err.println(response.body())
        exitProcess(1)
    }

    println(response.body())
}
