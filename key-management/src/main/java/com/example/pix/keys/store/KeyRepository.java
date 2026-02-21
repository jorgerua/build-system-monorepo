package com.example.pix.keys.store;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * SQLite-backed repository for PIX keys.
 * Each key-management instance owns its own database file (no shared state).
 */
@Repository
public class KeyRepository {

    private final JdbcTemplate jdbc;

    public KeyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        initSchema();
    }

    private void initSchema() {
        jdbc.execute("PRAGMA journal_mode=WAL");
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS pix_keys (
                key            TEXT PRIMARY KEY,
                key_type       TEXT NOT NULL,
                account_holder TEXT NOT NULL,
                account_branch TEXT NOT NULL,
                account_number TEXT NOT NULL,
                owner_id       TEXT NOT NULL,
                created_at     INTEGER NOT NULL
            )
            """);
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS portability_claims (
                claim_id          TEXT PRIMARY KEY,
                key               TEXT NOT NULL,
                requesting_owner  TEXT NOT NULL,
                state             TEXT NOT NULL,
                initiated_at      INTEGER NOT NULL,
                expires_at        INTEGER NOT NULL
            )
            """);
    }

    public void save(PixKeyRecord record) {
        jdbc.update(
            "INSERT INTO pix_keys (key, key_type, account_holder, account_branch, account_number, owner_id, created_at) VALUES (?,?,?,?,?,?,?)",
            record.key, record.keyType, record.accountHolder, record.accountBranch,
            record.accountNumber, record.ownerId, record.createdAt.getEpochSecond()
        );
    }

    public Optional<PixKeyRecord> findByKey(String key) {
        List<PixKeyRecord> results = jdbc.query(
            "SELECT key, key_type, account_holder, account_branch, account_number, owner_id, created_at FROM pix_keys WHERE key = ?",
            new PixKeyRowMapper(), key
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void delete(String key) {
        jdbc.update("DELETE FROM pix_keys WHERE key = ?", key);
    }

    public void updateOwner(String key, String newOwnerId) {
        jdbc.update("UPDATE pix_keys SET owner_id = ? WHERE key = ?", newOwnerId, key);
    }

    // Portability claim operations

    public void saveClaim(PortabilityClaimRecord claim) {
        jdbc.update(
            "INSERT INTO portability_claims (claim_id, key, requesting_owner, state, initiated_at, expires_at) VALUES (?,?,?,?,?,?)",
            claim.claimId, claim.key, claim.requestingOwner, claim.state,
            claim.initiatedAt.getEpochSecond(), claim.expiresAt.getEpochSecond()
        );
    }

    public Optional<PortabilityClaimRecord> findClaim(String claimId) {
        List<PortabilityClaimRecord> results = jdbc.query(
            "SELECT claim_id, key, requesting_owner, state, initiated_at, expires_at FROM portability_claims WHERE claim_id = ?",
            new ClaimRowMapper(), claimId
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void updateClaimState(String claimId, String newState) {
        jdbc.update("UPDATE portability_claims SET state = ? WHERE claim_id = ?", newState, claimId);
    }

    private static final class PixKeyRowMapper implements RowMapper<PixKeyRecord> {
        @Override
        public PixKeyRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PixKeyRecord(
                rs.getString("key"),
                rs.getString("key_type"),
                rs.getString("account_holder"),
                rs.getString("account_branch"),
                rs.getString("account_number"),
                rs.getString("owner_id"),
                Instant.ofEpochSecond(rs.getLong("created_at"))
            );
        }
    }

    private static final class ClaimRowMapper implements RowMapper<PortabilityClaimRecord> {
        @Override
        public PortabilityClaimRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            PortabilityClaimRecord c = new PortabilityClaimRecord();
            c.claimId = rs.getString("claim_id");
            c.key = rs.getString("key");
            c.requestingOwner = rs.getString("requesting_owner");
            c.state = rs.getString("state");
            c.initiatedAt = Instant.ofEpochSecond(rs.getLong("initiated_at"));
            c.expiresAt = Instant.ofEpochSecond(rs.getLong("expires_at"));
            return c;
        }
    }
}
