## ADDED Requirements

### Requirement: Accept inbound payment credit
The system SHALL receive and persist inbound PIX credit notifications from the SPI. Each inbound payment MUST be identified by a unique SPI end-to-end ID. The system MUST be idempotent — processing the same notification twice MUST NOT create duplicate records.

#### Scenario: First-time credit notification
- **WHEN** an inbound credit notification arrives with a new SPI end-to-end ID
- **THEN** the system creates a payment record in SETTLED state and returns HTTP 200

#### Scenario: Duplicate credit notification
- **WHEN** an inbound credit notification arrives with an already-known SPI end-to-end ID
- **THEN** the system returns HTTP 200 without creating a duplicate record

#### Scenario: Malformed notification payload
- **WHEN** a notification arrives with missing required fields (e.g., no amount or no payer key)
- **THEN** the system returns HTTP 422 and does not persist any record

### Requirement: Validate payee key ownership
The system SHALL verify that the payee PIX key in the inbound notification belongs to the receiving account before crediting it. Payments directed to an unregistered key MUST be rejected.

#### Scenario: Valid payee key
- **WHEN** an inbound payment arrives and the payee key is registered in key-management
- **THEN** the credit is accepted and the record is persisted

#### Scenario: Unknown payee key
- **WHEN** an inbound payment arrives and the payee key is not registered
- **THEN** the system rejects the notification with HTTP 422 and error code `UNKNOWN_PAYEE_KEY`

### Requirement: Credit confirmation response
The system SHALL respond to the SPI confirmation request within the BCB-mandated timeout of 10 seconds. If the system cannot confirm within 10 seconds, it MUST return an error response to prevent settlement.

#### Scenario: Confirmation within timeout
- **WHEN** the credit is validated and persisted within 10 seconds of receiving the notification
- **THEN** the system sends a confirmation response to SPI

#### Scenario: Processing exceeds timeout
- **WHEN** internal processing takes longer than 10 seconds
- **THEN** the system returns an error to SPI indicating it cannot confirm, and marks the internal record as FAILED
