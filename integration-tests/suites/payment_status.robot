*** Settings ***
Documentation    Payment status: query by ID, list with filters, state machine enforcement.
Resource         ../resources/common.resource
Suite Setup      Create API Session

*** Test Cases ***

Get Payment By ID — Existing Payment
    [Documentation]    Create a payment then retrieve it by ID.
    ${create_body}=    Create Dictionary
    ...    payerKey=11122233344
    ...    payeeKey=bob@example.com
    ...    amountCentavos=${200}
    ...    idempotencyKey=idem-status-test-001
    ${create_resp}=    Post JSON    /payments    ${create_body}
    Should Be Equal As Integers    ${create_resp.status_code}    200
    ${payment_id}=    Set Variable    ${create_resp.json()['id']}

    ${get_resp}=    Get JSON    /payments/${payment_id}
    Should Be Equal As Integers    ${get_resp.status_code}    200
    Should Be Equal    ${get_resp.json()['id']}    ${payment_id}
    Dictionary Should Contain Key    ${get_resp.json()}    state
    Dictionary Should Contain Key    ${get_resp.json()}    amountCentavos

Get Payment By ID — Not Found Returns 404
    [Documentation]    Querying a non-existent ID returns HTTP 404 with PAYMENT_NOT_FOUND.
    ${resp}=    GET On Session    pix    /payments/nonexistent-id-xyz    expected_status=404
    Should Be Equal As Integers    ${resp.status_code}    404
    Should Contain    ${resp.text}    PAYMENT_NOT_FOUND

List Payments — Default Page
    [Documentation]    GET /payments returns HTTP 200 with a payments array.
    ${resp}=    Get JSON    /payments
    Should Be Equal As Integers    ${resp.status_code}    200
    Dictionary Should Contain Key    ${resp.json()}    payments

List Payments — Filter By State
    [Documentation]    GET /payments?state=CREATED returns only CREATED payments.
    ${resp}=    Get JSON    /payments?state=CREATED
    Should Be Equal As Integers    ${resp.status_code}    200
    ${payments}=    Set Variable    ${resp.json()['payments']}
    FOR    ${p}    IN    @{payments}
        Should Be Equal    ${p['state']}    PAYMENT_STATE_CREATED
    END
