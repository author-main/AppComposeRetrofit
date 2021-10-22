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

}

/*
private var image: Bitmap? = null
 private fun getBitmapFromURL(src: String?) {
    CoroutineScope(Job() + Dispatchers.IO).launch {
        try {
            val url = URL(src)
            val bitMap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            image = Bitmap.createScaledBitmap(bitMap, 100, 100, true)
        } catch (e: IOException) {
            // Log exception
        }
    }
}
 */