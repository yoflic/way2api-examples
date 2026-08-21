# Domain WHOIS Lookup

**Domain WHOIS Lookup API** — live registry WHOIS for any domain. Returns **registration dates** (created, updated, expires), the **registrar** with its IANA ID and abuse contact, the full **registrant contact** — organisation, name, email, phone and postal address — **name servers**, and the domain's **EPP status codes**. Registrant fields are returned exactly as the registry publishes them: privacy-protected or GDPR-redacted domains come back with the redacted fields empty rather than as an error, and unregistered names return **is_registered = false** with empty detail fields. Built for domain monitoring, expiry alerting, brand protection and due diligence. Raw WHOIS text is deliberately not returned — every field is parsed and normalised. It is a v2 API, an improvement on the v1 API.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/domain/whois` |
| **Category** | Domain Intelligence and Analysis |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `domain_whois_lookup` |
| **Full documentation** | [https://app.way2api.com/documentation/domain-whois-lookup-v2](https://app.way2api.com/documentation/domain-whois-lookup-v2) |

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
| `domain_name` | string | Yes | Domain name to query, e.g. example.com. A scheme, path or www. prefix is stripped automatically. Max 253 characters. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/domain/whois" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "domain_name": "way2api.com"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/domain/whois";

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
ENDPOINT = "https://app.way2api.com/api/v1/domain/whois"

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
      "is_registered": true,
      "created_on": "2024-03-03",
      "updated_on": "2026-02-21",
      "expires_on": "2031-03-03",
      "registrar": {
        "name": "GoDaddy.com, LLC",
        "iana_id": "146",
        "url": "http://www.godaddy.com",
        "abuse_email": "abuse@godaddy.com",
        "abuse_phone": "+14806242505"
      },
      "registrant": {
        "registry_id": "way2apicom-reg",
        "name": "Registration Private",
        "organization": "Domains By Proxy, LLC",
        "email": "",
        "phone": "+14806242599",
        "street": "DomainsByProxy.com 100 S. Mill Ave, Suite 1600",
        "city": "Tempe",
        "state": "Arizona",
        "zip_code": "85281",
        "country": "United States",
        "country_code": "US"
      },
      "technical_contact": {
        "email": ""
      },
      "name_servers": [
        "magnolia.ns.cloudflare.com",
        "charles.ns.cloudflare.com"
      ],
      "domain_status": [
        "client_update_prohibited",
        "client_delete_prohibited",
        "client_renew_prohibited",
        "client_transfer_prohibited"
      ],
      "queried_at": "2026-08-06 05:53:51"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "We are not providing the whois of this domain extension (local)",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "Not Supported Domain extension"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/domain-whois-lookup-v2/errors](https://app.way2api.com/documentation/domain-whois-lookup-v2/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/domain-whois-lookup-v2/rate-limits](https://app.way2api.com/documentation/domain-whois-lookup-v2/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/domain-whois-lookup-v2)
- [Request reference](https://app.way2api.com/documentation/domain-whois-lookup-v2/request)
- [Response reference](https://app.way2api.com/documentation/domain-whois-lookup-v2/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `domain-whois-lookup-v2`
