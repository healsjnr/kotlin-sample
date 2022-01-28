package com.squareup.exemplar.adapters

import misk.moshi.MoshiAdapterModule
import misk.moshi.adapter
import com.squareup.exemplar.models.MaskedString
import com.squareup.moshi.Moshi
import misk.MiskTestingServiceModule
import misk.inject.KAbstractModule
import misk.testing.MiskTest
import misk.testing.MiskTestModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MiskTest(startService = false)
internal class MaskedStringAdapterTest {
    @MiskTestModule
    val module = TestModule()

    @Inject
    lateinit var moshi: Moshi

    val clearCardString = "12345678912345"

    @Test fun `JSON converted to a MaskedString`() {
        val clearJson = "\"$clearCardString\""
        val valueObject = MaskedString(clearCardString)
        val jsonAdapter = moshi.adapter<MaskedString>()
        assertThat(jsonAdapter.fromJson(clearJson)).isEqualTo(valueObject)
    }
    @Test fun `MaskedString converted to JSON with data masked`() {
        val maskedJson = "\"**********2345\""
        val valueObject = MaskedString(clearCardString)
        val jsonAdapter = moshi.adapter<MaskedString>()
        assertThat(jsonAdapter.toJson(valueObject)).isEqualTo(maskedJson.trimMargin())
    }

    class TestModule : KAbstractModule() {
        override fun configure() {
            install(MiskTestingServiceModule())
            install(MoshiAdapterModule(MaskedStringAdapter))
        }
    }
}
