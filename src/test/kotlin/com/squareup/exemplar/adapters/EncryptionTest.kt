package com.squareup.exemplar.adapters

import misk.testing.MiskTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.crypto.AEADBadTagException

@MiskTest(startService = false)
class EncryptionTest {

    fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    @Nested
    inner class `Known Answer Tests` {
        private val KEY_BYTES   = ("feffe9928665731c6d6a8f9467308308" +
                                   "feffe9928665731c6d6a8f9467308308").decodeHex()
        private val IV          =  "cafebabefacedbaddecaf888".decodeHex()

        @Test
        fun `NIST AES256 Known Answer Test`() {
            val aadBytes        = null
            val plainTextBytes  = ("d9313225f88406e5a55909c5aff5269a" +
                                   "86a7a9531534f7da2e4c303d8a318a72" +
                                   "1c3c0c95956809532fcf0e2449a6b525" +
                                   "b16aedf5aa0de657ba637b391aafd255").decodeHex()
            val cipherTextBytes = ("522dc1f099567d07f47f37a32a84427d" +
                                   "643a8cdcbfe5c0c97598a2bd2555d1aa" +
                                   "8cb08e48590dbb3da7b08b1056828838" +
                                   "c5f61e6393ba7a0abcc9f662898015ad").decodeHex()
            val tagBytes        =  "b094dac5d93471bdec1a502270e3cc6c".decodeHex()

            val keySpec = Encryption.keyGenAES256(KEY_BYTES)
            val result = Encryption.encryptBytes(plainTextBytes, aadBytes, IV, keySpec)

            assertThat(result).isEqualTo(cipherTextBytes + tagBytes)
        }

        @Test
        fun `NIST AES256 Known Answer Test with AAD`() {
            val aadBytes        = ("feedfacedeadbeeffeedfacedeadbeefabaddad2").decodeHex()
            val plainTextBytes  = ("d9313225f88406e5a55909c5aff5269a" +
                                   "86a7a9531534f7da2e4c303d8a318a72" +
                                   "1c3c0c95956809532fcf0e2449a6b525" +
                                   "b16aedf5aa0de657ba637b39").decodeHex()
            val cipherTextBytes = ("522dc1f099567d07f47f37a32a84427d" +
                                   "643a8cdcbfe5c0c97598a2bd2555d1aa" +
                                   "8cb08e48590dbb3da7b08b1056828838" +
                                   "c5f61e6393ba7a0abcc9f662").decodeHex()
            val tagBytes        =  "76fc6ece0f4e1768cddf8853bb2d551b".decodeHex()

            val keySpec = Encryption.keyGenAES256(KEY_BYTES)
            val result = Encryption.encryptBytes(plainTextBytes, aadBytes, IV, keySpec)

            assertThat(result).isEqualTo(cipherTextBytes + tagBytes)
        }
    }

    @Nested
    inner class `Round trip tests` {
        private val testString  = "String to Be Encrypted"
        private val aadString   = "Some AAD data"

        @Test
        fun `Encrypt Decrypt test with AAD`() {
            val keySpec = Encryption.keyGenAES256()
            val encryptionResult = Encryption.encryptString(testString, aadString, keySpec)
            val decryptedString = Encryption.decryptString(encryptionResult, aadString, keySpec)
            assertThat(decryptedString).isEqualTo(testString)
        }

        @Test
        fun `Encrypt Decrypt test without AAD when it is required`() {
            val keySpec = Encryption.keyGenAES256()
            val encryptionResult = Encryption.encryptString(testString, null, keySpec)
            val e = assertThrows<AEADBadTagException> {
                Encryption.decryptString(encryptionResult, aadString, keySpec)
            }
            assertThat(e.message).isEqualTo("Tag mismatch!")
        }

        @Test
        fun `Encrypt Decrypt test without AAD`() {
            val keySpec = Encryption.keyGenAES256()
            val encryptionResult = Encryption.encryptString(testString, null, keySpec)
            val decryptedString = Encryption.decryptString(encryptionResult, null, keySpec)
            assertThat(decryptedString).isEqualTo(testString)
        }
    }
}