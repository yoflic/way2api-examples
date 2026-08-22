# Contributing

Thanks for helping improve the Way2API examples. Corrections, clearer wording
and additional languages are all welcome.

## Before you open a pull request

**The API folders are generated, not hand-written.** Every `README.md`,
`curl.txt`, `node.js`, `python.py`, `php.php`, `java.java`, `csharp.cs`,
`go.go`, `ruby.rb`, `kotlin.kt` and `swift.swift` is produced from the live
Way2API documentation catalogue (`GET https://app.way2api.com/api/v1/docs`), so
an edit made directly to one of those files is overwritten the next time the
repository is regenerated.

That means:

- **A wrong parameter, endpoint or response** is a documentation bug, not a file
  bug. Open an issue naming the API and what is wrong — it gets fixed at the
  source and every language is corrected at once.
- **A bug in how a language example is written** (a broken idiom, a missing
  error check, a runtime that is too new) is worth an issue or a PR describing
  the fix. The change is applied to the generator so all 42 APIs get it, not
  just the one you noticed.
- **Prose in this file, `SECURITY.md` or the `LICENSE`** is hand-maintained and
  can be edited directly.

## Adding a language

Open an issue first and say which language and runtime you need. A language is
added to the generator once and then appears in all 42 API folders, so it needs
to work for every endpoint rather than just one.

A new language example should:

- use the **standard library** where that is realistic, and name any dependency
  plus the minimum runtime version in the file's header comment
- read the key from the `WAY2API_KEY` environment variable, falling back to the
  literal `YOUR_API_KEY`
- send `Authorization: Bearer <key>` and `Content-Type: application/json`
- check **both** the HTTP status and the `success` flag, and print `order_id` on
  failure
- stay short enough to read in one screen

## Reporting a problem

Useful issues include the API slug (for example `email-validation`), the
language, what you expected and what happened. If a request failed, include the
`order_id` from the response — it identifies the transaction without exposing
anything sensitive.

## Never include secrets or personal data

Do not paste API keys, real PAN/Aadhaar/GSTIN/account numbers, real phone
numbers, or full responses from live lookups into an issue or a PR. See
[SECURITY.md](SECURITY.md).

## Licence

By contributing you agree that your contribution is licensed under the
[MIT Licence](LICENSE) that covers this repository.
