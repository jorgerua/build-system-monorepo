## ADDED Requirements

### Requirement: Webhook registration
The system SHALL allow callers to register a webhook endpoint URL to receive payment event notifications. Each registration MUST include a target URL and a list of event types to subscribe to. The system MUST validate that the URL is reachable (HTTP 200 response) before confirming registration.

#### Scenario: Successful webhook registration
- **WHEN** a valid URL is submitted and returns HTTP 200 on a probe request
- **THEN** the webhook is registered and its ID is returned to the caller

#### Scenario: Unreachable URL
- **WHEN** a URL is submitted but the probe request fails or times out
- **THEN** the system returns HTTP 422 with error code `WEBHOOK_URL_UNREACHABLE`

#### Scenario: Invalid URL format
- **WHEN** a registration is submitted with a non-HTTPS URL or a malformed URL
- **THEN** the system returns HTTP 422 with error code `INVALID_WEBHOOK_URL`

### Requirement: Event delivery on payment state change
The system SHALL deliver a webhook notification to all registered subscribers whenever a payment transitions to a new state. Delivery MUST use HTTP POST with a JSON body containing the event type, payment ID, new state, and event timestamp. Delivery MUST use HTTPS only.

#### Scenario: Notification on SETTLED transition
- **WHEN** a payment transitions to SETTLED
- **THEN** all subscribers of the `payment.settled` event type receive a POST request within 5 seconds

#### Scenario: Notification on FAILED transition
- **WHEN** a payment transitions to FAILED
- **THEN** all subscribers of the `payment.failed` event type receive a POST request within 5 seconds

#### Scenario: Subscriber not interested in event type
- **WHEN** a payment event fires for a type the subscriber did not register for
- **THEN** no notification is sent to that subscriber

### Requirement: Delivery retry with exponential backoff
The system SHALL retry failed webhook deliveries up to 5 times using exponential backoff starting at 1 second (1s, 2s, 4s, 8s, 16s). A delivery is considered failed if the subscriber returns a non-2xx status or the request times out after 5 seconds. After 5 failed attempts the delivery MUST be marked as permanently failed and no further retries attempted.

#### Scenario: Transient failure then success
- **WHEN** the first delivery attempt fails and the second succeeds
- **THEN** the event is marked as delivered and no further retries are scheduled

#### Scenario: All retries exhausted
- **WHEN** all 5 delivery attempts fail
- **THEN** the delivery is marked as `PERMANENTLY_FAILED` and an internal alert is logged

#### Scenario: Retry backoff timing
- **WHEN** the first attempt fails
- **THEN** the second attempt is scheduled no sooner than 1 second later, and subsequent retries double the delay

### Requirement: Delivery audit log
The system SHALL maintain an audit log of every delivery attempt for each webhook event, recording the attempt number, timestamp, HTTP status code returned (or timeout), and final outcome. The audit log MUST be retained for at least 7 days.

#### Scenario: Successful delivery logged
- **WHEN** a webhook is delivered successfully
- **THEN** an audit entry is created with attempt number 1, the response status code, and outcome `DELIVERED`

#### Scenario: Failed attempt logged
- **WHEN** a delivery attempt fails
- **THEN** an audit entry is created with the attempt number, the error or status code, and outcome `FAILED`

#### Scenario: Log retention
- **WHEN** an audit entry is 8 days old
- **THEN** it is eligible for deletion by the retention cleanup job
