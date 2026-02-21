## ADDED Requirements

### Requirement: Query payment status by ID
The system SHALL allow a caller to retrieve the current status of any payment by its internal ID. The response MUST include the payment ID, current state, amount, payer key, payee key, creation timestamp, and last-updated timestamp.

#### Scenario: Existing payment query
- **WHEN** a query is made with a valid payment ID
- **THEN** the system returns the full payment record including current state and timestamps

#### Scenario: Payment not found
- **WHEN** a query is made with an ID that does not exist
- **THEN** the system returns HTTP 404 with error code `PAYMENT_NOT_FOUND`

### Requirement: Payment state machine enforcement
The system SHALL enforce a strict state machine for all payment lifecycle transitions. Only the following transitions are valid:
- `CREATED → VALIDATING`
- `VALIDATING → SUBMITTED`
- `VALIDATING → FAILED`
- `SUBMITTED → SETTLED`
- `SUBMITTED → FAILED`
- `SETTLED → REVERSING`
- `REVERSING → REVERSED`

Any attempt to transition to an invalid state MUST be rejected.

#### Scenario: Valid state transition
- **WHEN** a payment transitions from VALIDATING to SUBMITTED
- **THEN** the new state is persisted and the last-updated timestamp is refreshed

#### Scenario: Invalid state transition
- **WHEN** code attempts to move a payment directly from CREATED to SETTLED
- **THEN** the system rejects the transition with an `INVALID_STATE_TRANSITION` error and the state is unchanged

#### Scenario: Terminal state is immutable
- **WHEN** any transition is attempted from a terminal state (SETTLED, FAILED, REVERSED)
- **THEN** the system rejects the transition with `INVALID_STATE_TRANSITION`

### Requirement: Payment history log
The system SHALL maintain an immutable append-only event log for every payment. Each state transition MUST be recorded as a new event entry containing the previous state, new state, timestamp, and optional reason code.

#### Scenario: Event recorded on transition
- **WHEN** a payment transitions from one state to another
- **THEN** a new event entry is appended to the log with the correct previous state, new state, and timestamp

#### Scenario: Event log is read-only
- **WHEN** any code attempts to delete or modify an existing event entry
- **THEN** the operation is rejected and the log remains unchanged

### Requirement: Filter payments by state
The system SHALL allow callers to list payments filtered by state, with results paginated (maximum 100 per page, default 20). Results MUST be ordered by creation timestamp descending.

#### Scenario: Filter by SETTLED state
- **WHEN** a list request is made with filter `state=SETTLED`
- **THEN** only payments in SETTLED state are returned, ordered newest-first

#### Scenario: Pagination — next page
- **WHEN** a list request includes a `cursor` token from a previous response
- **THEN** the next page of results is returned starting after the cursor position

#### Scenario: Empty result set
- **WHEN** a filter matches no payments
- **THEN** the system returns HTTP 200 with an empty items array and no cursor
