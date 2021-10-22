package com.education.appcomposeretrofit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import kotlinx.coroutines.*
import java.net.URL

class WeatherViewModel(private val repository: Repository) : ViewModel(){
    val forecastToday: LiveData<WeatherDay> by lazy {
        repository.getDataToday()
    }

    val forecastWeek: LiveData<WeatherForecast> by lazy {
        repository.getDataWeek()
    }

    init{
        repository.updateForecast()
    }

  /*  fun getImageFromUrl(value: String?) {
        value?.let{
            var image: Bitmap? = null
            val url = URL(it)
            val stream = url.openConnection().getInputStream()
            viewModelScope.launch {
                image = withContext(Dispatchers.IO) {
                    val bitmap = BitmapFactory.decodeStream(stream)
                    Bitmap.createScaledBitmap(bitmap, 100, 100, true)
                }
            }
        }
    }*/

}