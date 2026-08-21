# Currency Exchange Rates

**Currency Exchange Rates API** — latest exchange rates for a base currency, refreshed continuously. Defaults to a **USD** base against every quoted currency; pass **base** to change it and **symbols** to narrow the result to the codes you actually need. Covers **fiat, crypto and precious metals**. **rates** is a plain code-to-rate map keyed by whatever you asked for, and **as_of** is the UTC timestamp the quote was taken. Built for pricing pages, multi-currency checkout and invoice conversion.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/currency/rates` |
| **Category** | Financial |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `currency_rates` |
| **Full documentation** | [https://app.way2api.com/documentation/currency-rates](https://app.way2api.com/documentation/currency-rates) |

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
| `base` | string | No | Base currency code, 3–5 letters. Defaults to USD. Accepts fiat, crypto and metal codes. |
| `symbols` | string | No | Comma-separated currency codes to return, e.g. "INR,EUR,GBP". Defaults to every quoted currency. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/currency/rates" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "base": "USD",
    "symbols": "INR,EUR,GBP"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/currency/rates";

const payload = {
  "base": "USD",
  "symbols": "INR,EUR,GBP"
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
ENDPOINT = "https://app.way2api.com/api/v1/currency/rates"

payload = {
    "base": "USD",
    "symbols": "INR,EUR,GBP"
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
      "base_currency": "USD",
      "rates": {
        "EUR": "0.868282",
        "GBP": "0.743799",
        "INR": "95.0968"
      },
      "as_of": "2026-08-04 13:32:00+00"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "Rates of provided currency ZZZ are not available in our database!",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "Not Found Exception"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/currency-rates/errors](https://app.way2api.com/documentation/currency-rates/errors).

## Common use cases

- **Multi-currency pricing** — Convert catalogue prices into a visitor local currency using continuously refreshed rates.
- **Invoice and settlement conversion** — Record the rate and the as_of UTC timestamp used, so a conversion can be audited later.
- **Narrow payload requests** — Pass symbols to return only the currency codes you actually need.
- **Crypto and metals coverage** — Quote against crypto and precious metals from the same endpoint as fiat.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/currency-rates/rate-limits](https://app.way2api.com/documentation/currency-rates/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/currency-rates)
- [Request reference](https://app.way2api.com/documentation/currency-rates/request)
- [Response reference](https://app.way2api.com/documentation/currency-rates/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `currency-rates`
