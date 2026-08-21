# Vehicle RC PDF

**Vehicle RC PDF API** — Turn a vehicle **registration number** into a printable **Registration Certificate (RC)** document. Send the registration number and receive a **pdf_url** pointing at a ready-to-print **smart-card / A4 PDF** of the RC, laid out as the physical certificate and carrying the owner and address, chassis and engine numbers, make and model, body type, colour, fuel and emission norms, seating and weight specifications, cubic capacity, financer and the registering RTO. The response is the **document link only** — this endpoint sells the certificate, not the fields. If you need the RC data as JSON to read programmatically, use the **Vehicle RC Verification** endpoint instead. **pdf_url** is an empty string in the rare case where a document could not be produced, so check it before following the link. Generated documents stay downloadable for a limited time — fetch and store the file promptly rather than holding the link. Built for vehicle onboarding, fleet and dealership records, insurance and loan files, and any workflow that needs a printable RC.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/rc/pdf` |
| **Category** | Verification |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `rc_pdf` |
| **Full documentation** | [https://app.way2api.com/documentation/vehicle-rc-pdf](https://app.way2api.com/documentation/vehicle-rc-pdf) |

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
| `rc_number` | string | Yes | Vehicle registration number, e.g. DL3CAB1234 or MH12AB1234. Case-insensitive, no spaces or hyphens. |
| `chassis_number` | string | No | Optional chassis number for additional verification. 5 to 25 characters. |
| `engine_number` | string | No | Optional engine number for additional verification. 3 to 25 characters. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/rc/pdf" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "rc_number": "DL3CAB1234"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/rc/pdf";

const payload = {
  "rc_number": "DL3CAB1234"
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
ENDPOINT = "https://app.way2api.com/api/v1/rc/pdf"

payload = {
    "rc_number": "DL3CAB1234"
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
      "pdf_url": "https://docs.example-renderer.com/upload/rc2_1786426531_70e7ff519ee00a12.pdf"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "No vehicle record was found for the registration number provided.",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "no_record"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/vehicle-rc-pdf/errors](https://app.way2api.com/documentation/vehicle-rc-pdf/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/vehicle-rc-pdf/rate-limits](https://app.way2api.com/documentation/vehicle-rc-pdf/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/vehicle-rc-pdf)
- [Request reference](https://app.way2api.com/documentation/vehicle-rc-pdf/request)
- [Response reference](https://app.way2api.com/documentation/vehicle-rc-pdf/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `vehicle-rc-pdf`
