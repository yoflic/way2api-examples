/**
 * Way2API - DTH Advance Information
 * Docs: https://app.way2api.com/documentation/dth-info
 *
 * Java 11+ (java.net.http, no dependencies).
 * Run:  WAY2API_KEY=your_key java java.java
 *
 * The response is printed as raw JSON. Add Jackson or Gson if you want to
 * bind it to a class - the JDK has no JSON parser of its own.
 */

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

class Way2ApiExample {

    static final String ENDPOINT = "https://app.way2api.com/api/v1/dth/info";

    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("WAY2API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "YOUR_API_KEY";
        }

        String payload = "{\"dth_number\":\"01234567890\",\"operator\":\"dish_tv\"}";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder(URI.create(ENDPOINT))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Every Way2API response is {"success": ..., "message": ..., "data": ...}.
        // Branch on the HTTP status; parse the body to read "success".
        if (response.statusCode() >= 400) {
            System.err.println("Request failed (HTTP " + response.statusCode() + ")");
            System.err.println(response.body());
            System.exit(1);
        }

        System.out.println(response.body());
    }
}
