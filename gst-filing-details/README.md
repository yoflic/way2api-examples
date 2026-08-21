# GST Filing Details

**GST Filing Details API** — Every **GST return** a **GSTIN** filed in one financial year, read from the government record. Send a GSTIN and a year such as **2023-24** and receive one entry per return: the form (**GSTR-1**, **GSTR-3B**, **GSTR-9**, **CMP-08** and the rest), the **tax period** as **YYYY-MM**, the **ARN** acknowledgement number, the **date of filing** as an ISO date, whether it was filed online or offline, and its status. This is how you check a counterparty's **GST return filing status** and **compliance history** before extending credit, onboarding a supplier, or claiming **input tax credit** — ITC depends on the supplier having actually filed, and a gap in GSTR-1 or GSTR-3B is the earliest signal that it is at risk. Entries are returned **most recent tax period first**, and **filing_count** reports how many were found for the year.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/gst/filing-details` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `gst_filing_details` |
| **Full documentation** | [https://app.way2api.com/documentation/gst-filing-details](https://app.way2api.com/documentation/gst-filing-details) |

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
| `gst_number` | string | Yes | 15-character GSTIN, e.g. 10ABCDE1234F1Z5. Case-insensitive. |
| `financial_year` | string | Yes | Indian financial year (April to March), 2017-18 onwards. Accepts 2023-24, 2023-2024 or 202324. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/gst/filing-details" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "gst_number": "10ABCDE1234F1Z5",
    "financial_year": "2023-24"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/gst/filing-details";

const payload = {
  "gst_number": "10ABCDE1234F1Z5",
  "financial_year": "2023-24"
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
ENDPOINT = "https://app.way2api.com/api/v1/gst/filing-details"

payload = {
    "gst_number": "10ABCDE1234F1Z5",
    "financial_year": "2023-24"
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
      "gst_number": "10ABCDE1234F1Z5",
      "financial_year": "2023-24",
      "filing_count": 16,
      "filings": [
        {
          "return_type": "GSTR1",
          "return_period": "2024-03",
          "arn": "AA030324078315H",
          "filed_on": "2024-04-08",
          "filing_mode": "online",
          "status": "filed",
          "is_valid": true
        },
        {
          "return_type": "GSTR3B",
          "return_period": "2024-03",
          "arn": "AA0303244598718",
          "filed_on": "2024-04-19",
          "filing_mode": "online",
          "status": "filed",
          "is_valid": true
        },
        {
          "return_type": "GSTR1",
          "return_period": "2024-02",
          "arn": "AA0302241450008",
          "filed_on": "2024-03-10",
          "filing_mode": "online",
          "status": "filed",
          "is_valid": true
        }
      ]
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "Invalid GSTIN",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "INVALID_GSTIN",
    "result": {
      "gst_number": "10ABCDE1234F1Z9",
      "financial_year": "2023-24",
      "filing_count": 0,
      "filings": []
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/gst-filing-details/errors](https://app.way2api.com/documentation/gst-filing-details/errors).

## Common use cases

- **Supplier due diligence** — Confirm a supplier filed GSTR-1 and GSTR-3B for the year before onboarding them or releasing a payment.
- **Protecting input tax credit** — ITC depends on the supplier's GSTR-1 for the tax period. Check the return was filed before claiming against it.
- **Credit and lending decisions** — An unbroken filing history is evidence of an operating business; missing periods and late ARNs are an early risk signal.
- **Portfolio compliance monitoring** — Re-check a book of GSTINs each year and flag any that stopped filing, without asking them for documents.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/gst-filing-details/rate-limits](https://app.way2api.com/documentation/gst-filing-details/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/gst-filing-details)
- [Request reference](https://app.way2api.com/documentation/gst-filing-details/request)
- [Response reference](https://app.way2api.com/documentation/gst-filing-details/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `gst-filing-details`
