package org.example.bankcardmanagement.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionUtilTest {
    private static final String TEST_DATA = "Test secret message";
    private static final String SHORT_KEY = "shortkey";
    private static final String INVALID_ALGORITHM = "DES";

    @Test
    @DisplayName("Шифрование и дешифрование возвращают исходные данные")
    void testEncryptDecrypt() {
        String encrypted = EncryptionUtil.encrypt(TEST_DATA);
        assertNotNull(encrypted);
        assertNotEquals(TEST_DATA, encrypted);

        String decrypted = EncryptionUtil.decrypt(encrypted);
        assertEquals(TEST_DATA, decrypted);
    }

    @Test
    @DisplayName("Шифрование пустой строки")
    void testEncryptEmptyString() {
        String encrypted = EncryptionUtil.encrypt("");
        assertNotNull(encrypted);

        String decrypted = EncryptionUtil.decrypt(encrypted);
        assertEquals("", decrypted);
    }

    @Test
    @DisplayName("Дешифрование неверных данных возвращает пустую строку")
    void testDecryptInvalidData() {
        String invalidData = "Not a valid encrypted string";
        String result = EncryptionUtil.decrypt(invalidData);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Шифрование null возвращает пустую строку")
    void testEncryptNull() {
        String result = EncryptionUtil.encrypt(null);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Дешифрование null возвращает пустую строку")
    void testDecryptNull() {
        String result = EncryptionUtil.decrypt(null);
        assertEquals("", result);
    }
}