*** Settings ***
Documentation    Key management: register, lookup (cache hit/miss), delete, portability lifecycle.
Resource         ../resources/common.resource
Suite Setup      Create API Session

*** Variables ***
${CPF_KEY}     55544433322
${EMAIL_KEY}   testuser@example.com

*** Test Cases ***

Register CPF Key — Success
    [Documentation]    Registering a valid CPF key returns HTTP 200 with the created key record.
    ${body}=    Create Dictionary
    ...    key=${CPF_KEY}
    ...    keyType=CPF
    ...    ownerId=owner-test-1
    ...    accountHolder=Test User
    ...    accountBranch=0001
    ...    accountNumber=12345-6
    ${resp}=    Post JSON    /keys    ${body}
    Should Be Equal As Integers    ${resp.status_code}    200
    Should Be Equal    ${resp.json()['key']}    ${CPF_KEY}

Register Duplicate Key — Returns 409
    [Documentation]    Attempting to register the same key twice should return HTTP 409.
    ${body}=    Create Dictionary
    ...    key=${CPF_KEY}
    ...    keyType=CPF
    ...    ownerId=owner-test-2
    ...    accountHolder=Other User
    ...    accountBranch=0002
    ...    accountNumber=99999-9
    ${resp}=    Post JSON    /keys    ${body}
    Should Be Equal As Integers    ${resp.status_code}    409
    Should Contain    ${resp.text}    KEY_ALREADY_EXISTS

Lookup Registered Key — Returns Account Info
    [Documentation]    Looking up a registered key returns the account holder information.
    ${resp}=    Get JSON    /keys/${CPF_KEY}
    Should Be Equal As Integers    ${resp.status_code}    200
    Should Be Equal    ${resp.json()['key']}    ${CPF_KEY}

Lookup Unknown Key — Returns 404
    [Documentation]    Looking up a key that does not exist returns HTTP 404.
    ${resp}=    Get JSON    /keys/00000000000
    Should Be Equal As Integers    ${resp.status_code}    404
    Should Contain    ${resp.text}    KEY_NOT_FOUND

Register Random Key — Server Generates UUID
    [Documentation]    Registering a key of type RANDOM causes the server to generate a UUID v4.
    ${body}=    Create Dictionary
    ...    keyType=RANDOM
    ...    ownerId=owner-test-rand
    ...    accountHolder=Rand User
    ...    accountBranch=0003
    ...    accountNumber=33333-3
    ${resp}=    Post JSON    /keys    ${body}
    Should Be Equal As Integers    ${resp.status_code}    200
    Should Match Regexp    ${resp.json()['key']}
    ...    [0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}

Delete Key — Owner Can Delete
    [Documentation]    The key owner can delete their own key.
    # First register
    ${body}=    Create Dictionary
    ...    key=${EMAIL_KEY}
    ...    keyType=EMAIL
    ...    ownerId=owner-del-test
    ...    accountHolder=Del User
    ...    accountBranch=0004
    ...    accountNumber=44444-4
    Post JSON    /keys    ${body}
    # Delete by owner
    ${resp}=    DELETE On Session    pix    /keys/${EMAIL_KEY}?ownerId=owner-del-test    expected_status=204
    Should Be Equal As Integers    ${resp.status_code}    204

Delete Key — Non-Owner Returns 403
    [Documentation]    Attempting to delete a key owned by someone else returns HTTP 403.
    # Register first
    ${body}=    Create Dictionary
    ...    key=99988877766
    ...    keyType=CPF
    ...    ownerId=real-owner
    ...    accountHolder=Real Owner
    ...    accountBranch=0001
    ...    accountNumber=12345-6
    Post JSON    /keys    ${body}
    # Attempt delete as wrong owner
    ${resp}=    DELETE On Session    pix    /keys/99988877766?ownerId=impostor
    ...    expected_status=403
    Should Be Equal As Integers    ${resp.status_code}    403
    Should Contain    ${resp.text}    KEY_OWNERSHIP_MISMATCH
