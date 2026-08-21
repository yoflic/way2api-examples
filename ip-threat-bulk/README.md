# Bulk IP Threat Intelligence

**Bulk IP Threat Intelligence API** — the same risk signals as **IP Threat Intelligence**, for up to **100 addresses in a single billable call**. Each entry carries its own **status** (**ok** or **error**) and **message**, so one malformed or reserved address never fails the whole batch. Entries with **status = error** return the security block at default values — always check **status** before acting on a score. Duplicate addresses are collapsed before the lookup. Built for batch screening of signup logs, order queues and access records.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/ip/threat/bulk` |
| **Category** | IP and Network Intelligence |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `ip_threat_bulk` |
| **Full documentation** | [https://app.way2api.com/documentation/ip-threat-bulk](https://app.way2api.com/documentation/ip-threat-bulk) |

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
| `ip_addresses` | array | Yes | Array of 1–100 IPv4/IPv6 addresses to screen, e.g. ["8.8.8.8", "1.1.1.1"]. Duplicates are removed. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/ip/threat/bulk" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "ip_addresses": [
      "8.8.8.8",
      "1.1.1.1",
      "not-an-ip"
    ]
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/ip/threat/bulk";

const payload = {
  "ip_addresses": [
    "8.8.8.8",
    "1.1.1.1",
    "not-an-ip"
  ]
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
ENDPOINT = "https://app.way2api.com/api/v1/ip/threat/bulk"

payload = {
    "ip_addresses": [
        "8.8.8.8",
        "1.1.1.1",
        "not-an-ip"
    ]
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
      "total_requested": 3,
      "total_returned": 3,
      "results": [
        {
          "ip_address": "8.8.8.8",
          "status": "ok",
          "message": "",
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
        },
        {
          "ip_address": "1.1.1.1",
          "status": "ok",
          "message": "",
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
            "cloud_provider": "Cloudflare, Inc."
          }
        },
        {
          "ip_address": "not-an-ip",
          "status": "error",
          "message": "Provided IP address 'not-an-ip' is not valid",
          "security": {
            "threat_score": 0,
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
            "is_cloud_provider": false,
            "cloud_provider": ""
          }
        }
      ]
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "Please provide data in required format in request body",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "Invalid request body Exception"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/ip-threat-bulk/errors](https://app.way2api.com/documentation/ip-threat-bulk/errors).

## Common use cases

- **Log and event triage** — Score up to 100 addresses from a server or WAF log in a single billable call.
- **Batch signup screening** — Screen a day of registration IPs at once instead of one request per user.
- **Partial failure handling** — Read the per-entry status so one malformed or reserved address never fails the whole batch.
- **Blocklist maintenance** — Re-score an existing blocklist periodically and retire entries that have gone quiet.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/ip-threat-bulk/rate-limits](https://app.way2api.com/documentation/ip-threat-bulk/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/ip-threat-bulk)
- [Request reference](https://app.way2api.com/documentation/ip-threat-bulk/request)
- [Response reference](https://app.way2api.com/documentation/ip-threat-bulk/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `ip-threat-bulk`
