package com.task.test.bdswiss.api

import android.content.Context
import com.task.test.bdswiss.models.RatesModel

import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MockService {

    @GET("/test/rates")
    fun getRates(): Observable<RatesModel>

    companion object {
        fun create(context: Context): MockService {

            val client = OkHttpClient.Builder()
                    .addInterceptor(FakeInterceptor(context))
                    .build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://mock.api")
                    .client(client)
                    .build()

            return retrofit.create(MockService::class.java)
        }
    }

}