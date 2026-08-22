// Way2API - Domain Availability Check
// Docs: https://app.way2api.com/documentation/domain-availability-check-v1
//
// Go 1.18+ standard library only.
// Run:  WAY2API_KEY=your_key go run go.go

package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"time"
)

const endpoint = "https://app.way2api.com/api/v1/domain/check"

func main() {
	apiKey := os.Getenv("WAY2API_KEY")
	if apiKey == "" {
		apiKey = "YOUR_API_KEY"
	}

	payload := map[string]interface{}{
		"domain": "example.com",
	}

	body, err := json.Marshal(payload)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Could not encode payload: %v\n", err)
		os.Exit(1)
	}

	req, err := http.NewRequest(http.MethodPost, endpoint, bytes.NewReader(body))
	if err != nil {
		fmt.Fprintf(os.Stderr, "Could not build request: %v\n", err)
		os.Exit(1)
	}
	req.Header.Set("Authorization", "Bearer "+apiKey)
	req.Header.Set("Content-Type", "application/json")

	client := &http.Client{Timeout: 30 * time.Second}
	resp, err := client.Do(req)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Network error: %v\n", err)
		os.Exit(1)
	}
	defer resp.Body.Close()

	raw, _ := io.ReadAll(resp.Body)

	// Every Way2API response is {"success": ..., "message": ..., "data": ...}.
	// Check both the HTTP status and the success flag.
	var parsed struct {
		Success bool            `json:"success"`
		Message string          `json:"message"`
		Data    struct {
			OrderID string          `json:"order_id"`
			Result  json.RawMessage `json:"result"`
		} `json:"data"`
	}
	if err := json.Unmarshal(raw, &parsed); err != nil {
		fmt.Fprintf(os.Stderr, "Non-JSON response (HTTP %d)\n", resp.StatusCode)
		os.Exit(1)
	}

	if resp.StatusCode >= 400 || !parsed.Success {
		fmt.Fprintf(os.Stderr, "Request failed (HTTP %d): %s\n", resp.StatusCode, parsed.Message)
		if parsed.Data.OrderID != "" {
			fmt.Fprintf(os.Stderr, "Order ID (quote this in support requests): %s\n", parsed.Data.OrderID)
		}
		os.Exit(1)
	}

	var pretty bytes.Buffer
	json.Indent(&pretty, parsed.Data.Result, "", "  ")
	fmt.Println(pretty.String())
}
