package com.education.appcomposeretrofit

import androidx.lifecycle.MutableLiveData
import com.education.appcomposeretrofit.weather.WeatherApi
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Repository {
    private val lat = 48.192638
    private val lon = 41.283229
    private val apiInterface: WeatherApi.ApiInterface? = WeatherApi.getRetrofitApi()
    private val forecastToday: MutableLiveData<WeatherDay> by lazy {
        MutableLiveData<WeatherDay>()
    }
    private val forecastWeek: MutableLiveData<WeatherForecast> by lazy {
        MutableLiveData<WeatherForecast>()
    }
    fun getDataToday(): MutableLiveData<WeatherDay> =
        forecastToday

    fun getDataWeek(): MutableLiveData<WeatherForecast> =
        forecastWeek


    private fun getForecastToday(){
        val callToday: Call<WeatherDay>? = apiInterface?.getToday(lat, lon, "metric", WeatherApi.key)
        callToday?.enqueue(object: Callback<WeatherDay> {
            override fun onResponse(call: Call<WeatherDay>?, response: Response<WeatherDay>?) {
                response?.let{
                    if (it.isSuccessful)
                        forecastToday.postValue(it.body())
                }
            }

            override fun onFailure(call: Call<WeatherDay>?, t: Throwable?) {
                log("error day")
                log("${t?.message}")
            }
        })

    }

    private fun getForecastWeek(){
        val callForecast: Call<WeatherForecast>? = apiInterface?.getForecast(lat, lon, "metric", WeatherApi.key)
        callForecast?.enqueue(object: Callback<WeatherForecast>{
            override fun onResponse(call: Call<WeatherForecast>?, response: Response<WeatherForecast>?) {
                val calendar = Calendar.getInstance()
                fun getDay(timestamp: Long): Int{
                    calendar.timeInMillis = timestamp * 1000
                    return calendar.get(Calendar.DAY_OF_MONTH)
                }
                response?.let{ it ->
                    if (it.isSuccessful) {
                        val data = it.body()
                        data?.getItems()?.let{items ->
                            var before = 0
                            val day = getDay(items[0].getTimeStamp())
                            for (i in items.indices)
                                if (getDay(items[i].getTimeStamp()) != day) {
                                    before = i
                                    break
                                }
                            log ("before $before")
                        }

                        forecastWeek.postValue(it.body())
                    }
                }
            }
            override fun onFailure(call: Call<WeatherForecast>?, t: Throwable?) {
                log("${t?.message}")
            }
        })
    }

    fun updateForecast(){
        getForecastToday()
        getForecastWeek()
    }

}