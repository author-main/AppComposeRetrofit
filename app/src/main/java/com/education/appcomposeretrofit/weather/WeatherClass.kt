package com.education.appcomposeretrofit.weather

import com.google.gson.annotations.SerializedName
import java.util.*

//key weather dc2e12a90c095c2ef1f98c5ef4b613e5
/************* JSON ответ сервера ************

@SerializedName("name"), name - ключ JSON, связывание переменной класса с переменной JSON
@Expose,                 разрешена сериализация/десериализация, по умолчанию true (можно не указывать)
@Expose(deserialize = true), @Expose(serialize = true)

http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=dc2e12a90c095c2ef1f98c5ef4b613e5

{
"coord": {
"lon": -0.1257,
"lat": 51.5085
},
"weather": [
{
"id": 803,
"main": "Clouds",
"description": "broken clouds",
"icon": "04d"
}
],
"base": "stations",
"main": {
"temp": 289.67,
"feels_like": 289.86,
"temp_min": 288.34,
"temp_max": 290.35,
"pressure": 1012,
"humidity": 95
},
"visibility": 3800,
"wind": {
"speed": 1.79,
"deg": 70,
"gust": 4.02
},
"clouds": {
"all": 75
},
"dt": 1631606680,
"sys": {
"type": 2,
"id": 2019646,
"country": "GB",
"sunrise": 1631597640,
"sunset": 1631643501
},
"timezone": 3600,
"id": 2643743,
"name": "London",
"cod": 200
}

 */

class WeatherDay {
    private val sourceIconUrl = "http://openweathermap.org/img/w/"
    class WeatherTemp {
        // свойства объекта "main"
        var temp: Double? = null
        var tempMin: Double? = null
        var tempMax: Double? = null
    }

    class WeatherDescription{
        // свойство объекта "weather"
        var icon: String? = null
    }

    // значения объекта "main" будут сохранены в классе WeatherTemp
    @SerializedName("main")
    private var temp: WeatherTemp? = null

    // значения объекта "weather" будут сохранены в классе WeatherDescription (список icon)
    @SerializedName("weather")
    private var description: List<WeatherDescription>? = null

    // свойство "name"
    @SerializedName("name")
    private var city: String? = null

    // свойство "dt"
    @SerializedName("dt")
    private var timestamp: Long = 0

    /* init{
         this.temp = temp
         this.description = description
     }*/

    fun getDate(): Calendar {
        val date = Calendar.getInstance()
        date.timeInMillis = timestamp * 1000
        return date
    }

    fun getTemp() =
        temp?.temp.toString()

    fun getTempInteger() =
        temp?.temp?.toInt()?.toString()

    fun getTempWithDegree() =
        temp?.temp?.toInt()?.toString()

    fun getTempMin() =
        temp?.tempMin.toString()

    fun getTempMax() =
        temp?.tempMax.toString()

    fun getCity() =
        city

    fun getIcon() =
        description?.get(0)?.icon

    fun getIconUrl() =
        sourceIconUrl + description?.get(0)?.icon + ".png"

}

class WeatherForecast{
    private var items: List<WeatherDay>? = null
    fun getItems() =
        items
}