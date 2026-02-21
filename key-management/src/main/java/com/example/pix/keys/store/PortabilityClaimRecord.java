package com.example.pix.keys.store;

import java.time.Instant;

/** DTO representing a portability claim record. */
public class PortabilityClaimRecord {
    public String claimId;
    public String key;
    public String requestingOwner;
    public String state;
    public Instant initiatedAt;
    public Instant expiresAt;
}
