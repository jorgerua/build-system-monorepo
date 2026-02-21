## ADDED Requirements

### Requirement: Register a PIX key
The system SHALL allow an account holder to register a PIX key of type CPF, CNPJ, e-mail, phone (E.164 with +55 prefix), or random UUID. Each key MUST be unique across the system. The key MUST be validated against its type format before registration. The system MUST propagate the registration to the DICT stub.

#### Scenario: Successful CPF key registration
- **WHEN** a valid, unregistered CPF is submitted for registration
- **THEN** the key is persisted, registered in DICT, and returned with its creation timestamp

#### Scenario: Duplicate key registration
- **WHEN** a key that is already registered is submitted again
- **THEN** the system returns HTTP 409 with error code `KEY_ALREADY_EXISTS`

#### Scenario: Invalid key format
- **WHEN** a key is submitted with a format that does not match its declared type (e.g., letters in a CPF field)
- **THEN** the system returns HTTP 422 with error code `INVALID_KEY_FORMAT`

#### Scenario: Random UUID key generation
- **WHEN** a registration request specifies type `random`
- **THEN** the system generates a UUID v4, registers it, and returns the generated value to the caller

### Requirement: Look up a PIX key
The system SHALL allow any caller to look up an active PIX key and retrieve the associated account information. Lookups MUST be proxied to the DICT stub and the result cached locally for 60 seconds.

#### Scenario: Successful key lookup
- **WHEN** a lookup is performed for a registered key
- **THEN** the system returns the associated account holder name, account branch, and account number

#### Scenario: Key not found
- **WHEN** a lookup is performed for a key that does not exist in DICT
- **THEN** the system returns HTTP 404 with error code `KEY_NOT_FOUND`

#### Scenario: Cache hit
- **WHEN** the same key is looked up twice within 60 seconds
- **THEN** the second lookup is served from cache without a DICT call

### Requirement: Delete a PIX key
The system SHALL allow an account holder to delete one of their registered PIX keys. Deletion MUST be propagated to DICT. After deletion, the key SHALL NOT be resolvable by any lookup.

#### Scenario: Successful deletion
- **WHEN** the owner of a key requests its deletion
- **THEN** the key is removed from local storage and DICT, and HTTP 204 is returned

#### Scenario: Deletion of unowned key
- **WHEN** a caller attempts to delete a key that belongs to a different account
- **THEN** the system returns HTTP 403 with error code `KEY_OWNERSHIP_MISMATCH`

#### Scenario: Deletion of non-existent key
- **WHEN** a deletion is requested for a key that does not exist
- **THEN** the system returns HTTP 404 with error code `KEY_NOT_FOUND`

### Requirement: Key portability
The system SHALL support key portability — transferring a PIX key from one account to another at the holder's request. The process MUST follow a challenge-response flow: the current owner confirms the transfer before it is committed.

#### Scenario: Successful portability request
- **WHEN** an account holder submits a portability request for a key they own
- **THEN** a portability claim is created in PENDING state and a confirmation challenge is sent to the current owner

#### Scenario: Owner confirms portability
- **WHEN** the current owner confirms the challenge within 7 days
- **THEN** the key is reassigned to the requesting account and DICT is updated

#### Scenario: Portability claim expires
- **WHEN** the current owner does not confirm the challenge within 7 days
- **THEN** the portability claim is cancelled and the key remains with the current owner
