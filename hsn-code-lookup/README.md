# HSN Code Lookup

**HSN Code Lookup API** — Validate an **HSN code** (goods) or **SAC code** (services) and get what the code actually covers: its official **tariff description**, the **chapter** it sits under, the applicable **GST rate**, the rate-revision history, the **cess** note, and the date each revision took effect. Accepts **2, 4, 6 or 8-digit HSN codes** and **6-digit SAC codes**, and reports which schedule the code came from in **code_type**. Rates come back twice — in the schedule's own notation in **gst_rate** ("5/12/18") and as a numeric list in **gst_rate_percent** ([5, 12, 18]) so they can be compared without parsing — and every effective date is returned as an **ISO date**, most recent revision first. Use it to check an **HSN code and its GST rate** before raising an invoice, validate the codes a supplier put on an incoming purchase invoice, and keep a product or service catalogue tax-correct.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/gst/hsn-code` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `hsn_code_details` |
| **Full documentation** | [https://app.way2api.com/documentation/hsn-code-lookup](https://app.way2api.com/documentation/hsn-code-lookup) |

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
| `hsn_code` | string | Yes | HSN code for goods (2, 4, 6 or 8 digits) or SAC code for services (6 digits, beginning 99). Digits only. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/gst/hsn-code" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "hsn_code": "998319"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/gst/hsn-code";

const payload = {
  "hsn_code": "998319"
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
ENDPOINT = "https://app.way2api.com/api/v1/gst/hsn-code"

payload = {
    "hsn_code": "998319"
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
      "hsn_code": "998319",
      "code_type": "SAC",
      "description": "OTHER INFORMATION TECHNOLOGY SERVICES N.E.C",
      "chapter_number": 99,
      "chapter_name": "Services",
      "gst_rate": "5/12/18",
      "gst_rate_percent": [
        5,
        12,
        18
      ],
      "rate_revision": "12% 5% 18%",
      "effective_date": "2019-10-01",
      "effective_dates": [
        "2019-10-01",
        "2017-07-01"
      ],
      "cess": "Nil Provided that Director (Sports), Ministry of Youth Affairs and Sports certifies that the services are directly or indirectly related to any of the events under FIFA U-17 Women's World Cup 2020."
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "No HSN or SAC record was found for the code provided.",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "NO_RECORD",
    "result": {
      "hsn_code": "9999999",
      "code_type": "",
      "description": "",
      "chapter_number": 0,
      "chapter_name": "",
      "gst_rate": "",
      "gst_rate_percent": [],
      "rate_revision": "",
      "effective_date": null,
      "effective_dates": [],
      "cess": ""
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/hsn-code-lookup/errors](https://app.way2api.com/documentation/hsn-code-lookup/errors).

## Common use cases

- **Invoice and e-invoice validation** — Confirm the HSN or SAC on an invoice line exists and carries the rate you charged, before the invoice is filed.
- **Product catalogue tax mapping** — Attach a verified code and GST rate to every SKU so billing, e-invoicing and GSTR-1 all agree.
- **Purchase invoice checks** — Validate the codes a supplier applied on an incoming invoice rather than accepting the rate they charged.
- **Rate revision awareness** — effective_dates shows when each revision took effect, so a historic rate is never mistaken for the current one.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/hsn-code-lookup/rate-limits](https://app.way2api.com/documentation/hsn-code-lookup/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/hsn-code-lookup)
- [Request reference](https://app.way2api.com/documentation/hsn-code-lookup/request)
- [Response reference](https://app.way2api.com/documentation/hsn-code-lookup/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `hsn-code-lookup`
