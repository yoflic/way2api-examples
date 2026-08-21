# Vehicle RC Text and PDF

**Vehicle RC Text and PDF API** — The **Registration Certificate (RC)** for a vehicle returned **both ways in a single call**: the full RC record as JSON, and a **pdf_url** pointing at a ready-to-print **smart-card / A4 PDF** of the same certificate. Send a vehicle **registration number** and receive the owner and address, chassis and engine numbers, make, model, body type, colour, fuel and emission norms, seating and weight specifications, cubic capacity, registering RTO, fitness and road-tax validity, hypothecation and financer, insurance and PUCC validity, permit details, and blacklist / NOC status — plus the document link. Dates come back as **ISO YYYY-MM-DD** and **rc_status** is published from a stable value set, so both the shape and the values of this response are guaranteed not to move. **pdf_url** is an empty string in the rare case where a document could not be produced; the record itself is unaffected, so check it before following the link. Generated documents stay downloadable for a limited time — fetch and store the file promptly rather than holding the link. Use this endpoint when you need to both read the RC data and hand someone a printable certificate; the **Vehicle RC Verification** and **Vehicle RC PDF** endpoints sell those halves separately. Built for vehicle onboarding, fleet and dealership records, insurance and loan files, and RTO-facing paperwork.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/rc/text-pdf` |
| **Category** | Verification |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `rc_text_pdf` |
| **Full documentation** | [https://app.way2api.com/documentation/vehicle-rc-text-pdf](https://app.way2api.com/documentation/vehicle-rc-text-pdf) |

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
curl -X POST "https://app.way2api.com/api/v1/rc/text-pdf" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "rc_number": "DL3CAB1234"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/rc/text-pdf";

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
ENDPOINT = "https://app.way2api.com/api/v1/rc/text-pdf"

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
      "rc_number": "OD05AB1234",
      "registration_date": "2024-03-20",
      "rc_status": "ACTIVE",
      "less_info": false,
      "latest_by": "2026-08-11",
      "owner_name": "SNEHA MOHANTY",
      "father_name": "",
      "owner_number": "1",
      "masked_name": false,
      "mobile_number": "",
      "present_address": "Jagatsinghapur, 754119",
      "permanent_address": "Jagatsinghapur, 754119",
      "vehicle_category": "2WN",
      "vehicle_category_description": "M-Cycle/Scooter(2WN)",
      "vehicle_chasi_number": "ME1AB1234C5678901",
      "vehicle_engine_number": "G3AB1C234567",
      "maker_description": "INDIA YAMAHA MOTOR PVT LTD",
      "maker_model": "YAMAHA FZS VERSION 4.0",
      "variant": null,
      "body_type": "SOLO WITH PILLION",
      "fuel_type": "PETROL(E20)",
      "color": "DEEP PURPLISH BLUE",
      "norms_type": "BHARAT STAGE VI",
      "manufacturing_date": "1/2024",
      "manufacturing_date_formatted": "2024-01",
      "cubic_capacity": "149.00",
      "no_cylinders": "1",
      "seat_capacity": "2",
      "sleeper_capacity": "0",
      "standing_capacity": "0",
      "wheelbase": "1330",
      "unladen_weight": "135",
      "vehicle_gross_weight": "285",
      "registered_at": "CUTTACK RTO, Odisha",
      "rto_code": "",
      "fit_up_to": "2039-03-19",
      "tax_upto": "2039-03-19",
      "tax_paid_upto": "2039-03-19",
      "financed": true,
      "financer": "EXAMPLE CAPITAL LTD",
      "insurance_company": "Example General Insurance Co. Ltd.",
      "insurance_policy_number": "3410/12345678/000/00",
      "insurance_upto": "2029-03-18",
      "pucc_number": "OR12345678901234",
      "pucc_upto": "2026-11-02",
      "permit_number": "",
      "permit_type": "",
      "permit_issue_date": null,
      "permit_valid_from": null,
      "permit_valid_upto": null,
      "national_permit_number": "",
      "national_permit_upto": null,
      "national_permit_issued_by": null,
      "non_use_status": null,
      "non_use_from": null,
      "non_use_to": null,
      "blacklist_status": "",
      "noc_details": "",
      "challan_details": null,
      "response_metadata": {
        "masked_chassis": false,
        "masked_engine": false,
        "masked_owner_name": false
      },
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/vehicle-rc-text-pdf/errors](https://app.way2api.com/documentation/vehicle-rc-text-pdf/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/vehicle-rc-text-pdf/rate-limits](https://app.way2api.com/documentation/vehicle-rc-text-pdf/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/vehicle-rc-text-pdf)
- [Request reference](https://app.way2api.com/documentation/vehicle-rc-text-pdf/request)
- [Response reference](https://app.way2api.com/documentation/vehicle-rc-text-pdf/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `vehicle-rc-text-pdf`
