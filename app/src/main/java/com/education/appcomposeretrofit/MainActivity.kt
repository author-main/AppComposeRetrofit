package com.education.appcomposeretrofit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.education.appcomposeretrofit.ui.theme.AppComposeRetrofitTheme
import com.education.appcomposeretrofit.weather.WeatherApi
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWeather()
        setContent {
            AppComposeRetrofitTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }

    private fun getWeather(){
        val lat = 48.192638
        val lon = 41.283229
        val units = "metric"
        val key: String = WeatherApi.key
        val weatherApi = WeatherApi.getRetrofitApi()
        val callToday: Call<WeatherDay>? = weatherApi?.getToday(lat, lon, units, key)
        callToday?.enqueue(object: Callback<WeatherDay> {
            override fun onResponse(call: Call<WeatherDay>?, response: Response<WeatherDay>?) {
                response?.let{
                    if (it.isSuccessful){
                        val weatherToday = it.body()
                        Today(it.body())
                        //log("${weatherToday?.getDate()}")
                    }
                }
            }

            override fun onFailure(call: Call<WeatherDay>?, t: Throwable?) {
                // showError()
            }
        })

        val callForecast: Call<WeatherForecast>? = weatherApi?.getForecast(lat, lon, units, key)
        callForecast?.enqueue(object: Callback<WeatherForecast>{
            override fun onResponse(call: Call<WeatherForecast>?, response: Response<WeatherForecast>?) {
                response?.let{
                    if (it.isSuccessful){
                        val weatherForecast = it.body()

                        log("${weatherForecast?.getItems()?.size}")
                    }
                }
            }

            override fun onFailure(call: Call<WeatherForecast>?, t: Throwable?) {
                //  showError()
            }
        })
    }


}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun Today(temp: WeatherDay){

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppComposeRetrofitTheme {
        Greeting("Android")
    }
}