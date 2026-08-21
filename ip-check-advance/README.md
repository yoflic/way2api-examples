# IP Check Advance

**IP Check Advance API** — Deep IP intelligence for any **IPv4 or IPv6** address. Beyond basic geolocation (city, country, coordinates, time zone), it returns rich **network** and **risk** signals: **ISP**, **organization**, **ASN**, **connection type**, **user type**, a **static IP score**, and anonymizer flags — **is_anonymous**, **is_anonymous_vpn**, **is_public_proxy**, **is_residential_proxy** and **is_tor_exit_node**. Built for fraud scoring, login-risk checks, and geo-targeting. **ip_address is optional** — omit it to auto-detect and look up the IP that called this API. Fields the source cannot resolve for a given address are omitted from the result.

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

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/ip/check_advance" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "ip_address": "122.180.149.100"
  }'
```

## Node.js

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

## Python

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
