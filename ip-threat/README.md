# IP Threat Intelligence

**IP Threat Intelligence API** — real-time risk signals for an **IPv4 or IPv6** address. Returns a **threat_score** from 0 to 100 (higher is riskier) alongside the individual flags behind it: **is_tor**, **is_proxy**, **is_residential_proxy**, **is_vpn**, **is_relay**, **is_anonymous**, **is_known_attacker**, **is_bot**, **is_spam** and **is_cloud_provider** — plus detected provider names and confidence scores for the proxy and VPN verdicts. **ip_address is optional**: omit it and the IP that called this API is looked up. Built for login-risk checks, signup fraud screening and payment review. Use the bulk endpoint to screen many addresses in one call.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/ip/threat` |
| **Category** | IP and Network Intelligence |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `ip_threat` |
| **Full documentation** | [https://app.way2api.com/documentation/ip-threat](https://app.way2api.com/documentation/ip-threat) |

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
| `ip_address` | string | No | IPv4 or IPv6 address to screen, e.g. 8.8.8.8. Optional — if omitted, the IP that called this API is used. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/ip/threat" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "ip_address": "8.8.8.8"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/ip/threat";

const payload = {
  "ip_address": "8.8.8.8"
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
ENDPOINT = "https://app.way2api.com/api/v1/ip/threat"

payload = {
    "ip_address": "8.8.8.8"
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
    "order_id": "W2A1739512345abcdef01",
    "result": {
      "ip_address": "8.8.8.8",
      "security": {
        "threat_score": 5,
        "is_anonymous": false,
        "is_tor": false,
        "is_proxy": false,
        "is_residential_proxy": false,
        "proxy_providers": [],
        "proxy_confidence": 0,
        "proxy_last_seen": "",
        "is_vpn": false,
        "vpn_providers": [],
        "vpn_confidence": 0,
        "vpn_last_seen": "",
        "is_relay": false,
        "relay_provider": "",
        "is_known_attacker": false,
        "is_bot": false,
        "is_spam": false,
        "is_cloud_provider": true,
        "cloud_provider": "Google LLC"
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
  "message": "'127.0.0.1' is a reserved (bogon) IP address.",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "Locked"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/ip-threat/errors](https://app.way2api.com/documentation/ip-threat/errors).

## Common use cases

- **Real-time risk scoring** — Score an IP from 0 to 100 at signup, login or checkout and act on the number.
- **Anonymised traffic detection** — Identify Tor, proxy, residential proxy, VPN and relay traffic, with provider names and confidence.
- **Bot and spam filtering** — Block known attackers, bots and spam sources before they reach your application.
- **Cloud traffic policy** — Separate cloud provider ranges from residential users and apply different rules to each.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/ip-threat/rate-limits](https://app.way2api.com/documentation/ip-threat/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/ip-threat)
- [Request reference](https://app.way2api.com/documentation/ip-threat/request)
- [Response reference](https://app.way2api.com/documentation/ip-threat/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `ip-threat`
