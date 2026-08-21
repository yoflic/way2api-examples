# Email Validation

**Email Validation API** — checks whether an address can actually receive mail, without sending one. **result** is a single verdict you can switch on: **valid**, **invalid**, **invalid_syntax**, **risky** or **unknown**, with **is_valid** as the boolean shortcut and **reason** explaining a rejection. The domain block classifies the receiving domain — **is_disposable** (throwaway inbox), **is_free** (consumer webmail), **is_spam**, **is_catch_all** and **is_valid** — while the account block flags **role addresses** (info@, support@) and **full mailboxes**. Resolved **mx_records** are returned too. Built for signup gating, list hygiene and bounce-rate reduction.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/email/validate` |
| **Category** | Utilities |
| **Coverage** | Global |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `email_validation` |
| **Full documentation** | [https://app.way2api.com/documentation/email-validation](https://app.way2api.com/documentation/email-validation) |

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
| `email` | string | Yes | Email address to validate, e.g. emma.thompson@example.com. Max 254 characters. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/email/validate" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "emma.thompson@example.com"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/email/validate";

const payload = {
  "email": "emma.thompson@example.com"
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
ENDPOINT = "https://app.way2api.com/api/v1/email/validate"

payload = {
    "email": "emma.thompson@example.com"
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
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "Please pass valid value for 'email' in body.",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "External API Error"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/email-validation/errors](https://app.way2api.com/documentation/email-validation/errors).

## Common use cases

- **Signup form validation** — Check an address can actually receive mail before you create the account, without sending anything.
- **Disposable address blocking** — Reject throwaway inboxes at registration using the is_disposable classification.
- **Deliverability protection** — Keep invalid addresses out of your list so your sender reputation is not damaged.
- **Risk-aware routing** — Switch on a single verdict of valid, invalid, invalid_syntax, risky or unknown and handle each differently.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/email-validation/rate-limits](https://app.way2api.com/documentation/email-validation/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/email-validation)
- [Request reference](https://app.way2api.com/documentation/email-validation/request)
- [Response reference](https://app.way2api.com/documentation/email-validation/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `email-validation`
