# Vehicle RC Text and PDF API

**Vehicle RC Text and PDF API** — The **Registration Certificate (RC)** for a vehicle returned **both ways in a single call**: the full RC record as JSON, and a **pdf_url** pointing at a ready-to-print **smart-card / A4 PDF** of the same certificate. Send a vehicle **registration number** and receive the owner and address, chassis and engine numbers, make, model, body type, colour, fuel and emission norms, seating and weight specifications, cubic capacity, registering RTO, fitness and road-tax validity, hypothecation and financer, insurance and PUCC validity, permit details, and blacklist / NOC status — plus the document link. Dates come back as **ISO YYYY-MM-DD** and **rc_status** is published from a stable value set, so both the shape and the values of this response are guaranteed not to move. **pdf_url** is an empty string in the rare case where a document could not be produced; the record itself is unaffected, so check it before following the link. Generated documents stay downloadable for a limited time — fetch and store the file promptly rather than holding the link. Use this endpoint when you need to both read the RC data and hand someone a printable certificate; the **Vehicle RC Verification** and **Vehicle RC PDF** endpoints sell those halves separately. Built for vehicle onboarding, fleet and dealership records, insurance and loan files, and RTO-facing paperwork.

Copy-paste integration examples in cURL, Python, Node.js, PHP, Java, C#, Go, Ruby, Kotlin and Swift.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/rc/text-pdf` |
| **Category** | Verification |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `rc_text_pdf` |
| **Full documentation** | [https://app.way2api.com/documentation/vehicle-rc-text-pdf](https://app.way2api.com/documentation/vehicle-rc-text-pdf) |

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
| `rc_number` | string | Yes | Vehicle registration number, e.g. DL3CAB1234 or MH12AB1234. Case-insensitive, no spaces or hyphens. |
| `chassis_number` | string | No | Optional chassis number for additional verification. 5 to 25 characters. |
| `engine_number` | string | No | Optional engine number for additional verification. 3 to 25 characters. |

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
curl -X POST "https://app.way2api.com/api/v1/rc/text-pdf" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "rc_number": "DL3CAB1234"
  }'
```

### Python

```python
import json
import os
import sys

import requests

API_KEY = os.environ.get("WAY2API_KEY", "YOUR_API_KEY")
ENDPOINT = "https://app.way2api.com/api/v1/rc/text-pdf"

payload = {
    "rc_number": "DL3CAB1234"
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
const ENDPOINT = "https://app.way2api.com/api/v1/rc/text-pdf";

const payload = {
  "rc_number": "DL3CAB1234"
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
$endpoint = 'https://app.way2api.com/api/v1/rc/text-pdf';

$payload = [
    'rc_number' => 'DL3CAB1234'
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

    static final String ENDPOINT = "https://app.way2api.com/api/v1/rc/text-pdf";

    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("WAY2API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "YOUR_API_KEY";
        }

        String payload = "{\"rc_number\":\"DL3CAB1234\"}";

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
    const string Endpoint = "https://app.way2api.com/api/v1/rc/text-pdf";

    static async Task<int> Main()
    {
        var apiKey = Environment.GetEnvironmentVariable("WAY2API_KEY") ?? "YOUR_API_KEY";
        var payload = "{\"rc_number\":\"DL3CAB1234\"}";

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

const endpoint = "https://app.way2api.com/api/v1/rc/text-pdf"

func main() {
	apiKey := os.Getenv("WAY2API_KEY")
	if apiKey == "" {
		apiKey = "YOUR_API_KEY"
	}

	payload := map[string]interface{}{
		"rc_number": "DL3CAB1234",
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
ENDPOINT = 'https://app.way2api.com/api/v1/rc/text-pdf'.freeze

payload = {
  'rc_number' => 'DL3CAB1234'
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

const val ENDPOINT = "https://app.way2api.com/api/v1/rc/text-pdf"

fun main() {
    val apiKey = System.getenv("WAY2API_KEY") ?: "YOUR_API_KEY"
    val payload = "{\"rc_number\":\"DL3CAB1234\"}"

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
let endpoint = URL(string: "https://app.way2api.com/api/v1/rc/text-pdf")!

let payload: [String: Any] = [
    "rc_number": "DL3CAB1234"
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
      "rc_number": "OD05AB1234",
      "registration_date": "2024-03-20",
      "rc_status": "ACTIVE",
      "less_info": false,
      "latest_by": "2026-08-11",
      "owner_name": "SNEHA MOHANTY",
      "father_name": "",
      "owner_number": "1",
      "masked_name": false,
      "mobile_number": "",
      "present_address": "Jagatsinghapur, 754119",
      "permanent_address": "Jagatsinghapur, 754119",
      "vehicle_category": "2WN",
      "vehicle_category_description": "M-Cycle/Scooter(2WN)",
      "vehicle_chasi_number": "ME1AB1234C5678901",
      "vehicle_engine_number": "G3AB1C234567",
      "maker_description": "INDIA YAMAHA MOTOR PVT LTD",
      "maker_model": "YAMAHA FZS VERSION 4.0",
      "variant": null,
      "body_type": "SOLO WITH PILLION",
      "fuel_type": "PETROL(E20)",
      "color": "DEEP PURPLISH BLUE",
      "norms_type": "BHARAT STAGE VI",
      "manufacturing_date": "1/2024",
      "manufacturing_date_formatted": "2024-01",
      "cubic_capacity": "149.00",
      "no_cylinders": "1",
      "seat_capacity": "2",
      "sleeper_capacity": "0",
      "standing_capacity": "0",
      "wheelbase": "1330",
      "unladen_weight": "135",
      "vehicle_gross_weight": "285",
      "registered_at": "CUTTACK RTO, Odisha",
      "rto_code": "",
      "fit_up_to": "2039-03-19",
      "tax_upto": "2039-03-19",
      "tax_paid_upto": "2039-03-19",
      "financed": true,
      "financer": "EXAMPLE CAPITAL LTD",
      "insurance_company": "Example General Insurance Co. Ltd.",
      "insurance_policy_number": "3410/12345678/000/00",
      "insurance_upto": "2029-03-18",
      "pucc_number": "OR12345678901234",
      "pucc_upto": "2026-11-02",
      "permit_number": "",
      "permit_type": "",
      "permit_issue_date": null,
      "permit_valid_from": null,
      "permit_valid_upto": null,
      "national_permit_number": "",
      "national_permit_upto": null,
      "national_permit_issued_by": null,
      "non_use_status": null,
      "non_use_from": null,
      "non_use_to": null,
      "blacklist_status": "",
      "noc_details": "",
      "challan_details": null,
      "response_metadata": {
        "masked_chassis": false,
        "masked_engine": false,
        "masked_owner_name": false
      },
      "pdf_url": "https://docs.example-renderer.com/upload/rc2_1786426531_70e7ff519ee00a12.pdf"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "No vehicle record was found for the registration number provided.",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "no_record"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/vehicle-rc-text-pdf/errors](https://app.way2api.com/documentation/vehicle-rc-text-pdf/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/vehicle-rc-text-pdf/rate-limits](https://app.way2api.com/documentation/vehicle-rc-text-pdf/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/vehicle-rc-text-pdf)
- [Request reference](https://app.way2api.com/documentation/vehicle-rc-text-pdf/request)
- [Response reference](https://app.way2api.com/documentation/vehicle-rc-text-pdf/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `vehicle-rc-text-pdf`
