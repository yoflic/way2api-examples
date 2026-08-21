# Subdomains Lookup

**Subdomains Lookup API** — discovers subdomains observed for a domain, each with **first_seen** and **last_seen** dates so you can tell live infrastructure from historical records. Results are **paginated**: read **total_pages** and **total_records** from the first response, then pass **page** to walk the rest. Pass **status** to return only active or only inactive hosts. Built for attack-surface mapping, asset inventory and shadow-IT discovery. **Each page is one billable call.**

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/domain/subdomains` |
| **Category** | Domain Intelligence and Analysis |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `subdomain_lookup` |
| **Full documentation** | [https://app.way2api.com/documentation/subdomain-lookup](https://app.way2api.com/documentation/subdomain-lookup) |

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
| `domain_name` | string | Yes | Root domain to enumerate, e.g. example.com. A scheme, path or www. prefix is stripped automatically. |
| `page` | integer | No | Page number for paginated results (positive integer). Defaults to 1. |
| `status` | string | No | Filter results by host status: "active" or "inactive". Defaults to all. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/domain/subdomains" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "domain_name": "way2api.com"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/domain/subdomains";

const payload = {
  "domain_name": "way2api.com"
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
ENDPOINT = "https://app.way2api.com/api/v1/domain/subdomains"

payload = {
    "domain_name": "way2api.com"
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
      "domain_name": "way2api.com",
      "total_records": 3,
      "current_page": 1,
      "total_pages": 1,
      "subdomains": [
        {
          "name": "app.way2api.com",
          "first_seen": "2026-04-30",
          "last_seen": "2026-05-03"
        },
        {
          "name": "www.way2api.com",
          "first_seen": "2020-09-21",
          "last_seen": "2026-04-09"
        },
        {
          "name": "stage-y4du5xz.way2api.com",
          "first_seen": "2026-04-30",
          "last_seen": "2026-06-30"
        }
      ],
      "queried_at": "2026-08-04T13:32:44.526753615"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/subdomain-lookup/errors](https://app.way2api.com/documentation/subdomain-lookup/errors).

## Common use cases

- **Attack surface mapping** — Discover subdomains exposed for a domain, with first_seen and last_seen dates to separate live from historical.
- **Shadow IT discovery** — Find hosts standing up under your domain that never went through IT.
- **Asset inventory** — Build and refresh a list of everything published under a domain you own.
- **Acquisition due diligence** — Survey the infrastructure footprint of a company before you buy it.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/subdomain-lookup/rate-limits](https://app.way2api.com/documentation/subdomain-lookup/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/subdomain-lookup)
- [Request reference](https://app.way2api.com/documentation/subdomain-lookup/request)
- [Response reference](https://app.way2api.com/documentation/subdomain-lookup/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `subdomain-lookup`
