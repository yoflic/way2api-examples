# IP WHOIS Lookup

**IP WHOIS Lookup API** — live registration data for an **IPv4 or IPv6** address, straight from the Regional Internet Registry that allocated it. Returns the **allocated network** (range, CIDR, name, handle, allocation status), the **holder organisation** with its postal address, and the registered **abuse** and **technical** contacts. **registry** names the RIR that answered — ARIN, RIPE, APNIC, LACNIC or AFRINIC. Use it for abuse reporting, network attribution and IP-block ownership checks. For risk scoring see IP Threat Intelligence; for location see IP Geolocation.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/ip/whois` |
| **Category** | IP and Network Intelligence |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `ip_whois` |
| **Full documentation** | [https://app.way2api.com/documentation/ip-whois](https://app.way2api.com/documentation/ip-whois) |

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
| `ip_address` | string | Yes | IPv4 or IPv6 address to look up, e.g. 8.8.8.8 or 2001:4860:4860::8888. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/ip/whois" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "ip_address": "8.8.8.8"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/ip/whois";

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
ENDPOINT = "https://app.way2api.com/api/v1/ip/whois"

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
      "registry": "ARIN",
      "network": {
        "range_start": "8.8.8.0",
        "range_end": "8.8.8.255",
        "cidr": [
          "8.8.8.0/24"
        ],
        "name": "GOGL",
        "handle": "NET-8-8-8-0-2",
        "allocation_status": "direct_allocation",
        "parent": "NET8 (NET-8-0-0-0-0)",
        "registered_on": "2023-12-28",
        "updated_on": "2023-12-28"
      },
      "organization": {
        "name": "Google LLC",
        "handle": "GOGL",
        "street": "Amphitheatre Parkway",
        "city": "Mountain View",
        "state": "CA",
        "postal_code": "94043",
        "country": "US",
        "registered_on": "2000-03-30",
        "updated_on": "2019-10-31"
      },
      "abuse_contact": {
        "name": "Abuse",
        "email": "network-abuse@google.com",
        "phone": "+1-650-253-0000"
      },
      "technical_contact": {
        "name": "Google LLC",
        "email": "arin-contact@google.com",
        "phone": "+1-650-253-0000"
      },
      "queried_at": "2026-08-04 13:32:26"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "Request-param 'ip' (not-an-ip) is not an IP address",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "Input parameter is not valid"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/ip-whois/errors](https://app.way2api.com/documentation/ip-whois/errors).

## Common use cases

- **Network ownership attribution** — Find the organisation an address is allocated to, straight from the RIR that issued it.
- **Abuse reporting** — Get the registered abuse and technical contacts you need to escalate against a network.
- **Allocation research** — Read the network range, CIDR, name, handle and allocation status behind an address.
- **Registry identification** — See which RIR answered, whether ARIN, RIPE, APNIC, LACNIC or AFRINIC.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/ip-whois/rate-limits](https://app.way2api.com/documentation/ip-whois/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/ip-whois)
- [Request reference](https://app.way2api.com/documentation/ip-whois/request)
- [Response reference](https://app.way2api.com/documentation/ip-whois/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `ip-whois`
