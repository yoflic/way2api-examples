# Credit Report Experian API

**Credit Report Experian API** — pull a full **Experian consumer credit report** for an individual from their **PAN and mobile number**, with the individual explicit **consent**. Returns the **bureau score**, the applicant record as the bureau holds it, an **account summary** (total, active, closed and defaulted accounts, plus secured versus unsecured outstanding), every **trade line** with its limit, balance, overdue amount and **month-by-month repayment history**, and the full **enquiry (CAPS) history**. Dates are returned as **ISO YYYY-MM-DD**, amounts as numbers, and every classification field — account type, account status, ownership, asset classification, enquiry purpose — as a stable published value such as **credit_card** or **overdue_90** rather than a raw bureau code, so both the shape and the values of this response are guaranteed not to move. Built for lending decisions, underwriting, portfolio monitoring and risk assessment.

Copy-paste integration examples in cURL, Python, Node.js, PHP, Java, C#, Go, Ruby, Kotlin and Swift.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/credit-report-experian/fetch-report` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `credit_report_experian` |
| **Full documentation** | [https://app.way2api.com/documentation/credit-report-experian](https://app.way2api.com/documentation/credit-report-experian) |

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
| `name` | string | Yes | Full name of the individual as it appears on their PAN. 2 to 100 characters. |
| `mobile` | string | Yes | 10-digit Indian mobile number of the individual, starting 6-9. |
| `pan` | string | Yes | 10-character PAN of the individual, e.g. ABCDE1234F. Case-insensitive. |
| `consent` | string | Yes | Must be "Y". A credit bureau enquiry is only lawful with the individual explicit consent, and you must have obtained and retained it. |

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
curl -X POST "https://app.way2api.com/api/v1/credit-report-experian/fetch-report" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ananya Sharma",
    "mobile": "9876543210",
    "pan": "ABCDE1234F",
    "consent": "Y"
  }'
```

### Python

```python
import json
import os
import sys

import requests

API_KEY = os.environ.get("WAY2API_KEY", "YOUR_API_KEY")
ENDPOINT = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report"

