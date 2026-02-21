*** Settings ***
Documentation    Inbound payment receiving: credit notification, idempotency, unknown key rejection.
Resource         ../resources/common.resource
Suite Setup      Create API Session

*** Test Cases ***

Receive Inbound Credit — First Time
    [Documentation]    First-time inbound credit with a new SPI end-to-end ID creates a SETTLED payment.
    ${body}=    Create Dictionary
    ...    spiEndToEndId=E1234567890ABCDEF001
    ...    payerKey=external-payer@bank.com
    ...    payeeKey=12345678901
    ...    amountCentavos=${500}
    ${resp}=    Post JSON    /payments/inbound    ${body}
    Should Be Equal As Integers    ${resp.status_code}    200
    Should Be Equal    ${resp.json()['state']}    PAYMENT_STATE_SETTLED

Receive Inbound Credit — Duplicate Is Idempotent
    [Documentation]    Sending the same SPI end-to-end ID twice returns the same payment (HTTP 200, no duplicate).
    ${body}=    Create Dictionary
    ...    spiEndToEndId=E1234567890ABCDEF002
    ...    payerKey=external-payer@bank.com
    ...    payeeKey=12345678901
    ...    amountCentavos=${500}
    ${resp1}=    Post JSON    /payments/inbound    ${body}
    Should Be Equal As Integers    ${resp1.status_code}    200
    ${resp2}=    Post JSON    /payments/inbound    ${body}
    Should Be Equal As Integers    ${resp2.status_code}    200
    Should Be Equal    ${resp1.json()['id']}    ${resp2.json()['id']}

Receive Inbound Credit — Missing Amount Returns 422
    [Documentation]    Inbound credit with amount zero should return HTTP 422.
    ${body}=    Create Dictionary
    ...    spiEndToEndId=E1234567890ABCDEF003
    ...    payerKey=external-payer@bank.com
    ...    payeeKey=12345678901
    ...    amountCentavos=${0}
    ${resp}=    Post JSON    /payments/inbound    ${body}
    Should Be Equal As Integers    ${resp.status_code}    422
