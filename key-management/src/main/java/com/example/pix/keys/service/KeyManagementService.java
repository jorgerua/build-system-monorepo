package com.example.pix.keys.service;

import com.example.pix.keys.dict.DictStubClient;
import com.example.pix.keys.store.KeyRepository;
import com.example.pix.keys.store.PixKeyRecord;
import com.example.pix.keys.store.PortabilityClaimRecord;
import com.example.pix.keys.validator.KeyValidator;
import com.example.pix.keys.validator.KeyValidator.KeyType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Core business logic for PIX key registration, lookup, deletion, and portability.
 * Portability follows a challenge-response flow with 7-day expiry.
 */
@Service
public class KeyManagementService {

    private final KeyRepository repo;
    private final DictStubClient dict;

    public KeyManagementService(KeyRepository repo, DictStubClient dict) {
        this.repo = repo;
        this.dict = dict;
    }

    // ── RegisterKey ───────────────────────────────────────────────────────────

    public PixKeyRecord registerKey(String key, KeyType keyType,
                                    String ownerId, String accountHolder,
                                    String accountBranch, String accountNumber) {
        // Generate UUID for random type
        String resolvedKey = key;
        if (keyType == KeyType.RANDOM) {
            resolvedKey = (key == null || key.isEmpty()) ? UUID.randomUUID().toString() : key;
        }

        String validationError = KeyValidator.validate(resolvedKey, keyType);
        if (validationError != null) {
            throw new KeyException(validationError);
        }

        if (repo.findByKey(resolvedKey).isPresent()) {
            throw new KeyException("KEY_ALREADY_EXISTS: key is already registered");
        }

        PixKeyRecord record = new PixKeyRecord(
            resolvedKey, keyType.name(), accountHolder, accountBranch, accountNumber,
            ownerId, Instant.now()
        );

        repo.save(record);
        dict.register(record);
        return record;
    }

    // ── LookupKey ─────────────────────────────────────────────────────────────

    @Cacheable(value = "pixKeyCache", key = "#key")
    public PixKeyRecord lookupKey(String key) {
        Optional<PixKeyRecord> cached = dict.lookup(key);
        return cached.orElseThrow(() ->
            new KeyException("KEY_NOT_FOUND: key does not exist in DICT"));
    }

    // ── DeleteKey ─────────────────────────────────────────────────────────────

    @CacheEvict(value = "pixKeyCache", key = "#key")
    public void deleteKey(String key, String requestingOwnerId) {
        PixKeyRecord record = repo.findByKey(key)
            .orElseThrow(() -> new KeyException("KEY_NOT_FOUND: key does not exist"));

        if (!record.ownerId.equals(requestingOwnerId)) {
            throw new KeyException("KEY_OWNERSHIP_MISMATCH: caller does not own this key");
        }

        repo.delete(key);
        dict.delete(key);
    }

    // ── InitiatePortability ───────────────────────────────────────────────────

    public PortabilityClaimRecord initiatePortability(String key, String requestingOwner) {
        if (repo.findByKey(key).isEmpty()) {
            throw new KeyException("KEY_NOT_FOUND: key does not exist");
        }

        PortabilityClaimRecord claim = new PortabilityClaimRecord();
        claim.claimId = UUID.randomUUID().toString();
        claim.key = key;
        claim.requestingOwner = requestingOwner;
        claim.state = "PENDING";
        claim.initiatedAt = Instant.now();
        claim.expiresAt = Instant.now().plusSeconds(7L * 24 * 60 * 60);

        repo.saveClaim(claim);
        return claim;
    }

    // ── ConfirmPortability ────────────────────────────────────────────────────

    @CacheEvict(value = "pixKeyCache", allEntries = true)
    public PixKeyRecord confirmPortability(String claimId, String confirmingOwner) {
        PortabilityClaimRecord claim = repo.findClaim(claimId)
            .orElseThrow(() -> new KeyException("CLAIM_NOT_FOUND: portability claim does not exist"));

        if (!"PENDING".equals(claim.state)) {
            throw new KeyException("INVALID_CLAIM_STATE: claim is not in PENDING state");
        }

        if (Instant.now().isAfter(claim.expiresAt)) {
            repo.updateClaimState(claimId, "CANCELLED");
            throw new KeyException("CLAIM_EXPIRED: portability claim has expired");
        }

        repo.updateClaimState(claimId, "CONFIRMED");
        repo.updateOwner(claim.key, claim.requestingOwner);
        dict.reassign(claim.key, claim.requestingOwner);

        return repo.findByKey(claim.key)
            .orElseThrow(() -> new KeyException("KEY_NOT_FOUND: key disappeared after portability"));
    }
}
