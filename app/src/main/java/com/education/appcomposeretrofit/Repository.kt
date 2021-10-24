package com.education.appcomposeretrofit

import androidx.lifecycle.MutableLiveData
import com.education.appcomposeretrofit.weather.WeatherApi
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt

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
              log("${t?.message}")
            }
        })

    }

    private fun getForecastWeek(){
        val callForecast: Call<WeatherForecast>? = apiInterface?.getForecast(lat, lon, "metric", WeatherApi.key)
        callForecast?.enqueue(object: Callback<WeatherForecast>{
            override fun onResponse(call: Call<WeatherForecast>?, response: Response<WeatherForecast>?) {
                val calendar = Calendar.getInstance()
                fun getHour(timestamp: Long): Int{
                    calendar.timeInMillis = timestamp * 1000
                    return calendar.get(Calendar.HOUR_OF_DAY)
                }
                /*fun getDay(timestamp: Long): Int{
                    calendar.timeInMillis = timestamp * 1000
                    return calendar.get(Calendar.DAY_OF_MONTH)
                }
                fun getMax(items: List<WeatherDay>, indexFrom: Int, indexTo: Int): Double {
                    var max = items[indexFrom].getTempMax()
                    for (i in indexFrom..indexTo) {
                        if (items[i].getTempMax() > max)
                            max = items[i].getTempMax()
                    }
                    return max
                }

                fun getMin(items: List<WeatherDay>, indexFrom: Int, indexTo: Int): Double {
                    var min = items[indexFrom].getTempMin()
                    for (i in indexFrom..indexTo) {
                        if (items[i].getTempMin() < min)
                            min = items[i].getTempMin()
                    }
                    return min
                }*/

                response?.let{ it ->
                    if (it.isSuccessful) {
                        val data = it.body()
                        val hour = 10//calendar.get(Calendar.HOUR_OF_DAY)
                        val minute  = 29//calendar.get(Calendar.MINUTE)
                        val time = hour * 60 + minute
                        var segment = -1
                        for (i in 0..7) {
                            val range = i * 3 * 60 .. (i + 1) * 3 * 60
                           // val half = i * 3 * 60 + 90
                            if (time in range) {
                              /*  if (time < half) {
                                    segment = i * 3
                                    break
                                }
                                else {*/
                                    segment = if (i + 1 == 8)
                                                0
                                              else
                                                (i + 1) * 3
                                    break;
                                //}
                            }
                        }

                        data?.getItems()?.let { items ->
                            val list = mutableListOf<WeatherDay>()
                            for (i in items.indices){
                               val itemHour = getHour(items[i].getTimeStamp())
                                if (itemHour == segment) {
                                    list.add(items[i])
                                }
                            }
                            val forecast = WeatherForecast()
                            forecast.setItems(list)
                            forecastWeek.postValue(forecast)
                        }
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