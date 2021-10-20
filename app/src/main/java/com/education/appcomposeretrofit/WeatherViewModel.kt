package com.education.appcomposeretrofit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast

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