package com.example.pix.keys.store;

import java.time.Instant;

/** DTO representing a persisted PIX key record. */
public class PixKeyRecord {
    public String key;
    public String keyType;
    public String accountHolder;
    public String accountBranch;
    public String accountNumber;
    public String ownerId;
    public Instant createdAt;

    public PixKeyRecord() {}

    public PixKeyRecord(String key, String keyType, String accountHolder,
                        String accountBranch, String accountNumber,
                        String ownerId, Instant createdAt) {
        this.key = key;
        this.keyType = keyType;
        this.accountHolder = accountHolder;
        this.accountBranch = accountBranch;
        this.accountNumber = accountNumber;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }
}
