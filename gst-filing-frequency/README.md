# GST Filing Frequency

**GST Filing Frequency API** — Whether a **GSTIN** files its returns **monthly or quarterly**, quarter by quarter, for one financial year. Under the **QRMP scheme** a small taxpayer may switch preference between quarters, so the answer is a list: **Q1** through **Q4**, each marked **monthly** or **quarterly**, alongside a derived **filing_frequency** that summarises the whole year as monthly, quarterly or **mixed**. It is the missing half of a **GST compliance check**: a GSTR-1 absent for a month is only late if that taxpayer files monthly, and treating a quarterly filer as delinquent is the most common false positive in supplier monitoring. Use it to time **GSTR-2A / 2B reconciliation** to each supplier's real cadence, set the right **input tax credit** expectation, and stop chasing returns that were never due.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/gst/filing-frequency` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `gst_filing_frequency` |
| **Full documentation** | [https://app.way2api.com/documentation/gst-filing-frequency](https://app.way2api.com/documentation/gst-filing-frequency) |

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
curl -X POST "https://app.way2api.com/api/v1/gst/filing-frequency" \
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
const ENDPOINT = "https://app.way2api.com/api/v1/gst/filing-frequency";

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
ENDPOINT = "https://app.way2api.com/api/v1/gst/filing-frequency"

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
      "filing_frequency": "quarterly",
      "quarters": [
        {
          "quarter": "Q1",
          "frequency": "quarterly"
        },
        {
          "quarter": "Q2",
          "frequency": "quarterly"
        },
        {
          "quarter": "Q3",
          "frequency": "quarterly"
        },
        {
          "quarter": "Q4",
          "frequency": "quarterly"
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
      "filing_frequency": "unknown",
      "quarters": []
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/gst-filing-frequency/errors](https://app.way2api.com/documentation/gst-filing-frequency/errors).

## Common use cases

- **Read filing gaps correctly** — A missing monthly GSTR-1 means nothing for a quarterly filer. Check the preference before treating an absent return as non-compliance.
- **Time your reconciliation** — Run 2A/2B reconciliation against each supplier's actual cadence instead of assuming everyone files monthly.
- **QRMP scheme tracking** — Spot suppliers who moved between monthly and quarterly mid-year — the derived filing_frequency reports that as mixed in a single field.
- **Onboarding and reminders** — Capture the cadence once at onboarding and drive payment release and ITC-claim reminders from it.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/gst-filing-frequency/rate-limits](https://app.way2api.com/documentation/gst-filing-frequency/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/gst-filing-frequency)
- [Request reference](https://app.way2api.com/documentation/gst-filing-frequency/request)
- [Response reference](https://app.way2api.com/documentation/gst-filing-frequency/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `gst-filing-frequency`
