# TAN Adv

**TAN Advanced Verification API** — Verify any 10-character **TAN (Tax Deduction Account Number)** issued by the Income Tax Department. Returns the deductor's **full name, allotment date, registered address, email, and phone number**. Useful for employer/deductor KYC, TDS compliance, and business verification workflows.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/tan/fetch_adv` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `tan_adv` |
| **Full documentation** | [https://app.way2api.com/documentation/tan-advanced](https://app.way2api.com/documentation/tan-advanced) |

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
| `tan_number` | string | Yes | 10-character TAN number (e.g. ABCT12345E). |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/tan/fetch_adv" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "tan_number": "ABKT12345E"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/tan/fetch_adv";

const payload = {
  "tan_number": "ABKT12345E"
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
ENDPOINT = "https://app.way2api.com/api/v1/tan/fetch_adv"

payload = {
    "tan_number": "ABKT12345E"
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
      "tan": "ABKT12345E",
      "details": {
        "full_name": "SOLUTIONS PRIVATE LIMITED",
        "first_name": "",
        "middle_name": "",
        "last_name": "",
        "tan_allotment_date": "2050-12-18",
        "address": {
          "line_1": "H.NO 123 ,SHIV COLONY",
          "line_2": ",",
          "line_3": "",
          "line_4": "",
          "line_5": "PANCHKULA",
          "state_code": 99,
          "zip": "123456",
          "full": "H.NO.123 SHIV COLONY , PANCHKULA 123456"
        },
        "email_1": "INFO@EXAMPLE.COM",
        "email_2": "",
        "phone_number": "1234567890"
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
  "message": "Verification Failed.",
  "data": {
    "order_id": "W2A1739512345abcdef02",
    "result": {
      "tan": "RTKT12345",
      "details": {}
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/tan-advanced/errors](https://app.way2api.com/documentation/tan-advanced/errors).

## Common use cases

- **Deductor verification** — Confirm a TAN belongs to the organisation named on a TDS certificate before you accept it.
- **Employer and payroll onboarding** — Verify an employer TAN with its registered address and contact details during business KYC.
- **TDS compliance** — Validate the TAN on file before filing returns, so a rejected filing does not cost you a penalty.
- **Vendor master cleanup** — Re-verify TAN records across a supplier database and correct stale addresses.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/tan-advanced/rate-limits](https://app.way2api.com/documentation/tan-advanced/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/tan-advanced)
- [Request reference](https://app.way2api.com/documentation/tan-advanced/request)
- [Response reference](https://app.way2api.com/documentation/tan-advanced/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `tan-advanced`
