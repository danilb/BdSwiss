package com.task.test.bdswiss
import com.google.gson.annotations.SerializedName

data class RatesModel(
    @SerializedName("rates") val rates: List<Rate>
)

data class Rate(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("price") val price: Double
)