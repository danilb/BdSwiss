package com.task.test.bdswiss.api

import com.task.test.bdswiss.models.RatesModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface BDService {
    @GET("rates")
    fun getRates(): Observable<RatesModel>

    companion object {
        fun create(): BDService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://mt4-api-staging.herokuapp.com")
                    .build()

            return retrofit.create(BDService::class.java)
        }
    }
}

