# Way2API Examples

Official, copy-paste-ready integration examples for the [Way2API](https://www.way2api.com) platform — cURL, Node.js and Python for every live API.

Way2API is a global developer API platform offering domain WHOIS and availability
checks, DNS, SSL and IP threat intelligence, email validation, SMS OTP delivery, and
identity, KYC and business compliance verification APIs.

- **Website:** https://www.way2api.com
- **Documentation:** https://app.way2api.com/documentation
- **Get an API key:** https://app.way2api.com/register

Every example in this repository is generated from the live Way2API documentation
catalogue (`GET https://app.way2api.com/api/v1/docs`), so endpoints, parameters and response shapes
match what the platform publishes. Catalogue snapshot: `2026-08-21T01:11:41+00:00`.

## Contents

- [Quick start](#quick-start)
- [Authentication](#authentication)
- [Response format](#response-format)
- [Error handling](#error-handling)
- [Rate limits](#rate-limits)
- [Available APIs](#available-apis)
- [Public catalogue endpoints](#public-catalogue-endpoints)
- [Repository layout](#repository-layout)
- [Getting an API key](#getting-an-api-key)
- [Support](#support)

## Quick start

All APIs are `POST` requests against the base URL `https://app.way2api.com/api/v1`, take a JSON body,
and return JSON.

```bash
curl -X POST "https://app.way2api.com/api/v1/email/validate" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"email": "emma.thompson@example.com"}'
```

```javascript
// Node.js 18+ — built-in fetch, no dependencies
const response = await fetch("https://app.way2api.com/api/v1/email/validate", {
  method: "POST",
  headers: {
    Authorization: `Bearer ${process.env.WAY2API_KEY}`,
    "Content-Type": "application/json",
  },
  body: JSON.stringify({ email: "emma.thompson@example.com" }),
});

const body = await response.json();
console.log(body.data.result);
```

```python
# pip install requests
import os, requests

response = requests.post(
    "https://app.way2api.com/api/v1/email/validate",
    headers={"Authorization": f"Bearer {os.environ['WAY2API_KEY']}"},
    json={"email": "emma.thompson@example.com"},
    timeout=30,
)
print(response.json()["data"]["result"])
```

Each API folder contains a runnable `curl.txt`, `node.js` and `python.py` plus a
README with that API's parameters and response.

## Authentication

Way2API authenticates with an API key sent on every request. Two header forms are
accepted and are equivalent:

```http
Authorization: Bearer YOUR_API_KEY
```

```http
X-API-Key: YOUR_API_KEY
```

A missing key returns HTTP `401`; an invalid, expired or IP-restricted key also
returns `401`.

> **Never commit your API key.** The examples read it from the `WAY2API_KEY`
> environment variable and fall back to the literal placeholder `YOUR_API_KEY`.

## Response format

Every endpoint returns the same envelope:

```json
{
  "success": true,
  "message": "",
  "data": {
    "order_id": "W2A1739512345abcdef01",
    "result": { }
  }
}
```

- `success` — boolean outcome flag; branch on this, not on `message` text.
- `message` — human-readable text, empty on a clean success.
- `data.order_id` — the transaction reference. Log it; quote it in support requests.
- `data.result` — the API-specific payload, documented per API.

## Error handling

A failure returns `success: false` and a `message`, with the HTTP status carrying the
category. Whether a call is **charged** depends on where it failed: input rejected
before the lookup is not charged, while a lookup that ran and returned a negative
result is.

| Status | Meaning | Charged |
|---|---|---|
| `200` | Result returned | Yes |
| `202` | Accepted and processing, or the provider did not respond in time | Yes |
| `400` | Request failed or the provider returned an invalid response | No |
| `401` | Missing or invalid API key | No |
| `402` | Insufficient balance — recharge required | No |
| `403` | No access to this API service | No |
| `404` | Unknown endpoint | No |
| `422` | Input validation failed (not charged), or the lookup ran and returned a negative result (charged) | Depends |
| `429` | Rate limit exceeded | No |
| `500` | Unexpected error on the Way2API side | No |
| `503` | Provider temporarily unavailable | No |

On `202`, the result is not final — keep the `order_id` and follow up rather than
retrying the call.

Each API publishes its own status and error-code reference at
`https://app.way2api.com/documentation/{api-slug}/errors`.

## Rate limits

Rate limits apply **per API key, per service**, on a one-minute sliding window, with a
separate daily cap. Exceeding a limit returns HTTP `429` with a `Retry-After` header —
wait for that interval before retrying.

Limits differ per API and are published on each API's documentation page at
`https://app.way2api.com/documentation/{api-slug}/rate-limits`. They are deliberately not duplicated here so
this repository cannot fall out of step with the platform.

## Available APIs

42 live APIs across 8 categories. Folder names match the
documentation slug, so each folder maps 1:1 to its documentation page.

### Business

| API | Endpoint | Example | Docs |
|---|---|---|---|
| Credit Report Equifax | `/api/v1/credit-report/fetch` | [`credit-report-equifax/`](./credit-report-equifax/) | [Docs](https://app.way2api.com/documentation/credit-report-equifax) |
| Credit Report Equifax PDF | `/api/v1/credit-report/pdf` | [`credit-report-equifax-pdf/`](./credit-report-equifax-pdf/) | [Docs](https://app.way2api.com/documentation/credit-report-equifax-pdf) |
| Credit Report Experian | `/api/v1/credit-report-experian/fetch-report` | [`credit-report-experian/`](./credit-report-experian/) | [Docs](https://app.way2api.com/documentation/credit-report-experian) |
| Credit Report Experian PDF | `/api/v1/credit-report-experian/fetch-report-pdf` | [`credit-report-experian-pdf/`](./credit-report-experian-pdf/) | [Docs](https://app.way2api.com/documentation/credit-report-experian-pdf) |
| GST Filing Details | `/api/v1/gst/filing-details` | [`gst-filing-details/`](./gst-filing-details/) | [Docs](https://app.way2api.com/documentation/gst-filing-details) |
| GST Filing Frequency | `/api/v1/gst/filing-frequency` | [`gst-filing-frequency/`](./gst-filing-frequency/) | [Docs](https://app.way2api.com/documentation/gst-filing-frequency) |
| GST Verification | `/api/v1/gst/verify` | [`gst/`](./gst/) | [Docs](https://app.way2api.com/documentation/gst) |
| HSN Code Lookup | `/api/v1/gst/hsn-code` | [`hsn-code-lookup/`](./hsn-code-lookup/) | [Docs](https://app.way2api.com/documentation/hsn-code-lookup) |
| TAN Adv | `/api/v1/tan/fetch_adv` | [`tan-advanced/`](./tan-advanced/) | [Docs](https://app.way2api.com/documentation/tan-advanced) |

### Domain Intelligence and Analysis

| API | Endpoint | Example | Docs |
|---|---|---|---|
| DNS Lookup | `/api/v1/domain/dns` | [`dns-lookup/`](./dns-lookup/) | [Docs](https://app.way2api.com/documentation/dns-lookup) |
| Domain Availability | `/api/v1/domain/availability` | [`domain-availability-check-v2/`](./domain-availability-check-v2/) | [Docs](https://app.way2api.com/documentation/domain-availability-check-v2) |
| Domain Availability Check | `/api/v1/domain/check` | [`domain-availability-check-v1/`](./domain-availability-check-v1/) | [Docs](https://app.way2api.com/documentation/domain-availability-check-v1) |
| Domain WHOIS Lookup | `/api/v1/domain/whois` | [`domain-whois-lookup-v2/`](./domain-whois-lookup-v2/) | [Docs](https://app.way2api.com/documentation/domain-whois-lookup-v2) |
| Domain Whois | `/api/v1/domain/whois_live` | [`domain-whois-lookup-v1/`](./domain-whois-lookup-v1/) | [Docs](https://app.way2api.com/documentation/domain-whois-lookup-v1) |
| SSL Certificate Lookup | `/api/v1/domain/ssl` | [`ssl-lookup/`](./ssl-lookup/) | [Docs](https://app.way2api.com/documentation/ssl-lookup) |
| Subdomains Lookup | `/api/v1/domain/subdomains` | [`subdomain-lookup/`](./subdomain-lookup/) | [Docs](https://app.way2api.com/documentation/subdomain-lookup) |

### Financial

| API | Endpoint | Example | Docs |
|---|---|---|---|
| Currency Exchange Rates | `/api/v1/currency/rates` | [`currency-rates/`](./currency-rates/) | [Docs](https://app.way2api.com/documentation/currency-rates) |

### IP and Network Intelligence

| API | Endpoint | Example | Docs |
|---|---|---|---|
| Bulk IP Threat Intelligence | `/api/v1/ip/threat/bulk` | [`ip-threat-bulk/`](./ip-threat-bulk/) | [Docs](https://app.way2api.com/documentation/ip-threat-bulk) |
| IP Check | `/api/v1/ip/check` | [`ip-check-v1/`](./ip-check-v1/) | [Docs](https://app.way2api.com/documentation/ip-check-v1) |
| IP Check Advance | `/api/v1/ip/check_advance` | [`ip-check-advance/`](./ip-check-advance/) | [Docs](https://app.way2api.com/documentation/ip-check-advance) |
| IP Geolocation Enrichment | `/api/v1/ip/geolocation/full` | [`ip-geolocation-full/`](./ip-geolocation-full/) | [Docs](https://app.way2api.com/documentation/ip-geolocation-full) |
| IP Geolocation Lookup | `/api/v1/ip/geolocation` | [`ip-geolocation/`](./ip-geolocation/) | [Docs](https://app.way2api.com/documentation/ip-geolocation) |
| IP Threat Intelligence | `/api/v1/ip/threat` | [`ip-threat/`](./ip-threat/) | [Docs](https://app.way2api.com/documentation/ip-threat) |
| IP WHOIS Lookup | `/api/v1/ip/whois` | [`ip-whois/`](./ip-whois/) | [Docs](https://app.way2api.com/documentation/ip-whois) |

### Identity and Security

| API | Endpoint | Example | Docs |
|---|---|---|---|
| Bank Account Validation | `/api/v1/bank/account_validation` | [`bank-account-validation/`](./bank-account-validation/) | [Docs](https://app.way2api.com/documentation/bank-account-validation) |
| Driving License Verify | `/api/v1/driving-license/verify` | [`driving-license/`](./driving-license/) | [Docs](https://app.way2api.com/documentation/driving-license) |
| Mobile Prefill | `/api/v1/mobile/prefill` | [`mobile-prefill/`](./mobile-prefill/) | [Docs](https://app.way2api.com/documentation/mobile-prefill) |
| PAN Verification | `/api/v1/pan/verify` | [`pan/`](./pan/) | [Docs](https://app.way2api.com/documentation/pan) |
| VPA Validation | `/api/v1/upi/vpa_validation` | [`vpa-validation/`](./vpa-validation/) | [Docs](https://app.way2api.com/documentation/vpa-validation) |
| Voter ID Verification | `/api/v1/voter-id/verify` | [`voter-id/`](./voter-id/) | [Docs](https://app.way2api.com/documentation/voter-id) |

### Telecom

| API | Endpoint | Example | Docs |
|---|---|---|---|
| DTH Advance Information | `/api/v1/dth/info` | [`dth-info/`](./dth-info/) | [Docs](https://app.way2api.com/documentation/dth-info) |
| DTH Operator Check | `/api/v1/dth/operator-check` | [`dth-operator-check/`](./dth-operator-check/) | [Docs](https://app.way2api.com/documentation/dth-operator-check) |
| Last Mobile Recharge Status | `/api/v1/mobile/last-recharge` | [`mobile-last-recharge/`](./mobile-last-recharge/) | [Docs](https://app.way2api.com/documentation/mobile-last-recharge) |
| Mobile R-Offer Fetch | `/api/v1/mobile/r-offer` | [`mobile-r-offer/`](./mobile-r-offer/) | [Docs](https://app.way2api.com/documentation/mobile-r-offer) |
| Operator and Circle Check | `/api/v1/operator-circle/check` | [`operator-circle/`](./operator-circle/) | [Docs](https://app.way2api.com/documentation/operator-circle) |
| Send OTP/PIN by SMS | `/api/v1/aws/send_otp-pin` | [`send-otp-pin-sms/`](./send-otp-pin-sms/) | [Docs](https://app.way2api.com/documentation/send-otp-pin-sms) |

### Utilities

| API | Endpoint | Example | Docs |
|---|---|---|---|
| Bulk Email Validation | `/api/v1/email/validate/bulk` | [`email-validation-bulk/`](./email-validation-bulk/) | [Docs](https://app.way2api.com/documentation/email-validation-bulk) |
| Email Validation | `/api/v1/email/validate` | [`email-validation/`](./email-validation/) | [Docs](https://app.way2api.com/documentation/email-validation) |

### Verification

| API | Endpoint | Example | Docs |
|---|---|---|---|
| Aadhaar PAN Link Check | `/api/v1/aadhaar/aadhaar_pan_link_check` | [`aadhaar-pan-link-check/`](./aadhaar-pan-link-check/) | [Docs](https://app.way2api.com/documentation/aadhaar-pan-link-check) |
| Vehicle RC PDF | `/api/v1/rc/pdf` | [`vehicle-rc-pdf/`](./vehicle-rc-pdf/) | [Docs](https://app.way2api.com/documentation/vehicle-rc-pdf) |
| Vehicle RC Text and PDF | `/api/v1/rc/text-pdf` | [`vehicle-rc-text-pdf/`](./vehicle-rc-text-pdf/) | [Docs](https://app.way2api.com/documentation/vehicle-rc-text-pdf) |
| Vehicle RC Verification | `/api/v1/rc/verify` | [`vehicle-rc/`](./vehicle-rc/) | [Docs](https://app.way2api.com/documentation/vehicle-rc) |

APIs marked for a single country in the documentation are India-specific (PAN, GST, Aadhaar, RC, Voter ID, DTH and telecom lookups); the domain, IP, email and currency APIs are global.

## Public catalogue endpoints

Way2API publishes read-only catalogue endpoints that need **no API key**. They are
useful for building your own integration list or keeping a client in sync:

| Endpoint | Returns |
|---|---|
| `GET https://app.way2api.com/api/v1/docs` | Full documentation catalogue — every live API, its parameters and sample responses |
| `GET https://app.way2api.com/api/v1/pricing` | Per-API pricing |
| `GET https://app.way2api.com/api/v1/trial-credits` | Per-API trial credit allowance |
| `GET https://app.way2api.com/api/v1/commissions` | Affiliate commission rates |

```bash
curl "https://app.way2api.com/api/v1/docs"
```

Values on the pricing, trial-credit and commission feeds may be returned as the
string `"require_login"` instead of a number when the platform gates that figure, so
check the type before formatting.

## Repository layout

```
way2api-examples/
├── README.md
├── LICENSE
├── .gitignore
├── email-validation/
│   ├── README.md      # parameters, responses, error handling
│   ├── curl.txt       # runnable cURL request
│   ├── node.js        # Node.js 18+, no dependencies
│   └── python.py      # Python 3, requests
├── domain-whois-lookup-v2/
│   └── ...
└── ...
```

### Running an example

```bash
export WAY2API_KEY=your_actual_key

cd email-validation
node node.js
python python.py

# for cURL, replace YOUR_API_KEY in the file, or:
sed "s/YOUR_API_KEY/$WAY2API_KEY/" curl.txt | bash
```

Node examples need **Node.js 18 or newer** (for built-in `fetch`) and have no
dependencies. Python examples need **Python 3** and `requests`:

```bash
pip install requests
```

These are **examples, not SDKs** — copy them into your project and adapt them.
There is no package to install.

## Getting an API key

1. Create an account at [https://app.way2api.com/register](https://app.way2api.com/register).
2. Confirm your email address — the account, API key and trial credits are created
   when you open the confirmation link.
3. Copy your key from the dashboard.
4. Export it as `WAY2API_KEY` so the examples pick it up.

Way2API states that a free tier is included and no credit card is required to start.
Trial credits are allocated per API; the current allowance for each API is published
live at `GET https://app.way2api.com/api/v1/trial-credits`. See [https://www.way2api.com](https://www.way2api.com) and
[https://www.way2api.com/page/pricing](https://www.way2api.com/page/pricing) for current plans and pricing.

## Support

- **Documentation:** https://app.way2api.com/documentation
- **Email:** support@way2api.com
- **WhatsApp:** [+91 70703 00613](https://wa.me/917070300613)
- **Contact:** [https://www.way2api.com/page/contact_us](https://www.way2api.com/page/contact_us)

For questions about these examples, open an issue on this repository. For questions
about your account, billing or a specific transaction, contact Way2API support and
quote the `order_id` from the response.

## Contributing

Issues and pull requests are welcome — corrections, clarifications and additional
languages. Please keep examples dependency-light and never commit a real API key,
token or personal data.

## License

Released under the [MIT License](LICENSE). The examples are free to copy and adapt.
**No API credentials are included in this repository** — every key is the placeholder
`YOUR_API_KEY`. Use of the Way2API service itself is governed by Way2API's own terms.
