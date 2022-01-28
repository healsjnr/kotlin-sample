package com.squareup.exemplar.actions

import com.squareup.exemplar.models.MaskedString
import misk.testing.MiskTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.crypto.AEADBadTagException

@MiskTest
class CreateInstrumentActionTest {

    private val clearCardString = "1234567890123456"
    private val maskedCard      = "1234********3456"
    private val cardExpiryYear = "29"
    private val cardExpiryMonth = "10"
    private val cardCVC= "909"

    private val requestBodyObject = CreateInstrumentPostBody(
        MaskedString(clearCardString),
        cardExpiryMonth,
        cardExpiryYear,
        cardCVC
    )

    private val requestJson: String = """
            {
              "cardNumber": "$clearCardString",
              "cardExpiryMonth": "$cardExpiryMonth",
              "cardExpiryYear": "$cardExpiryYear",
              "cardCVC": "$cardCVC"
            }
        """.trimIndent()

    @Test
    fun createInstrument() {
        assertThat(CreateInstrumentAction().createInstrument(requestBodyObject))
            .isEqualTo(CreateInstrumentPostResponse(maskedCard, cardExpiryMonth, cardExpiryYear))
    }


}
