# Way2API - Mobile R-Offer Fetch
# Docs: https://app.way2api.com/documentation/mobile-r-offer
#
# Ruby 2.6+ standard library (net/http, json). No gems required.
# Run:  WAY2API_KEY=your_key ruby ruby.rb

require 'json'
require 'net/http'
require 'uri'

API_KEY  = ENV.fetch('WAY2API_KEY', 'YOUR_API_KEY')
ENDPOINT = 'https://app.way2api.com/api/v1/mobile/r-offer'.freeze

payload = {
  'mobile_number' => '9876543210',
  'operator' => 'airtel'
}

uri = URI(ENDPOINT)

request = Net::HTTP::Post.new(uri)
request['Authorization'] = "Bearer #{API_KEY}"
request['Content-Type']  = 'application/json'
request.body = JSON.generate(payload)

response = Net::HTTP.start(uri.hostname, uri.port, use_ssl: uri.scheme == 'https', read_timeout: 30) do |http|
  http.request(request)
end

begin
  body = JSON.parse(response.body)
rescue JSON::ParserError
  warn "Non-JSON response (HTTP #{response.code})"
  exit 1
end

# Every Way2API response is {"success" => ..., "message" => ..., "data" => ...}.
# Check both the HTTP status and the success flag.
unless response.is_a?(Net::HTTPSuccess) && body['success']
  warn "Request failed (HTTP #{response.code}): #{body['message']}"
  order_id = body.dig('data', 'order_id')
  warn "Order ID (quote this in support requests): #{order_id}" if order_id
  exit 1
end

puts JSON.pretty_generate(body['data']['result'])
