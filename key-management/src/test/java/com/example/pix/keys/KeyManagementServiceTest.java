package com.example.pix.keys;

import com.example.pix.keys.dict.DictStubClient;
import com.example.pix.keys.service.KeyException;
import com.example.pix.keys.service.KeyManagementService;
import com.example.pix.keys.store.KeyRepository;
import com.example.pix.keys.store.PixKeyRecord;
import com.example.pix.keys.store.PortabilityClaimRecord;
import com.example.pix.keys.validator.KeyValidator.KeyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteDataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit unit tests for KeyManagementService.
 * Uses an in-memory SQLite database — no external services required.
 */
class KeyManagementServiceTest {

    private KeyManagementService svc;

    @BeforeEach
    void setUp() {
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl("jdbc:sqlite::memory:");
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        KeyRepository repo = new KeyRepository(jdbc);
        DictStubClient dict = new DictStubClient();
        svc = new KeyManagementService(repo, dict);
    }

    // ── RegisterKey tests ─────────────────────────────────────────────────────

    @Test
    void registerKey_cpf_success() {
        PixKeyRecord rec = svc.registerKey("12345678901", KeyType.CPF,
            "owner-1", "Alice", "001", "12345-6");
        assertEquals("12345678901", rec.key);
        assertEquals("CPF", rec.keyType);
        assertNotNull(rec.createdAt);
    }

    @Test
    void registerKey_cnpj_success() {
        PixKeyRecord rec = svc.registerKey("12345678000190", KeyType.CNPJ,
            "owner-2", "Acme Ltd", "001", "99999-0");
        assertEquals("12345678000190", rec.key);
        assertEquals("CNPJ", rec.keyType);
    }

    @Test
    void registerKey_email_success() {
        PixKeyRecord rec = svc.registerKey("alice@example.com", KeyType.EMAIL,
            "owner-1", "Alice", "001", "12345-6");
        assertEquals("alice@example.com", rec.key);
    }

    @Test
    void registerKey_phone_success() {
        PixKeyRecord rec = svc.registerKey("+5511987654321", KeyType.PHONE,
            "owner-1", "Bob", "002", "54321-0");
        assertEquals("+5511987654321", rec.key);
    }

    @Test
    void registerKey_random_generatesUuid() {
        PixKeyRecord rec = svc.registerKey(null, KeyType.RANDOM,
            "owner-3", "Carol", "003", "11111-1");
        assertNotNull(rec.key);
        // UUID v4 format
        assertTrue(rec.key.matches("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"),
            "random key should be UUID v4");
    }

    @Test
    void registerKey_duplicate_throws() {
        svc.registerKey("12345678901", KeyType.CPF, "owner-1", "Alice", "001", "1");
        KeyException ex = assertThrows(KeyException.class,
            () -> svc.registerKey("12345678901", KeyType.CPF, "owner-2", "Bob", "002", "2"));
        assertTrue(ex.getMessage().contains("KEY_ALREADY_EXISTS"));
    }

    @Test
    void registerKey_invalidCpfFormat_throws() {
        KeyException ex = assertThrows(KeyException.class,
            () -> svc.registerKey("not-a-cpf", KeyType.CPF, "owner-1", "Alice", "001", "1"));
        assertTrue(ex.getMessage().contains("INVALID_KEY_FORMAT"));
    }

    @Test
    void registerKey_invalidEmail_throws() {
        KeyException ex = assertThrows(KeyException.class,
            () -> svc.registerKey("not-an-email", KeyType.EMAIL, "owner-1", "Alice", "001", "1"));
        assertTrue(ex.getMessage().contains("INVALID_KEY_FORMAT"));
    }

    @Test
    void registerKey_invalidPhone_throws() {
        KeyException ex = assertThrows(KeyException.class,
            () -> svc.registerKey("+1234567890", KeyType.PHONE, "owner-1", "Alice", "001", "1"));
        assertTrue(ex.getMessage().contains("INVALID_KEY_FORMAT"));
    }

    // ── LookupKey tests ───────────────────────────────────────────────────────

    @Test
    void lookupKey_registered_success() {
        svc.registerKey("12345678901", KeyType.CPF, "owner-1", "Alice", "001", "1");
        PixKeyRecord rec = svc.lookupKey("12345678901");
        assertEquals("Alice", rec.accountHolder);
        assertEquals("owner-1", rec.ownerId);
    }

    @Test
    void lookupKey_notFound_throws() {
        KeyException ex = assertThrows(KeyException.class,
            () -> svc.lookupKey("99999999999"));
        assertTrue(ex.getMessage().contains("KEY_NOT_FOUND"));
    }

    // ── DeleteKey tests ───────────────────────────────────────────────────────

    @Test
    void deleteKey_owner_success() {
        svc.registerKey("12345678901", KeyType.CPF, "owner-1", "Alice", "001", "1");
        assertDoesNotThrow(() -> svc.deleteKey("12345678901", "owner-1"));
    }

    @Test
    void deleteKey_wrongOwner_throws() {
        svc.registerKey("12345678901", KeyType.CPF, "owner-1", "Alice", "001", "1");
        KeyException ex = assertThrows(KeyException.class,
            () -> svc.deleteKey("12345678901", "owner-2"));
        assertTrue(ex.getMessage().contains("KEY_OWNERSHIP_MISMATCH"));
    }

    @Test
    void deleteKey_notFound_throws() {
        KeyException ex = assertThrows(KeyException.class,
            () -> svc.deleteKey("99999999999", "owner-1"));
        assertTrue(ex.getMessage().contains("KEY_NOT_FOUND"));
    }

    // ── Portability tests ─────────────────────────────────────────────────────

    @Test
    void portability_initiateAndConfirm_success() {
        svc.registerKey("12345678901", KeyType.CPF, "owner-1", "Alice", "001", "1");

        PortabilityClaimRecord claim = svc.initiatePortability("12345678901", "owner-2");
        assertEquals("PENDING", claim.state);
        assertNotNull(claim.claimId);

        PixKeyRecord transferred = svc.confirmPortability(claim.claimId, "owner-1");
        assertEquals("owner-2", transferred.ownerId);
    }

    @Test
    void portability_initiate_keyNotFound_throws() {
        KeyException ex = assertThrows(KeyException.class,
            () -> svc.initiatePortability("99999999999", "owner-2"));
        assertTrue(ex.getMessage().contains("KEY_NOT_FOUND"));
    }
}
