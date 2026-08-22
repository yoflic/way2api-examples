# Security Policy

## Never post a real API key

This repository is public. Everything in an issue, a pull request, a commit or a
screenshot is visible to anyone and is preserved in the Git history even after
you delete it.

Before you open an issue or a PR, check that you have removed:

- your Way2API key (`Authorization: Bearer …` or `X-API-Key: …`)
- real PAN, Aadhaar, GSTIN, voter ID, driving licence, vehicle RC or bank
  account numbers
- real phone numbers, email addresses and names
- full API responses from live lookups — they usually contain personal data

Use the placeholders the examples already use: `YOUR_API_KEY`, `example.com`,
and the sample values in each API's `curl.txt`.

## If you have already exposed a key

Rotate it first, then clean up:

1. Sign in at <https://app.way2api.com> and revoke the exposed key.
2. Generate a replacement.
3. Email <support@way2api.com> if you believe the key was used before you
   revoked it, quoting any `order_id` values you do not recognise.

Deleting the comment or force-pushing over the commit is **not** sufficient on
its own — assume any key that reached a public page is compromised.

## Reporting a vulnerability

**In these examples** — insecure patterns, a snippet that leaks a key, a
dependency concern: open a
[GitHub issue](https://github.com/yoflic/way2api-examples/issues). These files
are documentation, so there is nothing confidential to protect.

**In the Way2API platform or API** — do not open a public issue. Email
<support@way2api.com> with:

- what you found and where
- the steps to reproduce it
- any `order_id` involved
- how you would like to be credited, if you would

Please give Way2API a reasonable opportunity to fix the issue before disclosing
it publicly, and do not access, modify or retain data belonging to anyone else
while investigating.

## Handling personal data

Most Way2API endpoints return personal data. If you are building on these
examples, see
[Responsible use and data protection](README.md#responsible-use-and-data-protection)
in the README: hold consent, collect the minimum, keep personal data out of
logs, and call the API from your backend rather than from a browser or a mobile
app.
