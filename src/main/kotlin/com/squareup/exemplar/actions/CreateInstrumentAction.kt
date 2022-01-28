package com.squareup.exemplar.actions

import com.squareup.exemplar.adapters.Encryption
import com.squareup.exemplar.models.MaskedString
import com.squareup.exemplar.models.Tokenizer
import misk.security.authz.Unauthenticated
import misk.web.Post
import misk.web.RequestBody
import misk.web.RequestContentType
import misk.web.ResponseContentType
import misk.web.actions.WebAction
import misk.web.mediatype.MediaTypes
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CreateInstrumentAction @Inject constructor(): WebAction {

    private val keySpec: SecretKeySpec = Encryption.keyGenAES256()

    @Post("/instrument")
    @Unauthenticated
    @RequestContentType(MediaTypes.APPLICATION_JSON)
    @ResponseContentType(MediaTypes.APPLICATION_JSON)
    fun createInstrument(@RequestBody body: CreateInstrumentPostBody): CreateInstrumentPostResponse {
        // Encrypted and Tokenize card -- Do this at the same time so we have the smallest surface area with access to PAN?
        val (encryptCardNumber, tokenizedCardNumber) = Tokenizer.tokenize(body.cardNumber, keySpec)

        // Store Encrypted card
        println(encryptCardNumber)

        // Return response
        return CreateInstrumentPostResponse(tokenizedCardNumber, body.cardExpiryMonth, body.cardExpiryYear)
    }
}

data class CreateInstrumentPostResponse(
    val cardNumber: String,
    val cardExpiryMonth: String,
    val cardExpiryYear: String
)

data class CreateInstrumentPostBody(
    val cardNumber: MaskedString,
    val cardExpiryMonth: String,
    val cardExpiryYear: String,
    val cardCVC: String
    )
