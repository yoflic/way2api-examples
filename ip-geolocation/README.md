# IP Geolocation Lookup

**IP Geolocation API** — locate an **IPv4 or IPv6** address and describe the network behind it. Returns **continent, country, state, district, city, postal code and coordinates**, plus country metadata (**calling code**, **TLD**, **languages**, **flag emoji**), the local **currency**, the full **time zone** with current offset and DST state, and the network's **ASN** and owning **company**. Pass **lang** to localise place names. **ip_address is optional** — omit it to look up the IP that called this API. For threat flags, reverse hostname and abuse contacts in the same call, use IP Geolocation Enrichment.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/ip/geolocation` |
| **Category** | IP and Network Intelligence |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `ip_geolocation` |
| **Full documentation** | [https://app.way2api.com/documentation/ip-geolocation](https://app.way2api.com/documentation/ip-geolocation) |

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
| `ip_address` | string | No | IPv4 or IPv6 address to locate, e.g. 8.8.8.8. Optional — if omitted, the IP that called this API is used. |
| `lang` | string | No | Language for location names: en (default), de, ru, ja, fr, cn, es, cs, it, ko, fa, pt. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/ip/geolocation" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "ip_address": "8.8.8.8"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/ip/geolocation";

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
ENDPOINT = "https://app.way2api.com/api/v1/ip/geolocation"

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
      "location": {
        "continent_code": "NA",
        "continent_name": "North America",
        "country_code": "US",
        "country_code3": "USA",
        "country_name": "United States",
        "country_name_official": "United States of America",
        "country_capital": "Washington, D.C.",
        "state": "California",
        "state_code": "US-CA",
        "district": "Santa Clara",
        "city": "Mountain View",
        "postal_code": "94043-1351",
        "latitude": 37.4224,
        "longitude": -122.08421,
        "is_eu": false,
        "geoname_id": "6301403"
      },
      "country_metadata": {
        "calling_code": "+1",
        "tld": ".us",
        "languages": [
          "en-US",
          "es-US",
          "haw",
          "fr"
        ],
        "flag_emoji": "🇺🇸"
      },
      "network": {
        "route": "8.8.8.0/24",
        "connection_type": "",
        "is_anycast": true
      },
      "currency": {
        "code": "USD",
        "name": "US Dollar",
        "symbol": "$"
      },
      "asn": {
        "number": "AS15169",
        "organization": "Google LLC",
        "country": "US",
        "type": "business",
        "domain": "google.com",
        "registry": "ARIN",
        "allocated_on": "2000-03-30"
      },
      "company": {
        "name": "Google LLC",
        "type": "hosting",
        "domain": "google.com"
      },
      "time_zone": {
        "name": "America/Los_Angeles",
        "offset": -8,
        "offset_with_dst": -7,
        "current_time": "2026-08-04 06:33:45.664-0700",
        "abbreviation": "PDT",
        "is_dst": true
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
  "message": "'255.255.255.255' is a reserved (bogon) IP address.",
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/ip-geolocation/errors](https://app.way2api.com/documentation/ip-geolocation/errors).

## Common use cases

- **Content localisation** — Localise currency, language and place names using continent through city plus the local currency and time zone.
- **Time zone aware scheduling** — Read the full time zone with current offset and DST state to schedule notifications sensibly.
- **Regional compliance** — Apply country-specific rules using country metadata such as calling code, TLD and languages.
- **Network attribution** — Identify the ASN and owning company behind a request.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/ip-geolocation/rate-limits](https://app.way2api.com/documentation/ip-geolocation/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/ip-geolocation)
- [Request reference](https://app.way2api.com/documentation/ip-geolocation/request)
- [Response reference](https://app.way2api.com/documentation/ip-geolocation/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `ip-geolocation`
