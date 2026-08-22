# DTH Advance Information API

**DTH Advance Information API** — Pull the full subscriber profile behind a **DTH customer ID / VC number**. Returns the account **name**, **registered_mobile**, current **balance**, **monthly_amount**, account **status**, **plan**, **next_recharge_date**, **last_recharge_date**, **last_recharge_amount**, **switch_off_date** and the installation **address** (with city, district, state and pin_code). Built for DTH recharge platforms and retailer apps that need to confirm the right account and its dues before taking a payment. Fields the operator does not publish for a given account are returned as empty strings (or "N/A" where the operator sends that).

Copy-paste integration examples in cURL, Python, Node.js, PHP, Java, C#, Go, Ruby, Kotlin and Swift.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/dth/info` |
| **Category** | Telecom |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `dth_info_advance` |
| **Full documentation** | [https://app.way2api.com/documentation/dth-info](https://app.way2api.com/documentation/dth-info) |

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
| `dth_number` | string | Yes | DTH subscriber / customer ID — 9 to 13 digits (e.g. 01234567890). The exact prefix and length are set by the operator and differ between them, so the operator rejects a well-formed ID that does not match its own scheme. |
| `operator` | string | Yes | DTH operator of the connection. Accepted values: airtel_dth, dish_tv, reliance_bigtv, sun_direct, tata_play (alias tata_sky), videocon_d2h (alias d2h). Use the DTH Operator Check API if the operator is unknown. |

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
curl -X POST "https://app.way2api.com/api/v1/dth/info" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "dth_number": "01234567890",
    "operator": "dish_tv"
  }'
```

### Python

```python
import json
import os
import sys

import requests

API_KEY = os.environ.get("WAY2API_KEY", "YOUR_API_KEY")
ENDPOINT = "https://app.way2api.com/api/v1/dth/info"

payload = {
    "dth_number": "01234567890",
    "operator": "dish_tv"
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
const ENDPOINT = "https://app.way2api.com/api/v1/dth/info";

const payload = {
  "dth_number": "01234567890",
  "operator": "dish_tv"
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
$endpoint = 'https://app.way2api.com/api/v1/dth/info';

$payload = [
    'dth_number' => '01234567890',
    'operator' => 'dish_tv'
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
    const string Endpoint = "https://app.way2api.com/api/v1/dth/info";

    static async Task<int> Main()
    {
        var apiKey = Environment.GetEnvironmentVariable("WAY2API_KEY") ?? "YOUR_API_KEY";
        var payload = "{\"dth_number\":\"01234567890\",\"operator\":\"dish_tv\"}";

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

const endpoint = "https://app.way2api.com/api/v1/dth/info"

func main() {
	apiKey := os.Getenv("WAY2API_KEY")
	if apiKey == "" {
		apiKey = "YOUR_API_KEY"
	}

	payload := map[string]interface{}{
		"dth_number": "01234567890",
		"operator": "dish_tv",
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
ENDPOINT = 'https://app.way2api.com/api/v1/dth/info'.freeze

payload = {
  'dth_number' => '01234567890',
  'operator' => 'dish_tv'
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

const val ENDPOINT = "https://app.way2api.com/api/v1/dth/info"

fun main() {
    val apiKey = System.getenv("WAY2API_KEY") ?: "YOUR_API_KEY"
    val payload = "{\"dth_number\":\"01234567890\",\"operator\":\"dish_tv\"}"

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
let endpoint = URL(string: "https://app.way2api.com/api/v1/dth/info")!

let payload: [String: Any] = [
    "dth_number": "01234567890",
    "operator": "dish_tv"
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
      "dth_number": "01234567890",
      "operator": "dish_tv",
      "customer_id": "9700000123",
      "name": "Lakshmi Priya",
      "registered_mobile": "98XXXXXX10",
      "balance": "194.73",
      "monthly_amount": "",
      "status": "1",
      "plan": "",
      "next_recharge_date": "8/4/2026 12:00:00 AM",
      "last_recharge_date": "N/A",
      "last_recharge_amount": "N/A",
      "switch_off_date": "8/7/2026 12:00:00 AM",
      "address": "12 MAIN ROAD, VILLIANUR, PONDICHERRY, Pin - 605110",
      "city": "PONDICHERRY",
      "district": "",
      "state": "PONDICHERRY",
      "pin_code": "605110"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "Customer Id Starts with 0 and is 11 digits long",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "1"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/dth-info/errors](https://app.way2api.com/documentation/dth-info/errors).

## Common use cases

- **Recharge amount guidance** — Read the current balance, monthly amount and next recharge date to suggest what the customer should actually pay.
- **Retailer applications** — Show account name, plan and status so a shop assistant can confirm the right connection before charging.
- **Dunning and reactivation** — Use the switch-off date and account status to target subscribers before or just after disconnection.
- **Address confirmation** — Use the installation address with city, district, state and pin code to confirm the connection you are servicing.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/dth-info/rate-limits](https://app.way2api.com/documentation/dth-info/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/dth-info)
- [Request reference](https://app.way2api.com/documentation/dth-info/request)
- [Response reference](https://app.way2api.com/documentation/dth-info/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `dth-info`
