# Credit Report Experian PDF

**Credit Report Experian PDF API** — the same **Experian bureau enquiry** as the JSON endpoint, returning the **bureau score** and a **download URL for the report as a PDF**. The URL is served by Way2API, is tied to the **order_id** of the enquiry that produced it and carries its own signature — so it opens straight in a browser or any client and needs **no API key**. Treat the link itself as the credential: anyone holding it can read the report, so pass it on only to the person it belongs to. The download is **free** — the enquiry is what is charged. The link stops working **7 days** after the enquiry, and the report itself is generated on demand and often becomes unretrievable sooner, so download promptly or run the enquiry again for a fresh copy. Requires the individual explicit **consent**. Use the JSON endpoint instead when you need to read the report programmatically.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/credit-report-experian/fetch-report-pdf` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `credit_report_experian_pdf` |
| **Full documentation** | [https://app.way2api.com/documentation/credit-report-experian-pdf](https://app.way2api.com/documentation/credit-report-experian-pdf) |

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
| `name` | string | Yes | Full name of the individual as it appears on their PAN. 2 to 100 characters. |
| `mobile` | string | Yes | 10-digit Indian mobile number of the individual, starting 6-9. |
| `pan` | string | Yes | 10-character PAN of the individual, e.g. ABCDE1234F. Case-insensitive. |
| `consent` | string | Yes | Must be "Y". A credit bureau enquiry is only lawful with the individual explicit consent, and you must have obtained and retained it. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/credit-report-experian/fetch-report-pdf" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ananya Sharma",
    "mobile": "9876543210",
    "pan": "ABCDE1234F",
    "consent": "Y"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report-pdf";

const payload = {
  "name": "Ananya Sharma",
  "mobile": "9876543210",
  "pan": "ABCDE1234F",
  "consent": "Y"
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
ENDPOINT = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report-pdf"

payload = {
    "name": "Ananya Sharma",
    "mobile": "9876543210",
    "pan": "ABCDE1234F",
    "consent": "Y"
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
      "name": "SHALINI RAJPUT",
      "mobile": "9812345670",
      "pan": "FGHIJ5678K",
      "credit_score": 761,
      "report_url": "https://way2api.com/api/v1/credit-report-experian/report/W2A1739512345abcdef01/3f1c9a7d54e0b28c6a4d19f7be03c5d281af6e94"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "No credit records were found for the details provided.",
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/credit-report-experian-pdf/errors](https://app.way2api.com/documentation/credit-report-experian-pdf/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/credit-report-experian-pdf/rate-limits](https://app.way2api.com/documentation/credit-report-experian-pdf/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/credit-report-experian-pdf)
- [Request reference](https://app.way2api.com/documentation/credit-report-experian-pdf/request)
- [Response reference](https://app.way2api.com/documentation/credit-report-experian-pdf/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `credit-report-experian-pdf`
