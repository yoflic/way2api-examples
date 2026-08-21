# SSL Certificate Lookup

**SSL Certificate Lookup API** — fetches the live TLS certificate chain presented by a domain. Each certificate reports its **validity window**, **serial number**, **signature algorithm**, **subject** and **issuer**, public-key **algorithm and size**, key usages, and the full list of **SAN hostnames** the certificate covers. Use **valid_to** for expiry monitoring and **san_dns_names** to confirm a certificate actually covers the host you are serving. Raw PEM blocks are omitted — the parsed fields carry the same information in usable form.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/domain/ssl` |
| **Category** | Domain Intelligence and Analysis |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `ssl_lookup` |
| **Full documentation** | [https://app.way2api.com/documentation/ssl-lookup](https://app.way2api.com/documentation/ssl-lookup) |

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
| `domain_name` | string | Yes | Domain whose certificate should be fetched, e.g. example.com. A scheme, path or www. prefix is stripped automatically. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/domain/ssl" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "domain_name": "google.com"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/domain/ssl";

const payload = {
  "domain_name": "google.com"
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
ENDPOINT = "https://app.way2api.com/api/v1/domain/ssl"

payload = {
    "domain_name": "google.com"
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
      "certificates": [
        {
          "chain_order": "end_user",
          "authentication_type": "domain",
          "valid_from": "2026-06-29 08:37:25 UTC",
          "valid_to": "2026-09-21 08:37:24 UTC",
          "serial_number": "02:b0:c1:99:b5:f2:ff:96:09:5e:3c:86:1a:f4:2b:ee",
          "signature_algorithm": "SHA256-RSA",
          "subject": {
            "common_name": "*.google.com",
            "organization": ""
          },
          "issuer": {
            "common_name": "WR2",
            "organization": "Google Trust Services",
            "country": "US"
          },
          "public_key": {
            "algorithm": "ECDSA",
            "size": "256 bit"
          },
          "key_usages": [
            "digital_signature"
          ],
          "extended_key_usages": [
            "server_auth"
          ],
          "san_dns_names": [
            "*.google.com",
            "google.com",
            "*.youtube.com"
          ]
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
  "message": "Please pass domain param correct value",
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/ssl-lookup/errors](https://app.way2api.com/documentation/ssl-lookup/errors).

## Common use cases

- **Certificate expiry monitoring** — Track valid_to across your estate and alert before a certificate lapses and takes a site down.
- **Coverage verification** — Check san_dns_names to confirm a certificate actually covers the hostname you are serving.
- **Security posture auditing** — Review signature algorithm, public key algorithm and key size against your own minimum standard.
- **Vendor and supplier checks** — Inspect the live certificate chain a third party presents before integrating with them.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/ssl-lookup/rate-limits](https://app.way2api.com/documentation/ssl-lookup/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/ssl-lookup)
- [Request reference](https://app.way2api.com/documentation/ssl-lookup/request)
- [Response reference](https://app.way2api.com/documentation/ssl-lookup/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `ssl-lookup`
