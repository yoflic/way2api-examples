# Credit Report Experian

**Credit Report Experian API** — pull a full **Experian consumer credit report** for an individual from their **PAN and mobile number**, with the individual explicit **consent**. Returns the **bureau score**, the applicant record as the bureau holds it, an **account summary** (total, active, closed and defaulted accounts, plus secured versus unsecured outstanding), every **trade line** with its limit, balance, overdue amount and **month-by-month repayment history**, and the full **enquiry (CAPS) history**. Dates are returned as **ISO YYYY-MM-DD**, amounts as numbers, and every classification field — account type, account status, ownership, asset classification, enquiry purpose — as a stable published value such as **credit_card** or **overdue_90** rather than a raw bureau code, so both the shape and the values of this response are guaranteed not to move. Built for lending decisions, underwriting, portfolio monitoring and risk assessment.

| | |
|---|---|
| **Endpoint** | `POST https://app.way2api.com/api/v1/credit-report-experian/fetch-report` |
| **Category** | Business |
| **Coverage** | India |
| **Authentication** | `Authorization: Bearer YOUR_API_KEY` |
| **API code** | `credit_report_experian` |
| **Full documentation** | [https://app.way2api.com/documentation/credit-report-experian](https://app.way2api.com/documentation/credit-report-experian) |

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
curl -X POST "https://app.way2api.com/api/v1/credit-report-experian/fetch-report" \
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
const ENDPOINT = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report";

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
ENDPOINT = "https://app.way2api.com/api/v1/credit-report-experian/fetch-report"

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
      "name": "ANANYA SHARMA",
      "mobile": "9876543210",
      "pan": "ABCDE1234F",
      "credit_score": 742,
      "report": {
        "number": "1786259702984",
        "date": "2026-08-09",
        "identity_match": "exact"
      },
      "applicant": {
        "first_name": "ANANYA",
        "middle_name": "",
        "last_name": "SHARMA",
        "gender": "female",
        "date_of_birth": "1990-04-17",
        "pan": "ABCDE1234F",
        "passport_number": "",
        "voter_id": "",
        "driving_licence": "",
        "mobile": "9876543210",
        "email": "ANANYA.SHARMA@EXAMPLE.COM",
        "address": {
          "line_1": "FLAT 12 SECOND FLOOR",
          "line_2": "GREEN VALLEY APARTMENTS",
          "line_3": "SECTOR 21 MAIN ROAD",
          "city": "NEW DELHI",
          "state_code": "07",
          "pincode": "110001",
          "country_code": "IB"
        }
      },
      "summary": {
        "total_accounts": 6,
        "active_accounts": 3,
        "closed_accounts": 3,
        "default_accounts": 0,
        "suit_filed_balance": 0,
        "outstanding": {
          "secured": 435757,
          "unsecured": 170579,
          "total": 606336,
          "secured_percent": 72,
          "unsecured_percent": 28
        }
      },
      "enquiry_summary": {
        "credit": {
          "last_7_days": 0,
          "last_30_days": 1,
          "last_90_days": 2,
          "last_180_days": 3
        },
        "non_credit": {
          "last_7_days": 0,
          "last_30_days": 0,
          "last_90_days": 0,
          "last_180_days": 0
        },
        "total": {
          "last_7_days": 0,
          "last_30_days": 1,
          "last_90_days": 2,
          "last_180_days": 3
        }
      },
      "accounts": [
        {
          "lender": "EXAMPLE BANK LTD",
          "lender_code": "PVT3000001",
          "account_number": "XXXXXXXXXXXXXXX0784",
          "account_type": "credit_card",
          "portfolio_type": "revolving",
          "ownership": "individual",
          "account_status": "active",
          "suit_filed_status": "no_suit_filed",
          "currency": "INR",
          "opened_on": "2025-12-09",
          "closed_on": "",
          "reported_on": "2026-06-30",
          "last_payment_on": "2026-06-07",
          "first_delinquency_on": "",
          "credit_limit": 135000,
          "sanctioned_amount": 39624,
          "current_balance": 21791,
          "amount_overdue": 0,
          "written_off_total": 0,
          "written_off_principal": 0,
          "settlement_amount": 0,
          "interest_rate": 0,
          "tenure_months": 0,
          "payment_history": "000000??????????????????????????????",
          "monthly_history": [
            {
              "year": 2026,
              "month": 6,
              "days_past_due": 0,
              "asset_classification": ""
            },
            {
              "year": 2026,
              "month": 5,
              "days_past_due": 0,
              "asset_classification": ""
            }
          ],
          "holders": [
            {
              "first_name": "ANANYA",
              "middle_name": "",
              "surname": "SHARMA",
              "alias": "",
              "gender": "female",
              "date_of_birth": "1990-04-17",
              "pan": "ABCDE1234F",
              "passport_number": "",
              "voter_id": ""
            }
          ],
          "addresses": [
            {
              "line_1": "FLAT 12 SECOND FLOOR",
              "line_2": "GREEN VALLEY APARTMENTS",
              "line_3": "SECTOR 21 MAIN ROAD",
              "city": "NEW DELHI",
              "state_code": "07",
              "pincode": "110001",
              "country_code": "IB"
            }
          ],
          "contacts": [
            {
              "landline": "",
              "mobile": "9876543210",
              "email": "ANANYA.SHARMA@EXAMPLE.COM"
            }
          ]
        }
      ],
      "enquiries": [
        {
          "enquired_on": "2026-05-14",
          "member": "EXAMPLE FINANCE LIMITED",
          "purpose": "personal_loan",
          "amount": 100000,
          "tenure_months": 12
        }
      ],
      "non_credit_enquiries": []
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

The full status and error-code reference for this API is at [https://app.way2api.com/documentation/credit-report-experian/errors](https://app.way2api.com/documentation/credit-report-experian/errors).

## Rate limits

Limits apply per API key, per service, on a one-minute sliding window. Exceeding them returns HTTP `429` with a `Retry-After` header. Current limits for this API are published at [https://app.way2api.com/documentation/credit-report-experian/rate-limits](https://app.way2api.com/documentation/credit-report-experian/rate-limits).

## More

- [Full documentation](https://app.way2api.com/documentation/credit-report-experian)
- [Request reference](https://app.way2api.com/documentation/credit-report-experian/request)
- [Response reference](https://app.way2api.com/documentation/credit-report-experian/response)
- [All Way2API documentation](https://app.way2api.com/documentation)

---

Part of [way2api-examples](../README.md). Slug: `credit-report-experian`
