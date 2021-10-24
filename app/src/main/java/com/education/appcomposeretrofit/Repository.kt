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
              /*  fun getDay(timestamp: Long): Int{
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
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute  = calendar.get(Calendar.MINUTE)
                        val time = hour * 60 + minute
                        for (i in 1..8) {
                            val endHour = i * 3 * 60
                            log ("hour $endHour -> time $time")

                        }

                        log("$hour:$minute")
                      /*  data?.getItems()?.let{items ->
                            val list = mutableListOf<WeatherDay>()
                            list.add(items[0])
                            var day = getDay(items[0].getTimeStamp())
                            for (i in items.indices)
                                if (getDay(items[i].getTimeStamp()) != day) {
                                    day = getDay(items[i].getTimeStamp())
                                    list.add(items[i])
                                }

                            list.forEach {item ->
                               day = getDay(item.getTimeStamp())
                               var start = -1
                                var last = false
                               for (i in items.indices){
                                   val matched = getDay(items[i].getTimeStamp()) == day
                                   if (matched && start == -1)
                                       start = i

                                   if (matched && start != -1 && i == items.size - 1)
                                       last = true


                                   if ((!matched && start != -1) || last){
                                       val index = if (last)
                                                        items.size - 1
                                                    else
                                                        i - 1
                                       val min = getMin(items, start, index)
                                       val max = getMax(items, start, index)
                                       item.setTempMax(max)
                                       item.setTempMin(min)
                                       break
                                   }
                               }

                            }





                            val forecast = WeatherForecast()
                            forecast.setItems(list)
                            forecastWeek.postValue(forecast)
                        }*/
                        forecastWeek.postValue(data)
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