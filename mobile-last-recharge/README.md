# Last Mobile Recharge Status

**Last Mobile Recharge Status API** — Check when a prepaid mobile number was **last recharged** and for **how much**. Returns the last recharge **amount** and **recharge_date**. Useful for recharge reminders, churn and dormancy analysis, retailer commission checks, and confirming whether a top-up already landed before retrying it. Currently supported for **Airtel** and **VI (Vodafone Idea)** prepaid numbers only.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/mobile/last-recharge` |
| **Category** | Telecom |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `mobile_last_recharge` |
| **Full documentation** | [https://app.way2api.com/documentation/mobile-last-recharge](https://app.way2api.com/documentation/mobile-last-recharge) |

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
| `mobile_number` | string | Yes | 10-digit Indian mobile number — format: [6-9][0-9]{9} (e.g. 9876543210) |
| `operator` | string | Yes | Prepaid operator of the number. Accepted values: airtel, vi (Vodafone Idea). Other operators (jio, bsnl) are not supported by the source yet and are rejected without charge. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/mobile/last-recharge" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "mobile_number": "9876543210",
    "operator": "airtel"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/mobile/last-recharge";

const payload = {
  "mobile_number": "9876543210",
  "operator": "airtel"
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
ENDPOINT = "https://app.way2api.com/api/v1/mobile/last-recharge"

payload = {
    "mobile_number": "9876543210",
    "operator": "airtel"
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
      "mobile_number": "9876543210",
      "operator": "airtel",
      "amount": "1799",
      "recharge_date": "2024-11-14"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "Last recharge details not available for this number",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "11"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/mobile-last-recharge/errors](https://app.way2api.com/documentation/mobile-last-recharge/errors).

## Common use cases

- **Duplicate top-up prevention** — Check whether a recharge already landed before retrying a transaction the customer says failed.
- **Churn and dormancy analysis** — Identify prepaid numbers that have not recharged recently and target them for reactivation.
- **Recharge reminders** — Time a reminder from the last recharge date instead of sending it blind.
- **Retailer commission checks** — Confirm a claimed recharge actually happened and for the amount claimed.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/mobile-last-recharge/rate-limits](https://app.way2api.com/documentation/mobile-last-recharge/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/mobile-last-recharge)
- [Request reference](https://app.way2api.com/documentation/mobile-last-recharge/request)
- [Response reference](https://app.way2api.com/documentation/mobile-last-recharge/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `mobile-last-recharge`
