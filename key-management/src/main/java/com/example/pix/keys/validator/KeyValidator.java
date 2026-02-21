package com.example.pix.keys.validator;

import java.util.regex.Pattern;

/**
 * Validates PIX key formats per BCB/DICT specification.
 * Supported types: CPF, CNPJ, email, phone (E.164 +55), random UUID.
 */
public final class KeyValidator {

    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{14}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+55\\d{10,11}$");
    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
                    Pattern.CASE_INSENSITIVE);

    private KeyValidator() {}

    /** Returns true if key matches CPF format (11 digits). */
    public static boolean isValidCpf(String key) {
        return key != null && CPF_PATTERN.matcher(key).matches();
    }

    /** Returns true if key matches CNPJ format (14 digits). */
    public static boolean isValidCnpj(String key) {
        return key != null && CNPJ_PATTERN.matcher(key).matches();
    }

    /** Returns true if key matches a basic email format. */
    public static boolean isValidEmail(String key) {
        return key != null && EMAIL_PATTERN.matcher(key).matches();
    }

    /** Returns true if key is a Brazilian phone in E.164 format (+55XXXXXXXXXXX). */
    public static boolean isValidPhone(String key) {
        return key != null && PHONE_PATTERN.matcher(key).matches();
    }

    /** Returns true if key is a valid UUID v4. */
    public static boolean isValidUuid(String key) {
        return key != null && UUID_PATTERN.matcher(key).matches();
    }

    public enum KeyType { CPF, CNPJ, EMAIL, PHONE, RANDOM }

    /**
     * Validates a key against its declared type.
     * @return null if valid, or an error message string if invalid.
     */
    public static String validate(String key, KeyType type) {
        boolean valid = switch (type) {
            case CPF    -> isValidCpf(key);
            case CNPJ   -> isValidCnpj(key);
            case EMAIL  -> isValidEmail(key);
            case PHONE  -> isValidPhone(key);
            case RANDOM -> key == null || key.isEmpty() || isValidUuid(key);
        };
        return valid ? null : "INVALID_KEY_FORMAT: key does not match declared type " + type;
    }
}
