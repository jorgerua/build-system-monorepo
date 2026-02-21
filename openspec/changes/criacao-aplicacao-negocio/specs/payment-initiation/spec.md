## ADDED Requirements

### Requirement: Create outbound payment order
The system SHALL allow a caller to create an outbound PIX payment order by providing payer key, payee key, amount, and an idempotency key. The system MUST validate all fields before persisting the order and MUST reject duplicate idempotency keys.

#### Scenario: Successful payment order creation
- **WHEN** a valid request is submitted with a unique idempotency key
- **THEN** the system creates a payment record in CREATED state and returns its ID and current state

#### Scenario: Duplicate idempotency key
- **WHEN** a request is submitted with an idempotency key that already exists
- **THEN** the system returns the original payment record without creating a new one and responds with HTTP 200

#### Scenario: Invalid amount
- **WHEN** a request is submitted with an amount less than or equal to zero
- **THEN** the system rejects the request with HTTP 422 and an error code of `INVALID_AMOUNT`

#### Scenario: Missing required field
- **WHEN** a request is submitted without a payee key or payer key
- **THEN** the system rejects the request with HTTP 422 and an error code of `MISSING_FIELD`

### Requirement: Submit payment to SPI
The system SHALL automatically submit a payment order to the SPI rails after creation. The submission MUST transition the payment from CREATED → VALIDATING → SUBMITTED or FAILED. The system MUST not allow double-submission of the same payment.

#### Scenario: Successful SPI submission
- **WHEN** a payment in CREATED state is processed
- **THEN** the payment transitions to VALIDATING then SUBMITTED, and the SPI end-to-end ID is stored

#### Scenario: SPI rejection
- **WHEN** the SPI rejects the payment (e.g., invalid account)
- **THEN** the payment transitions to FAILED with the BCB rejection reason code stored

#### Scenario: Submission attempted on non-CREATED payment
- **WHEN** a submission is attempted on a payment already in SUBMITTED or terminal state
- **THEN** the system rejects the operation with an `INVALID_STATE_TRANSITION` error

### Requirement: Payment amount limits
The system SHALL enforce minimum and maximum amount limits per payment. The minimum amount is R$ 0.01. The maximum amount is R$ 999,999,999.99.

#### Scenario: Amount at minimum boundary
- **WHEN** a payment is created with amount R$ 0.01
- **THEN** the payment is accepted and created successfully

#### Scenario: Amount exceeds maximum
- **WHEN** a payment is created with amount above R$ 999,999,999.99
- **THEN** the system rejects the request with HTTP 422 and error code `AMOUNT_EXCEEDS_LIMIT`
