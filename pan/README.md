# PAN Verification

**PAN Verification API** — Verify a 10-character **Permanent Account Number (PAN)** issued by the Indian Income Tax Department. Essential for financial KYC, income tax compliance, and onboarding workflows in the banking, insurance, and lending sectors. Returns the PAN holder's **full name, PAN status** (Existing and Valid, Deactivated, etc.), **category** (Individual, Company, HUF, Trust, etc.), **Aadhaar seeding status**, and other detailed information. Validates the PAN format and checks it against official government records.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/pan/verify` |
| **Category** | Identity and Security |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `pan_verify` |
| **Full documentation** | [https://app.way2api.com/documentation/pan](https://app.way2api.com/documentation/pan) |

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
| `pan_number` | string | Yes | 10-character PAN — format: [A-Z]{5}[0-9]{4}[A-Z] (e.g. ABCDE1234F) |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/pan/verify" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "pan_number": "ABCDE1234F"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/pan/verify";

const payload = {
  "pan_number": "ABCDE1234F"
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
ENDPOINT = "https://app.way2api.com/api/v1/pan/verify"

payload = {
    "pan_number": "ABCDE1234F"
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
      "pan_number": "ABCDE1234F",
      "full_name": "ANANYA SHARMA",
      "title": "",
      "full_name_split": [
        "",
        "",
        "ANANYA SHARMA"
      ],
      "pan_status": "E",
      "pan_status_desc": "EXISTING AND VALID",
      "aadhaar_seeding_status": "Y",
      "aadhaar_seeding_status_desc": "Seeded",
      "pan_modified_date": null,
      "category": "individual",
      "client_id": "pan_advanced_v2_xxxxxxxxxxxxxxxxxxxx"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "Invalid PAN",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "VERIFICATION_FAILED",
    "result": {
      "pan_number": "ABCDE12340",
      "full_name": "",
      "title": "",
      "full_name_split": [],
      "pan_status": "",
      "pan_status_desc": "",
      "aadhaar_seeding_status": "",
      "aadhaar_seeding_status_desc": "",
      "pan_modified_date": null,
      "category": "company"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/pan/errors](https://app.way2api.com/documentation/pan/errors).

## Common use cases

- **Financial KYC** — Validate a PAN before opening a broking, lending or insurance account, and confirm the holder name matches the application.
- **Tax compliance and TDS** — Check PAN status and category before deducting TDS, so you apply the right rate and avoid a higher-rate penalty.
- **Vendor and merchant onboarding** — Verify the PAN of a seller or supplier before the first payout to keep your payables ledger clean.
- **Aadhaar seeding checks** — Read the Aadhaar seeding status to see whether a PAN is at risk of becoming inoperative.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/pan/rate-limits](https://app.way2api.com/documentation/pan/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/pan)
- [Request reference](https://app.way2api.com/documentation/pan/request)
- [Response reference](https://app.way2api.com/documentation/pan/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `pan`
