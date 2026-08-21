# GST Verification

**GST Verification API** — Verify a 15-character **GSTIN (Goods & Services Tax Identification Number)** registered under India's GST system. Critical for B2B vendor verification, invoice validation, and supply chain compliance. Returns the **business legal name, trade name, registration status** (Active, Cancelled, Suspended), **taxpayer type**, **constitution** (Private Limited, LLP, etc.), **registration date**, registered **address with state and pincode**, **PAN number**, **nature of business activities**, and **e-invoice status**. Use this endpoint to validate GST invoices, verify suppliers before onboarding, and ensure regulatory compliance under GST law.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/gst/verify` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `gst_verify` |
| **Full documentation** | [https://app.way2api.com/documentation/gst](https://app.way2api.com/documentation/gst) |

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
| `gst_number` | string | Yes | 15-character GSTIN — format: [0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][0-9A-Z][Z][0-9A-Z] (e.g. 10ABCDE1234F1Z5) |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/gst/verify" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "gst_number": "10ABCDE1234F1Z5"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/gst/verify";

const payload = {
  "gst_number": "10ABCDE1234F1Z5"
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
ENDPOINT = "https://app.way2api.com/api/v1/gst/verify"

payload = {
    "gst_number": "10ABCDE1234F1Z5"
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
      "gstin": "10ABCDE1234F1Z5",
      "lgnm": "EXAMPLE TRADERS PRIVATE LIMITED",
      "tradeNam": "EXAMPLE TRADERS PRIVATE LIMITED",
      "sts": "Active",
      "dty": "Regular",
      "ctb": "Private Limited Company",
      "rgdt": "16/05/2019",
      "lstupdt": "13/07/2023",
      "stj": "Saran 1",
      "ctj": "CHHAPRA RANGE",
      "panNo": "ABCDE1234F",
      "einvoiceStatus": "Yes",
      "nba": [
        "Office / Sale Office",
        "Recipient of Goods or Services",
        "Supplier of Services",
        "Retail Business"
      ],
      "pradr": {
        "addr": {
          "bnm": "PLOT 12, GANDHI NAGAR",
          "st": "MAIN ROAD, BLOCK-JALALPUR",
          "loc": "JALALPUR",
          "dst": "Saran",
          "stcd": "Bihar",
          "pncd": "841412"
        },
        "ntr": "Office / Sale Office, Supplier of Services"
      }
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "Invalid GSTIN / UID",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "INVALID_GSTIN",
    "result": {
      "gst_number": "10ABCDE1234F2Z5A"
    }
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/gst/errors](https://app.way2api.com/documentation/gst/errors).

## Common use cases

- **B2B vendor verification** — Confirm a supplier GSTIN is active and belongs to the business you think it does, before you raise a purchase order.
- **Invoice and input credit validation** — Validate the GSTIN on an incoming invoice so your input tax credit claim is not rejected later.
- **Supply chain compliance** — Screen a vendor master for cancelled or suspended registrations and stop transacting with them.
- **Business onboarding** — Pull legal name, trade name, constitution and registration date to prefill a merchant application.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/gst/rate-limits](https://app.way2api.com/documentation/gst/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/gst)
- [Request reference](https://app.way2api.com/documentation/gst/request)
- [Response reference](https://app.way2api.com/documentation/gst/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `gst`
