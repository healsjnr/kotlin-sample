package com.squareup.exemplar.models

import com.squareup.exemplar.adapters.Encryption
import misk.testing.MiskTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MiskTest
class MaskedStringTest {

    private val testString      = "1234566789012345"

    @Nested inner class `When data is longer than clear text length` {
        @Test fun `The string is masked as expected`() {
            val maskedString    = "************2345"
            assertThat(MaskedString(testString).toString()).isEqualTo(maskedString)
        }

        @Test fun `The string is masked when the clear text value is supplied`() {
            val maskedString    = "**********012345"
            assertThat(MaskedString(testString,6).toString()).isEqualTo(maskedString)
        }
    }

    @Nested inner class `When data is shorter than clear text length` {
        private val testString      = "123"
        private val maskedString    = "***"
        @Test fun `The string is masked as expected`() {
            assertThat(MaskedString(testString).toString()).isEqualTo(maskedString)
        }

        @Test fun `The string is masked when the clear text value is supplied`() {
            assertThat(MaskedString(testString,6).toString()).isEqualTo(maskedString)
        }
    }

    /* Attempts to access `data` that don't work:
      - Accessing the `data` directly -> Compile error, private member access
      - Creating an extension function to access `data` -> Compile error, private member access
      - Creating a subclass to expose `data` -> Compile error, class is final
     */
    @Nested inner class `Data is not returned when data is accessed insecurely` {
        // The only valid way so far to extract this data
        /*@Test fun `via declared field`() {
            val maskedString = MaskedString(testString)
            val rawData = MaskedString::class.java.getDeclaredField("data")?.let {
                it.isAccessible = true
                it.get(maskedString) as String
            }
            assertThat(rawData).isNotEqualTo(testString)
        }*/
    }

    @Nested inner class `Data is only accessible when called from correct class` {
        private val maskedString = MaskedString(testString)
        @Test fun `empty when called from invalid class`() {
            val testCaller = MaskedTestCaller()
            val e = assertThrows<Exception> { testCaller.useRawData(maskedString) }
            assertThat(e.message).isEqualTo("Invalid calling class for MaskedString")
        }

        @Test fun `data is returned when called from the class`() {
            val tokenizedString = "1234********2345"
            val keySpec = Encryption.keyGenAES256()
            val result = Tokenizer.tokenize(maskedString, keySpec)
            assertThat(result.tokenizedCardNumber).isEqualTo(tokenizedString)
        }
    }
}

class MaskedTestCaller {
    fun useRawData(data: MaskedString): String {
        return data.getRawData()
    }
}