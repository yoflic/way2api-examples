# DTH Advance Information

**DTH Advance Information API** — Pull the full subscriber profile behind a **DTH customer ID / VC number**. Returns the account **name**, **registered_mobile**, current **balance**, **monthly_amount**, account **status**, **plan**, **next_recharge_date**, **last_recharge_date**, **last_recharge_amount**, **switch_off_date** and the installation **address** (with city, district, state and pin_code). Built for DTH recharge platforms and retailer apps that need to confirm the right account and its dues before taking a payment. Fields the operator does not publish for a given account are returned as empty strings (or "N/A" where the operator sends that).

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/dth/info` |
| **Category** | Telecom |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `dth_info_advance` |
| **Full documentation** | [https://app.way2api.com/documentation/dth-info](https://app.way2api.com/documentation/dth-info) |

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
| `dth_number` | string | Yes | DTH subscriber / customer ID — 9 to 13 digits (e.g. 01234567890). The exact prefix and length are set by the operator and differ between them, so the operator rejects a well-formed ID that does not match its own scheme. |
| `operator` | string | Yes | DTH operator of the connection. Accepted values: airtel_dth, dish_tv, reliance_bigtv, sun_direct, tata_play (alias tata_sky), videocon_d2h (alias d2h). Use the DTH Operator Check API if the operator is unknown. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/dth/info" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "dth_number": "01234567890",
    "operator": "dish_tv"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/dth/info";

const payload = {
  "dth_number": "01234567890",
  "operator": "dish_tv"
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
ENDPOINT = "https://app.way2api.com/api/v1/dth/info"

payload = {
    "dth_number": "01234567890",
    "operator": "dish_tv"
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
      "dth_number": "01234567890",
      "operator": "dish_tv",
      "customer_id": "9700000123",
      "name": "Lakshmi Priya",
      "registered_mobile": "98XXXXXX10",
      "balance": "194.73",
      "monthly_amount": "",
      "status": "1",
      "plan": "",
      "next_recharge_date": "8/4/2026 12:00:00 AM",
      "last_recharge_date": "N/A",
      "last_recharge_amount": "N/A",
      "switch_off_date": "8/7/2026 12:00:00 AM",
      "address": "12 MAIN ROAD, VILLIANUR, PONDICHERRY, Pin - 605110",
      "city": "PONDICHERRY",
      "district": "",
      "state": "PONDICHERRY",
      "pin_code": "605110"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "Customer Id Starts with 0 and is 11 digits long",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "1"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/dth-info/errors](https://app.way2api.com/documentation/dth-info/errors).

## Common use cases

- **Recharge amount guidance** — Read the current balance, monthly amount and next recharge date to suggest what the customer should actually pay.
- **Retailer applications** — Show account name, plan and status so a shop assistant can confirm the right connection before charging.
- **Dunning and reactivation** — Use the switch-off date and account status to target subscribers before or just after disconnection.
- **Address confirmation** — Use the installation address with city, district, state and pin code to confirm the connection you are servicing.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/dth-info/rate-limits](https://app.way2api.com/documentation/dth-info/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/dth-info)
- [Request reference](https://app.way2api.com/documentation/dth-info/request)
- [Response reference](https://app.way2api.com/documentation/dth-info/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `dth-info`
