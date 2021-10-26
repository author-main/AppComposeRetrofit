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
    }

    fun updateForecast(){
         repository.updateForecast()
     }

    /*class ImageLoader(builder: Builder){
        private val image: Bitmap? = null
        fun getImage() = image
        companion object Builder{
            private var path: String? = null
            private var image: Bitmap? = null
            fun path(value: String?): Builder{
                path = value
                return this
            }

            fun into(value: Bitmap): Builder{
                image = value
                getImageFromUrl()
                return this
            }

            fun build(): ImageLoader {
                return ImageLoader(this)
            }

            private fun getImageFromUrl() {
               val scope = CoroutineScope(Dispatchers.Main + Job())
               val url = URL(path)
               val stream = url.openStream()
               scope.launch {
                   withContext(Dispatchers.IO) {
                       val urlBitmap = BitmapFactory.decodeStream(stream)
                       image = Bitmap.createScaledBitmap(urlBitmap, 50, 50, true)
                   }
                }
            }
        }
    }*/

}