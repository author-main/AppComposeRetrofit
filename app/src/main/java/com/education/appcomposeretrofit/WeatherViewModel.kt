package com.education.appcomposeretrofit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import kotlinx.coroutines.*
import java.net.URL

class WeatherViewModel(private val repository: Repository) : ViewModel(){

    val isRefreshing: LiveData<Boolean>  by lazy {
        repository.isRefreshing()
    }


    val forecastToday: LiveData<WeatherDay> by lazy {
        repository.getDataToday()
    }

    val forecastWeek: LiveData<WeatherForecast> by lazy {
        repository.getDataWeek()
    }

    val forecastWeekMore: LiveData<WeatherForecast> by lazy {
        repository.getDataWeekMore()
    }

    fun setLocation(lan: Double, lon: Double) {
        repository.setLocation(lan, lon)
        updateForecast()
    }

    fun updateForecast(){
         viewModelScope.launch {
             repository.updateForecast()
         }
     }

}