payload = {
    "name": "Ananya Sharma",
    "mobile": "9876543210",
    "pan": "ABCDE1234F",
    "consent": "Y"
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
const ENDPOINT = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report";

const payload = {
  "name": "Ananya Sharma",
  "mobile": "9876543210",
  "pan": "ABCDE1234F",
  "consent": "Y"
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
$endpoint = 'https://app.way2api.com/api/v1/credit-report-experian/fetch-report';

$payload = [
    'name' => 'Ananya Sharma',
    'mobile' => '9876543210',
    'pan' => 'ABCDE1234F',
    'consent' => 'Y'
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

    static final String ENDPOINT = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report";

    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("WAY2API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "YOUR_API_KEY";
        }

        String payload = "{\"name\":\"Ananya Sharma\",\"mobile\":\"9876543210\",\"pan\":\"ABCDE1234F\",\"consent\":\"Y\"}";

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
    const string Endpoint = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report";

    static async Task<int> Main()
    {
        var apiKey = Environment.GetEnvironmentVariable("WAY2API_KEY") ?? "YOUR_API_KEY";
        var payload = "{\"name\":\"Ananya Sharma\",\"mobile\":\"9876543210\",\"pan\":\"ABCDE1234F\",\"consent\":\"Y\"}";

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

const endpoint = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report"

func main() {
	apiKey := os.Getenv("WAY2API_KEY")
	if apiKey == "" {
		apiKey = "YOUR_API_KEY"
	}

	payload := map[string]interface{}{
		"name": "Ananya Sharma",
		"mobile": "9876543210",
		"pan": "ABCDE1234F",
		"consent": "Y",
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
ENDPOINT = 'https://app.way2api.com/api/v1/credit-report-experian/fetch-report'.freeze

payload = {
  'name' => 'Ananya Sharma',
  'mobile' => '9876543210',
  'pan' => 'ABCDE1234F',
  'consent' => 'Y'
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

const val ENDPOINT = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report"

fun main() {
    val apiKey = System.getenv("WAY2API_KEY") ?: "YOUR_API_KEY"
    val payload = "{\"name\":\"Ananya Sharma\",\"mobile\":\"9876543210\",\"pan\":\"ABCDE1234F\",\"consent\":\"Y\"}"

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
let endpoint = URL(string: "https://app.way2api.com/api/v1/credit-report-experian/fetch-report")!

let payload: [String: Any] = [
    "name": "Ananya Sharma",
    "mobile": "9876543210",
    "pan": "ABCDE1234F",
    "consent": "Y"
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
      "name": "ANANYA SHARMA",
      "mobile": "9876543210",
      "pan": "ABCDE1234F",
      "credit_score": 742,
      "report": {
        "number": "1786259702984",
        "date": "2026-08-09",
        "identity_match": "exact"
      },
      "applicant": {
        "first_name": "ANANYA",
        "middle_name": "",
        "last_name": "SHARMA",
        "gender": "female",
        "date_of_birth": "1990-04-17",
        "pan": "ABCDE1234F",
        "passport_number": "",
        "voter_id": "",
        "driving_licence": "",
        "mobile": "9876543210",
        "email": "ANANYA.SHARMA@EXAMPLE.COM",
        "address": {
          "line_1": "FLAT 12 SECOND FLOOR",
          "line_2": "GREEN VALLEY APARTMENTS",
          "line_3": "SECTOR 21 MAIN ROAD",
          "city": "NEW DELHI",
          "state_code": "07",
          "pincode": "110001",
          "country_code": "IB"
        }
      },
      "summary": {
        "total_accounts": 6,
        "active_accounts": 3,
        "closed_accounts": 3,
        "default_accounts": 0,
        "suit_filed_balance": 0,
        "outstanding": {
          "secured": 435757,
          "unsecured": 170579,
          "total": 606336,
          "secured_percent": 72,
          "unsecured_percent": 28
        }
      },
      "enquiry_summary": {
        "credit": {
          "last_7_days": 0,
          "last_30_days": 1,
          "last_90_days": 2,
          "last_180_days": 3
        },
        "non_credit": {
          "last_7_days": 0,
          "last_30_days": 0,
          "last_90_days": 0,
          "last_180_days": 0
        },
        "total": {
          "last_7_days": 0,
          "last_30_days": 1,
          "last_90_days": 2,
          "last_180_days": 3
        }
      },
      "accounts": [
        {
          "lender": "EXAMPLE BANK LTD",
          "lender_code": "PVT3000001",
          "account_number": "XXXXXXXXXXXXXXX0784",
          "account_type": "credit_card",
          "portfolio_type": "revolving",
          "ownership": "individual",
          "account_status": "active",
          "suit_filed_status": "no_suit_filed",
          "currency": "INR",
          "opened_on": "2025-12-09",
          "closed_on": "",
          "reported_on": "2026-06-30",
          "last_payment_on": "2026-06-07",
          "first_delinquency_on": "",
          "credit_limit": 135000,
          "sanctioned_amount": 39624,
          "current_balance": 21791,
          "amount_overdue": 0,
          "written_off_total": 0,
          "written_off_principal": 0,
          "settlement_amount": 0,
          "interest_rate": 0,
          "tenure_months": 0,
          "payment_history": "000000??????????????????????????????",
          "monthly_history": [
            {
              "year": 2026,
              "month": 6,
              "days_past_due": 0,
              "asset_classification": ""
            },
            {
              "year": 2026,
              "month": 5,
              "days_past_due": 0,
              "asset_classification": ""
            }
          ],
          "holders": [
            {
              "first_name": "ANANYA",
              "middle_name": "",
              "surname": "SHARMA",
              "alias": "",
              "gender": "female",
              "date_of_birth": "1990-04-17",
              "pan": "ABCDE1234F",
              "passport_number": "",
              "voter_id": ""
            }
          ],
          "addresses": [
            {
              "line_1": "FLAT 12 SECOND FLOOR",
              "line_2": "GREEN VALLEY APARTMENTS",
              "line_3": "SECTOR 21 MAIN ROAD",
              "city": "NEW DELHI",
              "state_code": "07",
              "pincode": "110001",
              "country_code": "IB"
            }
          ],
          "contacts": [
            {
              "landline": "",
              "mobile": "9876543210",
              "email": "ANANYA.SHARMA@EXAMPLE.COM"
            }
          ]
        }
      ],
      "enquiries": [
        {
          "enquired_on": "2026-05-14",
          "member": "EXAMPLE FINANCE LIMITED",
          "purpose": "personal_loan",
          "amount": 100000,
          "tenure_months": 12
        }
      ],
      "non_credit_enquiries": []
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "No credit records were found for the details provided.",
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/credit-report-experian/errors](https://app.way2api.com/documentation/credit-report-experian/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/credit-report-experian/rate-limits](https://app.way2api.com/documentation/credit-report-experian/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/credit-report-experian)
- [Request reference](https://app.way2api.com/documentation/credit-report-experian/request)
- [Response reference](https://app.way2api.com/documentation/credit-report-experian/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `credit-report-experian`
