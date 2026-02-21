*** Settings ***
Documentation    Notifications: webhook registration, invalid URL rejection.
...              Note: end-to-end delivery verification requires a running webhook receiver;
...              that scenario is covered by the payment_initiation + webhook registration tests.
Resource         ../resources/common.resource
Suite Setup      Create API Session

*** Test Cases ***

Register Webhook — Valid HTTPS URL
    [Documentation]    Registering a webhook with a valid HTTPS URL returns HTTP 200 with a webhook ID.
    ${body}=    Create Dictionary
    ...    url=https://example.com/webhook
    ...    eventTypes=${{ ["payment.settled", "payment.failed"] }}
    ${resp}=    Post JSON    /webhooks    ${body}
    Should Be Equal As Integers    ${resp.status_code}    200
    Dictionary Should Contain Key    ${resp.json()}    id

Register Webhook — HTTP URL Rejected
    [Documentation]    Non-HTTPS webhook URL is rejected with HTTP 422 and INVALID_WEBHOOK_URL code.
    ${body}=    Create Dictionary
    ...    url=http://example.com/webhook
    ...    eventTypes=${{ ["payment.settled"] }}
    ${resp}=    Post JSON    /webhooks    ${body}
    Should Be Equal As Integers    ${resp.status_code}    422
    Should Contain    ${resp.text}    INVALID_WEBHOOK_URL

Register Webhook — Missing URL Rejected
    [Documentation]    Missing url field is rejected with HTTP 422.
    ${body}=    Create Dictionary
    ...    eventTypes=${{ ["payment.settled"] }}
    ${resp}=    Post JSON    /webhooks    ${body}
    Should Be Equal As Integers    ${resp.status_code}    422

Delete Webhook — Existing
    [Documentation]    A registered webhook can be deleted and returns HTTP 204.
    ${body}=    Create Dictionary
    ...    url=https://example.com/webhook-del-test
    ...    eventTypes=${{ ["payment.created"] }}
    ${reg_resp}=    Post JSON    /webhooks    ${body}
    Should Be Equal As Integers    ${reg_resp.status_code}    200
    ${webhook_id}=    Set Variable    ${reg_resp.json()['id']}

    ${del_resp}=    DELETE On Session    pix    /webhooks/${webhook_id}    expected_status=204
    Should Be Equal As Integers    ${del_resp.status_code}    204

Delete Webhook — Not Found Returns 404
    [Documentation]    Deleting a non-existent webhook returns HTTP 404.
    ${resp}=    DELETE On Session    pix    /webhooks/no-such-id    expected_status=404
    Should Be Equal As Integers    ${resp.status_code}    404
