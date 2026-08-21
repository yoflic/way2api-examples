# IP Check

Look up geolocation, ISP, and network information for any public IPv4 or IPv6 address.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/ip/check` |
| **Category** | IP and Network Intelligence |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `ip_check` |
| **Full documentation** | [https://app.way2api.com/documentation/ip-check-v1](https://app.way2api.com/documentation/ip-check-v1) |

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
| `ip_address` | string | No | A valid public IPv4 or IPv6 address. If omitted, returns info for the caller s IP. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/ip/check" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "ip_address": "195.250.21.125"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/ip/check";

const payload = {
  "ip_address": "195.250.21.125"
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

## Python

```python
import json
import os
import sys

import requests

API_KEY = os.environ.get("WAY2API_KEY", "YOUR_API_KEY")
ENDPOINT = "https://app.way2api.com/api/v1/ip/check"

payload = {
    "ip_address": "195.250.21.125"
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

## Example response

```json
{
  "success": true,
  "message": "",
  "data": {
    "order_id": "W2A_SAMPLE_ORDER",
    "result": {
      "query": "195.250.21.125",
      "status": "success",
      "country": "India",
      "countryCode": "IN",
      "region": "MH",
      "regionName": "Maharashtra",
      "city": "Navi Mumbai",
      "zip": "400703",
      "lat": 19.0336,
      "lon": 73.0118,
      "timezone": "Asia/Kolkata",
      "isp": "WHG Hosting Services Ltd",
      "org": "WHG Hosting Services Ltd",
      "as": "AS199404 WHG Hosting Services Ltd"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "ip_address must be a valid IPv4 or IPv6 address.",
  "data": {
    "error_code": "VALIDATION_ERROR"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/ip-check-v1/errors](https://app.way2api.com/documentation/ip-check-v1/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/ip-check-v1/rate-limits](https://app.way2api.com/documentation/ip-check-v1/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/ip-check-v1)
- [Request reference](https://app.way2api.com/documentation/ip-check-v1/request)
- [Response reference](https://app.way2api.com/documentation/ip-check-v1/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `ip-check-v1`
