# DTH Operator Check

**DTH Operator Check API** — Identify which **DTH operator** a subscriber / customer ID belongs to, without asking the customer. Returns the machine-readable **operator** slug (e.g. **sun_direct**) plus the display **operator_name**. The returned slug is exactly the value the **DTH Advance Information** API expects, so the two chain together: identify the operator first, then pull the subscriber profile. Removes the most common cause of failed DTH recharges — the customer picking the wrong operator.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/dth/operator-check` |
| **Category** | Telecom |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `dth_operator_check` |
| **Full documentation** | [https://app.way2api.com/documentation/dth-operator-check](https://app.way2api.com/documentation/dth-operator-check) |

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
| `dth_number` | string | Yes | DTH subscriber / customer ID — 9 to 13 digits (e.g. 70512345661). Exact length depends on the operator. |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/dth/operator-check" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "dth_number": "70512345661"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/dth/operator-check";

const payload = {
  "dth_number": "70512345661"
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
ENDPOINT = "https://app.way2api.com/api/v1/dth/operator-check"

payload = {
    "dth_number": "70512345661"
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
      "dth_number": "70512345661",
      "operator": "sun_direct",
      "operator_name": "SUN DIRECT"
    }
  }
}
```

## Error handling

A failed request returns `success: false` with HTTP `400`:

```json
{
  "success": false,
  "message": "DTH operator could not be identified for this number",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "error_code": "3"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/dth-operator-check/errors](https://app.way2api.com/documentation/dth-operator-check/errors).

## Common use cases

- **Operator detection** — Identify the DTH operator from a customer ID alone, so the subscriber does not have to know it.
- **Chaining to the full profile** — Feed the returned operator slug straight into the DTH Advance Information API to pull the subscriber record.
- **Checkout friction removal** — Drop the operator dropdown from a recharge flow and remove the most common point of user error.
- **Failed recharge reduction** — Stop transactions being sent to the wrong operator and refunded days later.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/dth-operator-check/rate-limits](https://app.way2api.com/documentation/dth-operator-check/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/dth-operator-check)
- [Request reference](https://app.way2api.com/documentation/dth-operator-check/request)
- [Response reference](https://app.way2api.com/documentation/dth-operator-check/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `dth-operator-check`
