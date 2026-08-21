# DNS Lookup

**DNS Lookup API** — live DNS records for a hostname, resolved at request time rather than served from a cache. Supports **A**, **AAAA**, **MX**, **NS**, **SOA**, **TXT**, **SPF** and **CNAME**; omit **record_type** to fetch every type in one call. Every record is returned in the **same flat shape** — name, type, ttl, value, priority, raw — so you never branch on record type to read the answer. **priority** is populated for MX records and 0 elsewhere; **raw** carries the original zone-file line.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/domain/dns` |
| **Category** | Domain Intelligence and Analysis |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `dns_lookup` |
| **Full documentation** | [https://app.way2api.com/documentation/dns-lookup](https://app.way2api.com/documentation/dns-lookup) |

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
| `domain_name` | string | Yes | Hostname to resolve, e.g. example.com. A scheme, path or www. prefix is stripped automatically. |
| `record_type` | string | No | Comma-separated record types: A, AAAA, MX, NS, SOA, SPF, TXT, CNAME. Defaults to "all". |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/domain/dns" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "domain_name": "google.com",
    "record_type": "all"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/domain/dns";

const payload = {
  "domain_name": "google.com",
  "record_type": "all"
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
ENDPOINT = "https://app.way2api.com/api/v1/domain/dns"

payload = {
    "domain_name": "google.com",
    "record_type": "all"
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
      "domain_name": "google.com",
      "is_registered": true,
      "record_types": [
        "A",
        "NS",
        "SOA",
        "MX",
        "TXT",
        "AAAA",
        "SPF"
      ],
      "records": [
        {
          "name": "google.com",
          "type": "A",
          "ttl": 300,
          "value": "142.250.102.138",
          "priority": 0,
          "raw": "google.com.\t\t300\tIN\tA\t142.250.102.138"
        },
        {
          "name": "google.com",
          "type": "A",
          "ttl": 300,
          "value": "142.250.102.139",
          "priority": 0,
          "raw": "google.com.\t\t300\tIN\tA\t142.250.102.139"
        },
        {
          "name": "google.com",
          "type": "A",
          "ttl": 300,
          "value": "142.250.102.101",
          "priority": 0,
          "raw": "google.com.\t\t300\tIN\tA\t142.250.102.101"
        },
        {
          "name": "google.com",
          "type": "A",
          "ttl": 300,
          "value": "142.250.102.102",
          "priority": 0,
          "raw": "google.com.\t\t300\tIN\tA\t142.250.102.102"
        }
      ],
      "queried_at": "2026-08-04 13:32:43"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "please pass correct parameters",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "Invalid Param Exception"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/dns-lookup/errors](https://app.way2api.com/documentation/dns-lookup/errors).

## Common use cases

- **Migration verification** — Confirm A, AAAA, CNAME and NS records resolved as expected after a cutover, at request time rather than from a cache.
- **Mail deliverability checks** — Read MX, TXT and SPF records to diagnose why mail to a domain is failing.
- **Monitoring and alerting** — Watch a record for an unexpected change that signals a hijack or a misconfiguration.
- **One call for every type** — Omit record_type to fetch all record types in a single billable request, in one flat shape.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/dns-lookup/rate-limits](https://app.way2api.com/documentation/dns-lookup/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/dns-lookup)
- [Request reference](https://app.way2api.com/documentation/dns-lookup/request)
- [Response reference](https://app.way2api.com/documentation/dns-lookup/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `dns-lookup`
