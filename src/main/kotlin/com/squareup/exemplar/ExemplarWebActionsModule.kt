package com.squareup.exemplar

import com.squareup.exemplar.actions.*
import com.squareup.exemplar.adapters.MaskedStringAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import misk.inject.KAbstractModule
import misk.moshi.MoshiAdapterModule
import misk.web.WebActionModule

class ExemplarWebActionsModule : KAbstractModule() {
  override fun configure() {
    install(WebActionModule.create<HelloWebAction>())
    install(WebActionModule.create<HelloWebPostAction>())
    install(WebActionModule.create<EchoFormAction>())
    install(WebActionModule.create<HelloWebProtoAction>())
    install(MoshiAdapterModule(MaskedStringAdapter))
    install(WebActionModule.create<CreateInstrumentAction>())
  }
}
