package com.squareup.exemplar.adapters

import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

data class EncryptionResult(val cipherText: String, val ivText: String)

object Encryption {
    final val GCM_IV_LENGTH = 12
    final val GCM_TAG_LENGTH = 16
    final val AES_KEY_LENGTH = 256

    fun keyGenAES256(keyBytes: ByteArray? = null): SecretKeySpec {
        val bytes = keyBytes ?: run {
            val keygen = KeyGenerator.getInstance("AES")
            keygen.init(AES_KEY_LENGTH)
            keygen.generateKey().encoded
        }
        return SecretKeySpec(bytes, "AES")
    }

    fun encryptBytes(plainTextBytes: ByteArray, aadBytes: ByteArray?, ivBytes: ByteArray, keySpec: SecretKeySpec): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec)
        aadBytes?.let {
            cipher.updateAAD(it);
        }
        return cipher.doFinal(plainTextBytes)
    }

    fun encryptString(plainText: String, aad: String?, keySpec: SecretKeySpec): EncryptionResult {
        val IV = ByteArray(GCM_IV_LENGTH)
        SecureRandom().let { it.nextBytes(IV) }
        val aadBytes = aad?.toByteArray()
        val plainTextBytes = plainText.toByteArray()
        val cipherTextBytes = encryptBytes(plainTextBytes, aadBytes, IV, keySpec)
        val encoder = Base64.getEncoder()
        return EncryptionResult(encoder.encodeToString(cipherTextBytes), encoder.encodeToString(IV))
    }

    fun decryptBytes(cipherBytes: ByteArray, aadBytes: ByteArray?, ivBytes: ByteArray, keySpec: SecretKeySpec): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec)
        aadBytes?.run { cipher.updateAAD(aadBytes) }
        return cipher.doFinal(cipherBytes)
    }

    // Spec for Cipher, and IvString: Base64 encoded String
    fun decryptString(cipherObject: EncryptionResult, aadString: String?, keySpec: SecretKeySpec): String {
        val decoder = Base64.getDecoder()
        val ivBytes = decoder.decode(cipherObject.ivText)
        val cipherTextBytes = decoder.decode(cipherObject.cipherText)
        val aadBytes = aadString?.toByteArray()

        val plainTextBytes = decryptBytes(cipherTextBytes, aadBytes, ivBytes, keySpec)
        val plainText = String(plainTextBytes)
        println(plainText)
        return plainText
    }
}