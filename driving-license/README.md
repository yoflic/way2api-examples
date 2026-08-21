# Driving License Verify

**Driving License Verification API** — Verify any Indian **driving license** number with date of birth against government RTO records in real time. Returns the license holder's **name, address, gender, father/husband name, date of birth, issue and expiry dates, vehicle classes, issuing authority**, and profile image availability. Useful for KYC onboarding, identity verification, ride-hailing driver validation, and fleet compliance. The **profile image** is excluded from the response for privacy.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/driving-license/verify` |
| **Category** | Identity and Security |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `dl_verify` |
| **Full documentation** | [https://app.way2api.com/documentation/driving-license](https://app.way2api.com/documentation/driving-license) |

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
| `dl_number` | string | Yes | Driving license number (10-20 characters, e.g. MH0320140001234). |
| `dob` | string | Yes | Date of birth of the license holder in dd/mm/yyyy format. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/driving-license/verify" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "dob": "15/06/1992",
    "dl_number": "MH0320140001234"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/driving-license/verify";

const payload = {
  "dob": "15/06/1992",
  "dl_number": "MH0320140001234"
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
ENDPOINT = "https://app.way2api.com/api/v1/driving-license/verify"

payload = {
    "dob": "15/06/1992",
    "dl_number": "MH0320140001234"
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
      "license_number": "MH0320140001234",
      "state": "Maharashtra",
      "name": "PRIYA PATEL",
      "permanent_address": "42 MG ROAD, PUNE, MAHARASHTRA",
      "permanent_zip": "411001",
      "temporary_address": "42 MG ROAD, PUNE, MAHARASHTRA",
      "temporary_zip": "411001",
      "citizenship": "",
      "ola_name": "RTO PUNE",
      "ola_code": "MH032",
      "gender": "F",
      "father_or_husband_name": "MAHESH PATEL",
      "dob": "1992-06-15",
      "doe": "2034-08-10",
      "transport_doe": "1800-01-01",
      "doi": "2014-08-11",
      "transport_doi": "1800-01-01",
      "has_image": true,
      "blood_group": "B+",
      "vehicle_classes": [
        "MCWG",
        "LMV-NT"
      ],
      "less_info": false
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
      "license_number": "MH0320140001234",
      "state": null,
      "name": null,
      "permanent_address": null,
      "gender": null,
      "father_or_husband_name": null,
      "dob": "1992-06-15",
      "doe": null,
      "doi": null,
      "has_image": false,
      "vehicle_classes": [],
      "less_info": false
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/driving-license/errors](https://app.way2api.com/documentation/driving-license/errors).

## Common use cases

- **Driver onboarding** — Verify a driving licence against RTO records before a ride-hailing, delivery or logistics driver takes a first job.
- **Vehicle class eligibility** — Confirm the licence actually covers the class of vehicle you are about to assign.
- **Expiry monitoring** — Track issue and expiry dates across a fleet and prompt renewal before a driver falls out of compliance.
- **Identity plus address in one call** — Use the returned name, date of birth, parent name and address as a second identity source during KYC.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/driving-license/rate-limits](https://app.way2api.com/documentation/driving-license/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/driving-license)
- [Request reference](https://app.way2api.com/documentation/driving-license/request)
- [Response reference](https://app.way2api.com/documentation/driving-license/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `driving-license`
