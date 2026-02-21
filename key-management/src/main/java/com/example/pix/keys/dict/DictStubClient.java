package com.example.pix.keys.dict;

import com.example.pix.keys.store.PixKeyRecord;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TEST DOUBLE — In-memory DICT (Diretório de Identificadores de Contas Transacionais) stub.
 * Real BCB DICT connectivity is out of scope for this test harness.
 *
 * <p>The stub mirrors operations performed on the local SQLite store so that
 * lookup behaviour is consistent without requiring an external service.
 */
@Component
public class DictStubClient {

    private final Map<String, PixKeyRecord> store = new ConcurrentHashMap<>();

    /** Registers a key in the stub DICT. */
    public void register(PixKeyRecord record) {
        store.put(record.key, record);
    }

    /** Looks up a key in the stub DICT. Returns empty if not found. */
    public Optional<PixKeyRecord> lookup(String key) {
        return Optional.ofNullable(store.get(key));
    }

    /** Removes a key from the stub DICT. */
    public void delete(String key) {
        store.remove(key);
    }

    /** Reassigns a key to a new owner in the stub DICT. */
    public void reassign(String key, String newOwnerId) {
        PixKeyRecord rec = store.get(key);
        if (rec != null) {
            rec.ownerId = newOwnerId;
        }
    }
}
