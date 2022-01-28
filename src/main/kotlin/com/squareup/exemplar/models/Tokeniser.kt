package com.squareup.exemplar.models

import com.squareup.exemplar.adapters.Encryption
import com.squareup.exemplar.adapters.EncryptionResult
import javax.crypto.spec.SecretKeySpec

data class TokenizedCard(val encryptedCardNumber: EncryptionResult, val tokenizedCardNumber: String)

object Tokenizer {
    private final val TOKEN_RANGE = 4..11
    fun tokenize(data: MaskedString, keySpec: SecretKeySpec): TokenizedCard {
        val rawCardData = data.getRawData()
        val encryptedCardNumber = Encryption.encryptString(rawCardData, null, keySpec)
        val tokenizedCardNumber = rawCardData.mapIndexed { index, c ->
            when {
                index in TOKEN_RANGE -> "*"
                else -> c
            }
        }.joinToString("")
        return TokenizedCard(encryptedCardNumber, tokenizedCardNumber)
    }
}