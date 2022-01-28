package com.squareup.exemplar.adapters

import com.squareup.exemplar.models.MaskedString
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

object MaskedStringAdapter {
    @FromJson
    fun read(str: String): MaskedString = MaskedString(str)

    @ToJson
    fun write(mstr: MaskedString): String = mstr.toString()
}
