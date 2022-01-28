package com.squareup.exemplar.models

data class MaskedString(private val data: String, val clearChars: Int = 4) {
    private val ALLOWED_RAW_DATA_CALLER = "com.squareup.exemplar.models.Tokenizer"
    override fun toString(): String {
        val maskedUpTo = when  {
            (data.length - clearChars) < 0 -> data.length
            else -> data.length - clearChars
        }
        val maskedData = data.mapIndexed { index, c ->
            when {
                index < maskedUpTo-> "*"
                else -> c
            }
        }.joinToString("")

        return maskedData
    }

    fun getRawData(): String = when (Throwable().stackTrace[1].className) {
        ALLOWED_RAW_DATA_CALLER -> data
        else -> throw Exception("Invalid calling class for MaskedString")
    }
}
