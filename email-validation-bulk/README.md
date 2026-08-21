# Bulk Email Validation

**Bulk Email Validation API** — the same deliverability check as **Email Validation**, for up to **100 addresses in a single billable call**. Every entry in **results** has exactly the shape a single-address lookup returns, so one parser handles both endpoints. Duplicate addresses are collapsed before the lookup, and **total_requested** versus **total_returned** lets you detect a short response. Built for cleaning imported lists, validating CSV uploads and periodic re-verification of a mailing database.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/email/validate/bulk` |
| **Category** | Utilities |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `email_validation_bulk` |
| **Full documentation** | [https://app.way2api.com/documentation/email-validation-bulk](https://app.way2api.com/documentation/email-validation-bulk) |

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
| `emails` | array | Yes | Array of 1–100 email addresses to validate, e.g. ["a@example.com", "b@example.com"]. Duplicates are removed. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/email/validate/bulk" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "emails": [
      "emma.thompson@example.com",
      "not-an-email"
    ]
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/email/validate/bulk";

const payload = {
  "emails": [
    "emma.thompson@example.com",
    "not-an-email"
  ]
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
ENDPOINT = "https://app.way2api.com/api/v1/email/validate/bulk"

payload = {
    "emails": [
        "emma.thompson@example.com",
        "not-an-email"
    ]
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
      "total_requested": 3,
      "total_returned": 3,
      "results": [
        {
          "email": "emma.thompson@example.com",
          "result": "valid",
          "is_valid": true,
          "is_syntax_valid": true,
          "reason": "",
          "domain": {
            "name": "example.com",
            "is_valid": true,
            "is_disposable": false,
            "is_free": false,
            "is_spam": false,
            "is_catch_all": false
          },
          "account": {
            "is_role": false,
            "is_full_mailbox": false
          },
          "mx_records": [
            "mx1.example.com.",
            "mx2.example.com.",
            "mx3.example.com.",
            "mx4.example.com.",
            "mx5.example.com."
          ]
        },
        {
          "email": "not-an-email",
          "result": "invalid_syntax",
          "is_valid": false,
          "is_syntax_valid": false,
          "reason": "",
          "domain": {
            "name": "not-an-email",
            "is_valid": false,
            "is_disposable": false,
            "is_free": false,
            "is_spam": false,
            "is_catch_all": false
          },
          "account": {
            "is_role": false,
            "is_full_mailbox": false
          },
          "mx_records": []
        },
        {
          "email": "test@nonexistentdomainxyz123abc.com",
          "result": "invalid",
          "is_valid": false,
          "is_syntax_valid": true,
          "reason": "mx record does not exist.",
          "domain": {
            "name": "nonexistentdomainxyz123abc.com",
            "is_valid": false,
            "is_disposable": false,
            "is_free": false,
            "is_spam": false,
            "is_catch_all": false
          },
          "account": {
            "is_role": false,
            "is_full_mailbox": false
          },
          "mx_records": []
        }
      ]
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "Please provide data in required format in request body",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "Invalid request body Exception"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/email-validation-bulk/errors](https://app.way2api.com/documentation/email-validation-bulk/errors).

## Common use cases

- **List cleaning before a campaign** — Validate up to 100 addresses in a single billable call before a send.
- **Imported data hygiene** — Clean a purchased or migrated list before it enters your CRM.
- **One parser for both endpoints** — Read entries in exactly the shape a single-address lookup returns.
- **Short response detection** — Compare total_requested against total_returned to confirm nothing was silently dropped.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/email-validation-bulk/rate-limits](https://app.way2api.com/documentation/email-validation-bulk/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/email-validation-bulk)
- [Request reference](https://app.way2api.com/documentation/email-validation-bulk/request)
- [Response reference](https://app.way2api.com/documentation/email-validation-bulk/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `email-validation-bulk`
