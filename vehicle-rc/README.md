# Vehicle RC Verification

**Vehicle RC Verification API** — Fetch full **Registration Certificate (RC)** details for any Indian vehicle using its **registration number**. Returns comprehensive vehicle information including **owner name, vehicle make/model, fuel type, insurance details, fitness status, tax details, PUCC status, financer info**, and more. Useful for used-car platforms, insurance underwriting, fleet management, RTO compliance, and loan verification. Optional chassis and engine number parameters for additional cross-verification.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/rc/verify` |
| **Category** | Verification |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `rc` |
| **Full documentation** | [https://app.way2api.com/documentation/vehicle-rc](https://app.way2api.com/documentation/vehicle-rc) |

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
| `rc_number` | string | Yes | Vehicle registration number (e.g. DL3CAB1234, MH12AB1234). |
| `chassis_number` | string | No | Chassis number for additional verification (optional). |
| `engine_number` | string | No | Engine number for additional verification (optional). |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/rc/verify" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "rc_number": "DL3CAB1234"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/rc/verify";

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
ENDPOINT = "https://app.way2api.com/api/v1/rc/verify"

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
      "rc_number": "DL3CAB1234",
      "fit_up_to": "2037-03-20",
      "registration_date": "2022-03-21",
      "owner_name": "Anjali Deshmukh",
      "father_name": "",
      "present_address": "12 Sector 9, Dwarka, New Delhi, 110075",
      "permanent_address": "12 Sector 9, Dwarka, New Delhi, 110075",
      "vehicle_category": "LMV",
      "vehicle_chasi_number": "MA1AB2CD3EF456789",
      "vehicle_engine_number": "0000A123",
      "maker_description": "BMW INDIA PVT LTD",
      "maker_model": "BMW 220I GRAN COUPE M SPORT",
      "body_type": "SEDAN",
      "fuel_type": "PETROL",
      "color": "SNAPPER ROCKS BLUE M",
      "norms_type": "BHARAT STAGE VI",
      "financer": "EXAMPLE BANK LTD",
      "financed": true,
      "insurance_company": "Example General Insurance Co. Ltd.",
      "insurance_policy_number": "P1234567890",
      "insurance_upto": "2026-05-20",
      "manufacturing_date_formatted": "2022-03",
      "registered_at": "SOUTH DELHI, Delhi",
      "rc_status": "ACTIVE"
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
    "order_id": "W2A1739512345abcdef01",
    "result": {
      "rc_number": "KA12AC3456",
      "registration_date": null,
      "owner_name": null,
      "vehicle_category": null,
      "maker_model": null,
      "fuel_type": null,
      "rc_status": null
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/vehicle-rc/errors](https://app.way2api.com/documentation/vehicle-rc/errors).

## Common use cases

- **Used car listings** — Pull make, model, fuel type and registration details from a number plate so a seller does not type them in wrong.
- **Insurance underwriting** — Read insurance validity, fitness status, PUCC and tax details before quoting a policy.
- **Fleet and compliance management** — Monitor fitness, tax and pollution certificate expiry across a fleet.
- **Finance and hypothecation checks** — See the financer on record before accepting a vehicle as collateral or completing a resale.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/vehicle-rc/rate-limits](https://app.way2api.com/documentation/vehicle-rc/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/vehicle-rc)
- [Request reference](https://app.way2api.com/documentation/vehicle-rc/request)
- [Response reference](https://app.way2api.com/documentation/vehicle-rc/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `vehicle-rc`
