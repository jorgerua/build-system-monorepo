*** Settings ***
Library     RequestsLibrary
Library     Collections
Library     String

*** Variables ***
${BASE_URL}    http://localhost:8080

*** Keywords ***
Create API Session
    [Documentation]    Create a RequestsLibrary session against the api-gateway.
    Create Session    pix    ${BASE_URL}    verify=False

Post JSON
    [Arguments]    ${path}    ${body}
    ${resp}=    POST On Session    pix    ${path}    json=${body}
    RETURN    ${resp}

Get JSON
    [Arguments]    ${path}
    ${resp}=    GET On Session    pix    ${path}
    RETURN    ${resp}

Delete JSON
    [Arguments]    ${path}
    ${resp}=    DELETE On Session    pix    ${path}    expected_status=204
    RETURN    ${resp}
