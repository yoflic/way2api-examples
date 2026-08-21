# Domain Whois

**Domain WHOIS Lookup API** — Retrieve comprehensive WHOIS registration information for any domain name. Returns registrar details, registration/expiry dates, registrant contact, administrative and technical contacts, name servers, and domain status.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/domain/whois_live` |
| **Category** | Domain Intelligence and Analysis |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `domain_whois` |
| **Full documentation** | [https://app.way2api.com/documentation/domain-whois-lookup-v1](https://app.way2api.com/documentation/domain-whois-lookup-v1) |

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
| `domain_name` | string | Yes | Fully qualified domain name to look up (e.g. example.com, example.co.in). |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/domain/whois_live" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "domain_name": "kwikapi.com"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/domain/whois_live";

const payload = {
  "domain_name": "kwikapi.com"
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
ENDPOINT = "https://app.way2api.com/api/v1/domain/whois_live"

payload = {
    "domain_name": "kwikapi.com"
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
  "message": "Verification successful.",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "verified": true,
    "result": {
      "domain_name": "kwikapi.com",
      "domain_registered": "yes",
      "create_date": "2019-03-19",
      "update_date": "2024-02-18",
      "expiry_date": "2025-03-19",
      "domain_registrar": {
        "iana_id": "1068",
        "registrar_name": "NAMECHEAP INC",
        "whois_server": "whois.namecheap.com",
        "website_url": "http://www.namecheap.com",
        "email_address": "abuse@namecheap.com",
        "phone_number": "+1.9854014545"
      },
      "registrant_contact": {
        "name": "Redacted for Privacy",
        "company": "Privacy service provided by Withheld for Privacy ehf",
        "street": "Kalkofnsvegur 2",
        "city": "Reykjavik",
        "state": "Capital Region",
        "zip_code": "101",
        "country_name": "Iceland",
        "country_code": "IS",
        "email_address": "redacted@withheldforprivacy.com",
        "phone": "+354.4212434",
        "fax": "N/A",
        "mailing_address": "Kalkofnsvegur 2, 101 Reykjavik, Capital Region, Iceland"
      },
      "administrative_contact": {
        "name": "Redacted for Privacy",
        "company": "Privacy service provided by Withheld for Privacy ehf",
        "street": "Kalkofnsvegur 2",
        "city": "Reykjavik",
        "state": "Capital Region",
        "zip_code": "101",
        "country_name": "Iceland",
        "country_code": "IS",
        "email_address": "redacted@withheldforprivacy.com",
        "phone": "+354.4212434",
        "fax": "N/A",
        "mailing_address": "Kalkofnsvegur 2, 101 Reykjavik, Capital Region, Iceland"
      },
      "technical_contact": {
        "name": "Redacted for Privacy",
        "company": "Privacy service provided by Withheld for Privacy ehf",
        "street": "Kalkofnsvegur 2",
        "city": "Reykjavik",
        "state": "Capital Region",
        "zip_code": "101",
        "country_name": "Iceland",
        "country_code": "IS",
        "email_address": "redacted@withheldforprivacy.com",
        "phone": "+354.4212434",
        "fax": "N/A",
        "mailing_address": "Kalkofnsvegur 2, 101 Reykjavik, Capital Region, Iceland"
      },
      "name_servers": [
        "dns3.cloudns.net",
        "dns4.cloudns.net",
        "dns7.cloudns.net",
        "dns8.cloudns.net"
      ],
      "domain_status": [
        "clientTransferProhibited"
      ]
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "domain_name must be a valid domain name (e.g. example.com).",
  "data": null
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/domain-whois-lookup-v1/errors](https://app.way2api.com/documentation/domain-whois-lookup-v1/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/domain-whois-lookup-v1/rate-limits](https://app.way2api.com/documentation/domain-whois-lookup-v1/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/domain-whois-lookup-v1)
- [Request reference](https://app.way2api.com/documentation/domain-whois-lookup-v1/request)
- [Response reference](https://app.way2api.com/documentation/domain-whois-lookup-v1/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `domain-whois-lookup-v1`
