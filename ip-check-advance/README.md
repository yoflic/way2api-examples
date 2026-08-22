# IP Check Advance API

**IP Check Advance API** — Deep IP intelligence for any **IPv4 or IPv6** address. Beyond basic geolocation (city, country, coordinates, time zone), it returns rich **network** and **risk** signals: **ISP**, **organization**, **ASN**, **connection type**, **user type**, a **static IP score**, and anonymizer flags — **is_anonymous**, **is_anonymous_vpn**, **is_public_proxy**, **is_residential_proxy** and **is_tor_exit_node**. Built for fraud scoring, login-risk checks, and geo-targeting. **ip_address is optional** — omit it to auto-detect and look up the IP that called this API. Fields the source cannot resolve for a given address are omitted from the result.

Copy-paste integration examples in cURL, Python, Node.js, PHP, Java, C#, Go, Ruby, Kotlin and Swift.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/ip/check_advance` |
| **Category** | IP and Network Intelligence |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `ip_check_advance` |
| **Full documentation** | [https://app.way2api.com/documentation/ip-check-advance](https://app.way2api.com/documentation/ip-check-advance) |

## Authentication

Send your API key on every request, either as a bearer token or as `X-API-Key`:

```http
Authorization: Bearer YOUR_API_KEY
```

```http
X-API-Key: YOUR_API_KEY
```

Get a key at [https://app.way2api.com/register](https://app.way2api.com/register).

## Request parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| `ip_address` | string | No | IPv4 or IPv6 address to look up (e.g. 122.180.145.105 or 2001:db8::1). Optional — if omitted, the IP that called this API is auto-detected and used. |

## Code examples

Each file below is runnable as-is once you set `WAY2API_KEY`. Pick your language:

| Language | File | Requirements |
|---|---|---|
| cURL | [`curl.txt`](./curl.txt) | Command line. Works anywhere curl is installed. |
| Python | [`python.py`](./python.py) | Python 3 with the requests library. |
| Node.js | [`node.js`](./node.js) | Node.js 18+ built-in fetch. No dependencies. |
| PHP | [`php.php`](./php.php) | PHP 8 with the cURL extension. No Composer packages. |
| Java | [`java.java`](./java.java) | Java 11+ java.net.http. No dependencies. |
| C# | [`csharp.cs`](./csharp.cs) | .NET 6+ HttpClient and System.Text.Json. |
| Go | [`go.go`](./go.go) | Go 1.18+ standard library only. |
| Ruby | [`ruby.rb`](./ruby.rb) | Ruby 2.6+ standard library (net/http). |
| Kotlin | [`kotlin.kt`](./kotlin.kt) | Kotlin on JVM 11+. Android friendly. |
| Swift | [`swift.swift`](./swift.swift) | Swift 5 URLSession. iOS and macOS. |

### cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/ip/check_advance" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "ip_address": "122.180.149.100"
  }'
```

### Python

```python
import json
import os
import sys

import requests

API_KEY = os.environ.get("WAY2API_KEY", "YOUR_API_KEY")
ENDPOINT = "https://app.way2api.com/api/v1/ip/check_advance"

payload = {
    "ip_address": "122.180.149.100"
}

headers = {
    "Authorization": f"Bearer {API_KEY}",
    "Content-Type": "application/json",
}


def main() -> int:
    try:
        response = requests.post(ENDPOINT, headers=headers, json=payload, timeout=30)
    except requests.RequestException as exc:
        print(f"Network error: {exc}", file=sys.stderr)
        return 1

    try:
        body = response.json()
    except ValueError:
        print(f"Non-JSON response (HTTP {response.status_code})", file=sys.stderr)
        return 1

    # Every Way2API response is {"success": ..., "message": ..., "data": ...}.
    # Check both the HTTP status and the success flag.
    if not response.ok or not body.get("success"):
        print(f"Request failed (HTTP {response.status_code}): {body.get('message')}", file=sys.stderr)
        order_id = (body.get("data") or {}).get("order_id")
        if order_id:
            print(f"Order ID (quote this in support requests): {order_id}", file=sys.stderr)
        return 1

    print(json.dumps(body["data"]["result"], indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
```

### Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/ip/check_advance";

const payload = {
  "ip_address": "122.180.149.100"
};

async function main() {
  const response = await fetch(ENDPOINT, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${API_KEY}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  const body = await response.json();

  // Every Way2API response is { success, message, data }.
  // Check both the HTTP status and the success flag.
  if (!response.ok || body.success !== true) {
    console.error(`Request failed (HTTP ${response.status}): ${body.message}`);
    if (body.data?.order_id) {
      console.error(`Order ID (quote this in support requests): ${body.data.order_id}`);
    }
    process.exitCode = 1;
    return;
  }

  console.log(JSON.stringify(body.data.result, null, 2));
}

main().catch((error) => {
  console.error(`Network or parsing error: ${error.message}`);
  process.exitCode = 1;
});
```

### PHP

```php
declare(strict_types=1);

$apiKey   = getenv('WAY2API_KEY') ?: 'YOUR_API_KEY';
$endpoint = 'https://app.way2api.com/api/v1/ip/check_advance';

$payload = [
    'ip_address' => '122.180.149.100'
];

$ch = curl_init($endpoint);
curl_setopt_array($ch, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST           => true,
    CURLOPT_HTTPHEADER     => [
        'Authorization: Bearer ' . $apiKey,
        'Content-Type: application/json',
    ],
    CURLOPT_POSTFIELDS     => json_encode($payload),
    CURLOPT_TIMEOUT        => 30,
]);

$raw    = curl_exec($ch);
$status = (int) curl_getinfo($ch, CURLINFO_RESPONSE_CODE);
$netErr = curl_error($ch);
curl_close($ch);

if ($raw === false) {
    fwrite(STDERR, "Network error: {$netErr}\n");
    exit(1);
}

$body = json_decode((string) $raw, true);

// Every Way2API response is ['success' => ..., 'message' => ..., 'data' => ...].
// Check both the HTTP status and the success flag.
if ($status >= 400 || empty($body['success'])) {
    fwrite(STDERR, "Request failed (HTTP {$status}): " . ($body['message'] ?? '') . "\n");
    if (!empty($body['data']['order_id'])) {
        fwrite(STDERR, "Order ID (quote this in support requests): {$body['data']['order_id']}\n");
    }
    exit(1);
}

echo json_encode($body['data']['result'], JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES), "\n";
```

### Java

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

class Way2ApiExample {

    static final String ENDPOINT = "https://app.way2api.com/api/v1/ip/check_advance";

    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("WAY2API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "YOUR_API_KEY";
        }

        String payload = "{\"ip_address\":\"122.180.149.100\"}";

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
```

### C#

```csharp
using System;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

class Program
{
    const string Endpoint = "https://app.way2api.com/api/v1/ip/check_advance";

    static async Task<int> Main()
    {
        var apiKey = Environment.GetEnvironmentVariable("WAY2API_KEY") ?? "YOUR_API_KEY";
        var payload = "{\"ip_address\":\"122.180.149.100\"}";

        using var client = new HttpClient { Timeout = TimeSpan.FromSeconds(30) };
        using var request = new HttpRequestMessage(HttpMethod.Post, Endpoint)
        {
            Content = new StringContent(payload, Encoding.UTF8, "application/json"),
        };
        request.Headers.Add("Authorization", $"Bearer {apiKey}");

        using var response = await client.SendAsync(request);
        var raw = await response.Content.ReadAsStringAsync();

        using var document = JsonDocument.Parse(raw);
        var root = document.RootElement;

        // Every Way2API response is { success, message, data }.
        // Check both the HTTP status and the success flag.
        var ok = root.TryGetProperty("success", out var success) && success.GetBoolean();
        if (!response.IsSuccessStatusCode || !ok)
        {
            var message = root.TryGetProperty("message", out var m) ? m.GetString() : "";
            Console.Error.WriteLine($"Request failed (HTTP {(int)response.StatusCode}): {message}");
            if (root.TryGetProperty("data", out var data) &&
                data.TryGetProperty("order_id", out var orderId))
            {
                Console.Error.WriteLine($"Order ID (quote this in support requests): {orderId.GetString()}");
            }
            return 1;
        }

        var result = root.GetProperty("data").GetProperty("result");
        Console.WriteLine(JsonSerializer.Serialize(result, new JsonSerializerOptions { WriteIndented = true }));
        return 0;
    }
}
```

### Go

```go
package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"time"
)

const endpoint = "https://app.way2api.com/api/v1/ip/check_advance"

func main() {
	apiKey := os.Getenv("WAY2API_KEY")
	if apiKey == "" {
		apiKey = "YOUR_API_KEY"
	}

	payload := map[string]interface{}{
		"ip_address": "122.180.149.100",
	}

	body, err := json.Marshal(payload)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Could not encode payload: %v\n", err)
		os.Exit(1)
	}

	req, err := http.NewRequest(http.MethodPost, endpoint, bytes.NewReader(body))
	if err != nil {
		fmt.Fprintf(os.Stderr, "Could not build request: %v\n", err)
		os.Exit(1)
	}
	req.Header.Set("Authorization", "Bearer "+apiKey)
	req.Header.Set("Content-Type", "application/json")

	client := &http.Client{Timeout: 30 * time.Second}
	resp, err := client.Do(req)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Network error: %v\n", err)
		os.Exit(1)
	}
	defer resp.Body.Close()

	raw, _ := io.ReadAll(resp.Body)

	// Every Way2API response is {"success": ..., "message": ..., "data": ...}.
	// Check both the HTTP status and the success flag.
	var parsed struct {
		Success bool            `json:"success"`
		Message string          `json:"message"`
		Data    struct {
			OrderID string          `json:"order_id"`
			Result  json.RawMessage `json:"result"`
		} `json:"data"`
	}
	if err := json.Unmarshal(raw, &parsed); err != nil {
		fmt.Fprintf(os.Stderr, "Non-JSON response (HTTP %d)\n", resp.StatusCode)
		os.Exit(1)
	}

	if resp.StatusCode >= 400 || !parsed.Success {
		fmt.Fprintf(os.Stderr, "Request failed (HTTP %d): %s\n", resp.StatusCode, parsed.Message)
		if parsed.Data.OrderID != "" {
			fmt.Fprintf(os.Stderr, "Order ID (quote this in support requests): %s\n", parsed.Data.OrderID)
		}
		os.Exit(1)
	}

	var pretty bytes.Buffer
	json.Indent(&pretty, parsed.Data.Result, "", "  ")
	fmt.Println(pretty.String())
}
```

### Ruby

```ruby
require 'json'
require 'net/http'
require 'uri'

API_KEY  = ENV.fetch('WAY2API_KEY', 'YOUR_API_KEY')
ENDPOINT = 'https://app.way2api.com/api/v1/ip/check_advance'.freeze

payload = {
  'ip_address' => '122.180.149.100'
}

uri = URI(ENDPOINT)

request = Net::HTTP::Post.new(uri)
request['Authorization'] = "Bearer #{API_KEY}"
request['Content-Type']  = 'application/json'
request.body = JSON.generate(payload)

response = Net::HTTP.start(uri.hostname, uri.port, use_ssl: uri.scheme == 'https', read_timeout: 30) do |http|
  http.request(request)
end

begin
  body = JSON.parse(response.body)
rescue JSON::ParserError
  warn "Non-JSON response (HTTP #{response.code})"
  exit 1
end

# Every Way2API response is {"success" => ..., "message" => ..., "data" => ...}.
# Check both the HTTP status and the success flag.
unless response.is_a?(Net::HTTPSuccess) && body['success']
  warn "Request failed (HTTP #{response.code}): #{body['message']}"
  order_id = body.dig('data', 'order_id')
  warn "Order ID (quote this in support requests): #{order_id}" if order_id
  exit 1
end

puts JSON.pretty_generate(body['data']['result'])
```

### Kotlin

```kotlin
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.system.exitProcess

const val ENDPOINT = "https://app.way2api.com/api/v1/ip/check_advance"

fun main() {
    val apiKey = System.getenv("WAY2API_KEY") ?: "YOUR_API_KEY"
    val payload = "{\"ip_address\":\"122.180.149.100\"}"

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
```

### Swift

```swift
import Foundation
#if canImport(FoundationNetworking)
import FoundationNetworking
#endif

let apiKey = ProcessInfo.processInfo.environment["WAY2API_KEY"] ?? "YOUR_API_KEY"
let endpoint = URL(string: "https://app.way2api.com/api/v1/ip/check_advance")!

let payload: [String: Any] = [
    "ip_address": "122.180.149.100"
]

var request = URLRequest(url: endpoint)
request.httpMethod = "POST"
request.timeoutInterval = 30
request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
request.setValue("application/json", forHTTPHeaderField: "Content-Type")
request.httpBody = try JSONSerialization.data(withJSONObject: payload)

let semaphore = DispatchSemaphore(value: 0)
var exitCode: Int32 = 0

URLSession.shared.dataTask(with: request) { data, response, error in
    defer { semaphore.signal() }

    if let error = error {
        FileHandle.standardError.write("Network error: \(error.localizedDescription)\n".data(using: .utf8)!)
        exitCode = 1
        return
    }

    let status = (response as? HTTPURLResponse)?.statusCode ?? 0
    guard let data = data,
          let body = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
        FileHandle.standardError.write("Non-JSON response (HTTP \(status))\n".data(using: .utf8)!)
        exitCode = 1
        return
    }

    // Every Way2API response is ["success": ..., "message": ..., "data": ...].
    // Check both the HTTP status and the success flag.
    let success = body["success"] as? Bool ?? false
    if status >= 400 || !success {
        let message = body["message"] as? String ?? ""
        FileHandle.standardError.write("Request failed (HTTP \(status)): \(message)\n".data(using: .utf8)!)
        exitCode = 1
        return
    }

    if let container = body["data"] as? [String: Any],
       let result = container["result"],
       let pretty = try? JSONSerialization.data(withJSONObject: result, options: [.prettyPrinted]) {
        print(String(data: pretty, encoding: .utf8) ?? "")
    }
}.resume()

semaphore.wait()
exit(exitCode)
```

## Example response

```json
{
  "success": true,
  "message": "",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "result": {
      "city": {
        "confidence": 30,
        "geoname_id": 1261481,
        "names": {
          "en": "New Delhi"
        }
      },
      "continent": {
        "code": "AS",
        "geoname_id": 6255147,
        "names": {
          "en": "Asia"
        }
      },
      "country": {
        "confidence": 99,
        "iso_code": "IN",
        "geoname_id": 1269750,
        "names": {
          "en": "India"
        }
      },
      "location": {
        "accuracy_radius": 20,
        "latitude": 28.6327,
        "longitude": 77.2198,
        "time_zone": "Asia/Kolkata"
      },
      "postal": {
        "confidence": 1,
        "code": "110002"
      },
      "registered_country": {
        "iso_code": "IN",
        "geoname_id": 1269750,
        "names": {
          "en": "India"
        }
      },
      "subdivisions": [
        {
          "confidence": 30,
          "iso_code": "DL",
          "geoname_id": 1273293,
          "names": {
            "en": "National Capital Territory of Delhi"
          }
        }
      ],
      "traits": {
        "static_ip_score": 5,
        "user_count": 2,
        "user_type": "residential",
        "autonomous_system_number": 24560,
        "autonomous_system_organization": "Bharti Airtel Ltd., Telemedia Services",
        "connection_type": "Cable/DSL",
        "domain": "airtel.in",
        "isp": "Airtel",
        "organization": "Airtel",
        "ip_address": "122.180.149.100",
        "network": "122.180.149.100/32"
      }
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "The address 203.0.113.1 is not in our database.",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "IP_ADDRESS_NOT_FOUND"
  }
}
```

Branch on the HTTP status and the `success` flag rather than on message text:

| Status | Meaning | Charged |
|---|---|---|
| `200` | Result returned | Yes |
| `202` | Accepted, or the provider did not respond in time — quote the `order_id` | Yes |
| `401` | Missing or invalid API key | No |
| `402` | Insufficient balance | No |
| `403` | No access to this API service | No |
| `422` | Input rejected, or the lookup ran and returned a negative result | Depends |
| `429` | Rate limit exceeded — honour the `Retry-After` header | No |
| `500` / `503` | Way2API or provider error | No |

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/ip-check-advance/errors](https://app.way2api.com/documentation/ip-check-advance/errors).

## Common use cases

- **Fraud scoring** — Feed ASN, connection type, user type and anonymizer flags into a risk model rather than geolocation alone.
- **Login risk assessment** — Flag a sign-in from a VPN, public proxy, residential proxy or Tor exit node for step-up authentication.
- **Bot and abuse mitigation** — Distinguish datacentre and hosting traffic from genuine residential users.
- **Account takeover defence** — Combine the static IP score with anonymizer flags to spot a session that does not match the account history.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/ip-check-advance/rate-limits](https://app.way2api.com/documentation/ip-check-advance/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/ip-check-advance)
- [Request reference](https://app.way2api.com/documentation/ip-check-advance/request)
- [Response reference](https://app.way2api.com/documentation/ip-check-advance/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `ip-check-advance`
