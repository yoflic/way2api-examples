# Bank Account Validation

**Bank Account Verification API** — Instantly verify Indian bank account details. Returns the **account holder name and account status** for payout processing, KYC, and compliance workflows.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/bank/account_validation` |
| **Category** | Identity and Security |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `bank_account_validation` |
| **Full documentation** | [https://app.way2api.com/documentation/bank-account-validation](https://app.way2api.com/documentation/bank-account-validation) |

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
| `account_number` | string | Yes | Bank account number to validate (6–18 digits). |
| `ifsc_code` | string | Yes | IFSC code of the account's branch (e.g. ABCD0123456). |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/bank/account_validation" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "account_number": "1234567890",
    "ifsc_code": "HDFC0001234"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/bank/account_validation";

const payload = {
  "account_number": "1234567890",
  "ifsc_code": "HDFC0001234"
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
ENDPOINT = "https://app.way2api.com/api/v1/bank/account_validation"

payload = {
    "account_number": "1234567890",
    "ifsc_code": "HDFC0001234"
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
      "account_number": "1234567890",
      "ifsc_code": "HDFC0001234",
      "account_holder": "Ananya Sharma",
      "name_match_score": "",
      "account_type": "",
      "bank_name": "HDFC BANK",
      "branch_name": "MUMBAI",
      "branch_state": "MAHARASHTRA",
      "is_penny_drop": true
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `422`:

```json
{
  "success": false,
  "message": "ifsc_code must be a valid IFSC code (e.g. ABCD0123456).",
  "data": null
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/bank-account-validation/errors](https://app.way2api.com/documentation/bank-account-validation/errors).

## Common use cases

- **Payout pre-validation** — Confirm an account number and IFSC accept credit before you send money, so a transfer does not bounce back days later.
- **Name match at onboarding** — Compare the returned account holder name with the name on file to catch a mistyped or borrowed account.
- **Refund and settlement accuracy** — Validate the destination account before issuing a refund or a merchant settlement.
- **Reducing failed transfer costs** — Catch bad account details up front instead of paying for a failed NEFT or IMPS attempt.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/bank-account-validation/rate-limits](https://app.way2api.com/documentation/bank-account-validation/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/bank-account-validation)
- [Request reference](https://app.way2api.com/documentation/bank-account-validation/request)
- [Response reference](https://app.way2api.com/documentation/bank-account-validation/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `bank-account-validation`
