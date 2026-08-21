# Credit Report Equifax PDF

**Credit Report PDF API** — Fetch the **credit report as a downloadable PDF** for an individual using their **Aadhaar or PAN** number. Returns the **credit score** and a **PDF download link** for the full credit report. Useful for lending workflows, KYC, creditworthiness assessments, and financial compliance. Consent is automatically set to "Y".

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/credit-report/pdf` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `credit_report_pdf` |
| **Full documentation** | [https://app.way2api.com/documentation/credit-report-equifax-pdf](https://app.way2api.com/documentation/credit-report-equifax-pdf) |

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
| `name` | string | Yes | Full name of the individual. |
| `number` | string | Yes | 12-digit Aadhaar number or 10-character PAN (based on fetch_by). |
| `fetch_by` | string | Yes | Type of ID document: "aadhaar" or "pan". |
| `mobile` | string | Yes | 10-digit mobile number of the individual. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/credit-report/pdf" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ananya Sharma",
    "mobile": "9876543210",
    "number": "123456789012",
    "fetch_by": "aadhaar"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/credit-report/pdf";

const payload = {
  "name": "Ananya Sharma",
  "mobile": "9876543210",
  "number": "123456789012",
  "fetch_by": "aadhaar"
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
ENDPOINT = "https://app.way2api.com/api/v1/credit-report/pdf"

payload = {
    "name": "Ananya Sharma",
    "mobile": "9876543210",
    "number": "123456789012",
    "fetch_by": "aadhaar"
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
      "number": "123456789012",
      "fetch_by": "aadhaar",
      "mobile": "9876543210",
      "name": "Ananya Sharma",
      "credit_score": "750",
      "credit_report": {},
      "credit_report_link": "https://example.com/credit-report.pdf"
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
      "number": "123456789012",
      "fetch_by": "aadhaar",
      "mobile": "9876543210",
      "name": "Ananya Sharma",
      "credit_score": "",
      "credit_report": {},
      "credit_report_link": ""
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/credit-report-equifax-pdf/errors](https://app.way2api.com/documentation/credit-report-equifax-pdf/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/credit-report-equifax-pdf/rate-limits](https://app.way2api.com/documentation/credit-report-equifax-pdf/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/credit-report-equifax-pdf)
- [Request reference](https://app.way2api.com/documentation/credit-report-equifax-pdf/request)
- [Response reference](https://app.way2api.com/documentation/credit-report-equifax-pdf/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `credit-report-equifax-pdf`
