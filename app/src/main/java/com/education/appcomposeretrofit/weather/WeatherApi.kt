package com.education.appcomposeretrofit.weather

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class WeatherApi {
    interface ApiInterface{
        @GET("weather")
        fun getToday(
            @Query("lat")
            lat: Double,
            @Query("lon")
            lon: Double,
            @Query("units")
            units: String,
            @Query("appid")
            appid: String,
            @Query("lang")
            lang: String = "ru"): Call<WeatherDay>
        @GET("forecast")
        fun getForecast(
            @Query("lat")
            lat: Double,
            @Query("lon")
            lon: Double,
            @Query("units")
            units: String,
            @Query("appid")
            appid: String,
            @Query("lang")
            lang: String = "ru"): Call<WeatherForecast>
    }

    companion object{
        const val key = "dc2e12a90c095c2ef1f98c5ef4b613e5"
        private const val url = "http://api.openweathermap.org/data/2.5/"
        private val retrofit: Retrofit? = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private var apiInterface: ApiInterface? = retrofit?.create(ApiInterface::class.java)
        fun getRetrofitApi() = apiInterface
    }
}