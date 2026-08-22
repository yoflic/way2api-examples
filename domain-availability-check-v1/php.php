<?php

/**
 * Way2API - Domain Availability Check
 * Docs: https://app.way2api.com/documentation/domain-availability-check-v1
 *
 * PHP 8.0+ with the cURL extension. No Composer packages required.
 * Run:  WAY2API_KEY=your_key php php.php
 */

declare(strict_types=1);

$apiKey   = getenv('WAY2API_KEY') ?: 'YOUR_API_KEY';
$endpoint = 'https://app.way2api.com/api/v1/domain/check';

$payload = [
    'domain' => 'example.com'
];

$ch = curl_init($endpoint);
curl_setopt_array($ch, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST           => true,
    CURLOPT_HTTPHEADER     => [
        'Authorization: Bearer ' . $apiKey,
        'Content-Type: application/json',
    ],
    CURLOPT_POSTFIELDS     => json_encode($payload),
    CURLOPT_TIMEOUT        => 30,
]);

$raw    = curl_exec($ch);
$status = (int) curl_getinfo($ch, CURLINFO_RESPONSE_CODE);
$netErr = curl_error($ch);
curl_close($ch);

if ($raw === false) {
    fwrite(STDERR, "Network error: {$netErr}\n");
    exit(1);
}

$body = json_decode((string) $raw, true);

// Every Way2API response is ['success' => ..., 'message' => ..., 'data' => ...].
// Check both the HTTP status and the success flag.
if ($status >= 400 || empty($body['success'])) {
    fwrite(STDERR, "Request failed (HTTP {$status}): " . ($body['message'] ?? '') . "\n");
    if (!empty($body['data']['order_id'])) {
        fwrite(STDERR, "Order ID (quote this in support requests): {$body['data']['order_id']}\n");
    }
    exit(1);
}

echo json_encode($body['data']['result'], JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES), "\n";
