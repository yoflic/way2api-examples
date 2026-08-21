# Aadhaar PAN Link Check

**Aadhaar–PAN Link Check API** — Check whether a given **Aadhaar number** is linked to a **PAN card** as mandated by the Income Tax Department. Returns the **masked PAN, linking status, and reason**. Essential for tax compliance, financial onboarding, and regulatory verification.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/aadhaar/aadhaar_pan_link_check` |
| **Category** | Verification |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `aadhaar_pan_link_check` |
| **Full documentation** | [https://app.way2api.com/documentation/aadhaar-pan-link-check](https://app.way2api.com/documentation/aadhaar-pan-link-check) |

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
| `aadhaar_number` | string | Yes | 12-digit Aadhaar number. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/aadhaar/aadhaar_pan_link_check" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "aadhaar_number": "123456789012"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/aadhaar/aadhaar_pan_link_check";

const payload = {
  "aadhaar_number": "123456789012"
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
ENDPOINT = "https://app.way2api.com/api/v1/aadhaar/aadhaar_pan_link_check"

payload = {
    "aadhaar_number": "123456789012"
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
      "masked_pan": "EKXXXXXX6F",
      "linking_status": true,
      "reason": "linked",
      "detailed_reason": null
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "Invalid Aadhaar Number",
  "data": {
    "order_id": "W2A1739512345abcdef02",
    "result": {
      "masked_pan": "",
      "linking_status": false,
      "reason": "invalid_aadhaar",
      "detailed_reason": null
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/aadhaar-pan-link-check/errors](https://app.way2api.com/documentation/aadhaar-pan-link-check/errors).

## Common use cases

- **Pre-transaction compliance** — Confirm an Aadhaar and PAN pair is linked as the Income Tax Department mandates, before a filing or a high-value transaction.
- **Preventing inoperative PANs** — Identify customers whose PAN will stop working, and prompt them to link before it blocks their account.
- **Lending and insurance onboarding** — Add the linkage check to your KYC waterfall so a compliance gap never reaches underwriting.
- **Payroll and TDS hygiene** — Screen employee and contractor records in bulk to keep deduction rates correct.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/aadhaar-pan-link-check/rate-limits](https://app.way2api.com/documentation/aadhaar-pan-link-check/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/aadhaar-pan-link-check)
- [Request reference](https://app.way2api.com/documentation/aadhaar-pan-link-check/request)
- [Response reference](https://app.way2api.com/documentation/aadhaar-pan-link-check/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `aadhaar-pan-link-check`
