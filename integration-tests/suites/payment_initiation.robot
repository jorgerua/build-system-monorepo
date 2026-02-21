*** Settings ***
Documentation    Payment initiation happy path: create → submit → settled flow.
Resource         ../resources/common.resource
Suite Setup      Create API Session

*** Test Cases ***

Create Payment — Happy Path
    [Documentation]    POST /payments with valid fields creates a payment in CREATED state.
    ${body}=    Create Dictionary
    ...    payerKey=12345678901
    ...    payeeKey=alice@example.com
    ...    amountCentavos=${100}
    ...    idempotencyKey=idem-happy-path-001
    ${resp}=    Post JSON    /payments    ${body}
    Should Be Equal As Integers    ${resp.status_code}    200
    Dictionary Should Contain Key    ${resp.json()}    id
    Should Be Equal    ${resp.json()['state']}    PAYMENT_STATE_CREATED

Duplicate Idempotency Key Returns Original
    [Documentation]    Submitting the same idempotency key twice returns the original payment (HTTP 200).
    ${body}=    Create Dictionary
    ...    payerKey=12345678901
    ...    payeeKey=alice@example.com
    ...    amountCentavos=${100}
    ...    idempotencyKey=idem-dedup-test-001
    ${resp1}=    Post JSON    /payments    ${body}
    Should Be Equal As Integers    ${resp1.status_code}    200
    ${resp2}=    Post JSON    /payments    ${body}
    Should Be Equal As Integers    ${resp2.status_code}    200
    Should Be Equal    ${resp1.json()['id']}    ${resp2.json()['id']}

Create Payment — Missing PayerKey Returns 422
    [Documentation]    Missing required field should return HTTP 422 with MISSING_FIELD code.
    ${body}=    Create Dictionary
    ...    payeeKey=alice@example.com
    ...    amountCentavos=${100}
    ...    idempotencyKey=idem-missing-payer-001
    ${resp}=    Post JSON    /payments    ${body}
    Should Be Equal As Integers    ${resp.status_code}    422
    Should Contain    ${resp.text}    MISSING_FIELD

Create Payment — Amount Zero Returns 422
    [Documentation]    Amount of zero should return HTTP 422 with INVALID_AMOUNT code.
    ${body}=    Create Dictionary
    ...    payerKey=12345678901
    ...    payeeKey=alice@example.com
    ...    amountCentavos=${0}
    ...    idempotencyKey=idem-zero-amount-001
    ${resp}=    Post JSON    /payments    ${body}
    Should Be Equal As Integers    ${resp.status_code}    422
    Should Contain    ${resp.text}    INVALID_AMOUNT

Create Payment — Amount Exceeds Maximum Returns 422
    [Documentation]    Amount exceeding R$999,999,999.99 should return HTTP 422 with AMOUNT_EXCEEDS_LIMIT.
    ${body}=    Create Dictionary
    ...    payerKey=12345678901
    ...    payeeKey=alice@example.com
    ...    amountCentavos=${100000000000}
    ...    idempotencyKey=idem-exceeds-max-001
    ${resp}=    Post JSON    /payments    ${body}
    Should Be Equal As Integers    ${resp.status_code}    422
    Should Contain    ${resp.text}    AMOUNT_EXCEEDS_LIMIT
