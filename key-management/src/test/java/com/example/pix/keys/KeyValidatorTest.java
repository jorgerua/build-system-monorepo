package com.example.pix.keys;

import com.example.pix.keys.validator.KeyValidator;
import com.example.pix.keys.validator.KeyValidator.KeyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for PIX key format validators. */
class KeyValidatorTest {

    @Test void cpf_valid()         { assertTrue(KeyValidator.isValidCpf("12345678901")); }
    @Test void cpf_tooShort()      { assertFalse(KeyValidator.isValidCpf("1234567890")); }
    @Test void cpf_withLetters()   { assertFalse(KeyValidator.isValidCpf("1234567890A")); }

    @Test void cnpj_valid()        { assertTrue(KeyValidator.isValidCnpj("12345678000190")); }
    @Test void cnpj_tooShort()     { assertFalse(KeyValidator.isValidCnpj("1234567800019")); }

    @Test void email_valid()       { assertTrue(KeyValidator.isValidEmail("user@example.com")); }
    @Test void email_noAt()        { assertFalse(KeyValidator.isValidEmail("userexample.com")); }
    @Test void email_noDomain()    { assertFalse(KeyValidator.isValidEmail("user@")); }

    @Test void phone_valid10()     { assertTrue(KeyValidator.isValidPhone("+5511987654321")); }
    @Test void phone_valid9()      { assertTrue(KeyValidator.isValidPhone("+551198765432")); }
    @Test void phone_noPlus55()    { assertFalse(KeyValidator.isValidPhone("+1234567890")); }
    @Test void phone_tooShort()    { assertFalse(KeyValidator.isValidPhone("+55119")); }

    @Test void uuid_valid()        { assertTrue(KeyValidator.isValidUuid("550e8400-e29b-41d4-a716-446655440000")); }
    @Test void uuid_wrongVersion() { assertFalse(KeyValidator.isValidUuid("550e8400-e29b-31d4-a716-446655440000")); }
    @Test void uuid_notUuid()      { assertFalse(KeyValidator.isValidUuid("not-a-uuid")); }

    @Test
    void validate_cpf_valid_returnsNull() {
        assertNull(KeyValidator.validate("12345678901", KeyType.CPF));
    }

    @Test
    void validate_cpf_invalid_returnsError() {
        assertNotNull(KeyValidator.validate("bad", KeyType.CPF));
        assertTrue(KeyValidator.validate("bad", KeyType.CPF).contains("INVALID_KEY_FORMAT"));
    }

    @Test
    void validate_random_nullKey_returnsNull() {
        assertNull(KeyValidator.validate(null, KeyType.RANDOM));
        assertNull(KeyValidator.validate("", KeyType.RANDOM));
    }
}
