# Voter ID Verification

**Voter ID Verification API** — Verify any Indian **Voter ID (EPIC)** number against Election Commission records in real time. Returns the voter's **name, relation name, relation type, gender, age, date of birth, area, state**, and house number. Useful for KYC onboarding, identity verification, electoral compliance, and address validation workflows.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/voter-id/verify` |
| **Category** | Identity and Security |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `voterid_verify` |
| **Full documentation** | [https://app.way2api.com/documentation/voter-id](https://app.way2api.com/documentation/voter-id) |

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
| `voter_id` | string | Yes | 10-character EPIC (Voter ID) number (e.g. ABC1234567). |

## cURL

```bash
curl -X POST "https://app.way2api.com/api/v1/voter-id/verify" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "voter_id": "ABC1234567"
  }'
```

## Node.js

```javascript
const API_KEY = process.env.WAY2API_KEY || "YOUR_API_KEY";
const ENDPOINT = "https://app.way2api.com/api/v1/voter-id/verify";

const payload = {
  "voter_id": "ABC1234567"
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
ENDPOINT = "https://app.way2api.com/api/v1/voter-id/verify"

payload = {
    "voter_id": "ABC1234567"
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
      "relation_type": "F",
      "gender": "F",
      "age": "32",
      "epic_no": "ABC1234567",
      "dob": "1992-06-15",
      "relation_name": "RAMESH SHARMA",
      "name": "MEERA SHARMA",
      "area": "Sector 5, Dwarka",
      "state": "Delhi",
      "house_no": "42-B"
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
      "epic_no": "ABC1234567"
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/voter-id/errors](https://app.way2api.com/documentation/voter-id/errors).

## Common use cases

- **Alternative KYC document** — Accept a Voter ID where a customer has no PAN or does not want to share Aadhaar, and still get a verified identity.
- **Address validation** — Use the returned area, state and house number to corroborate an address a customer typed in.
- **Age verification** — Read the date of birth and age against Election Commission records for age-gated products.
- **Electoral and civic compliance** — Verify EPIC numbers where a programme requires a registered voter.

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/voter-id/rate-limits](https://app.way2api.com/documentation/voter-id/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/voter-id)
- [Request reference](https://app.way2api.com/documentation/voter-id/request)
- [Response reference](https://app.way2api.com/documentation/voter-id/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `voter-id`
