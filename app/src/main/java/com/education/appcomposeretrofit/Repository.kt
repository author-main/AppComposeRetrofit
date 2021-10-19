package com.education.appcomposeretrofit

import androidx.lifecycle.MutableLiveData
import com.education.appcomposeretrofit.weather.WeatherApi
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class Repository {
  /*  enum class Result {
        COMPLETE, ERROR
    }*/
    private val apiInterface: WeatherApi.ApiInterface? = WeatherApi.getRetrofitApi()
    private val forecastToday: MutableLiveData<WeatherDay> by lazy {
        MutableLiveData<WeatherDay>()
    }
    private val forecastWeek: MutableLiveData<WeatherForecast> by lazy {
        MutableLiveData<WeatherForecast>()
    }
    /*var onGetForecastToday: ((result: Result) -> Unit)? = null
    var onGetForecastWeek: ((result: Result) -> Unit)? = null*/

    fun getForecastToday(lat: Double, lon: Double){
        val callToday: Call<WeatherDay>? = apiInterface?.getToday(lat, lon, "metric", WeatherApi.key)
        callToday?.enqueue(object: Callback<WeatherDay> {
            override fun onResponse(call: Call<WeatherDay>?, response: Response<WeatherDay>?) {
                response?.let{
                    if (it.isSuccessful)
                      //  onGetForecastToday?.invoke(Result.COMPLETE)

                }
            }

            override fun onFailure(call: Call<WeatherDay>?, t: Throwable?) {
              //  onGetForecastToday?.invoke(Result.ERROR)
            }
        })

    }

    fun getForecastWeek(lat: Double, lon: Double){
        val callForecast: Call<WeatherForecast>? = apiInterface?.getForecast(lat, lon, "metric", WeatherApi.key)
        callForecast?.enqueue(object: Callback<WeatherForecast>{
            override fun onResponse(call: Call<WeatherForecast>?, response: Response<WeatherForecast>?) {
                response?.let{
                    if (it.isSuccessful)
                      //  onGetForecastWeek?.invoke(Result.COMPLETE)
                }
            }
            override fun onFailure(call: Call<WeatherForecast>?, t: Throwable?) {
              // onGetForecastWeek?.invoke(Result.ERROR)
            }
        })
    }

}