package com.education.appcomposeretrofit.weather

import com.google.gson.annotations.SerializedName
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherDay{
    private val calendar = Calendar.getInstance()
    private val sourceIconUrl = "http://openweathermap.org/img/w/"

    class WeatherWind{
        var speed: Double? = null
        var deg  : Int? = null
    }

    class WeatherTemp {
        var temp: Double? = null
        var feels_like: Double? = null
        var pressure: Int? = null
        var humidity: Int? = null
    }

    class WeatherDescription{
        var description: String? = null
        var icon: String? = null
    }

    @SerializedName("main")
    private var temp: WeatherTemp? = null

    @SerializedName("wind")
    private var wind: WeatherWind? = null

    @SerializedName("weather")
    private var description: List<WeatherDescription>? = null

    @SerializedName("name")
    private var city: String? = null

    @SerializedName("dt")
    private var timestamp: Long = 0

    fun getWindSpeed() = wind?.speed?.toString() ?: ""
    fun getWindDeg() = wind?.deg ?: 0


    fun getDate(short: Boolean = false): String {
        val pattern = if (short)
                            "dd MMM"
                        else
                            "dd MMMM"
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(timestamp * 1000)
    }

    fun getTime(): String {
        val pattern = "HH:mm"
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(timestamp * 1000)
    }


    fun getDayWeek(): String{
        val format = SimpleDateFormat("EEEE", Locale.getDefault())
        return if (timestamp == 0L)
            ""
        else
            format.format(timestamp * 1000)
    }

    fun isWeekend(): Boolean {
        calendar.timeInMillis = timestamp * 1000
        val day: Int = calendar.get(Calendar.DAY_OF_WEEK)
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY
    }

    fun getTimeStamp() = timestamp

    fun getPressure(): String {
        return temp?.pressure?.let{
            (it * 0.750063755419211).roundToInt().toString()
        } ?: ""
    }



    fun getHumidity() = temp?.humidity?.toString() ?: ""

    fun getFeelLike() =
        getTempDegree(temp?.feels_like)

    private fun getTempDegree(value: Double?): String{
        var wc = ""
        return value?.let{
            if (it > 0)
                wc = "+"
            wc + it.toInt() + "\u00b0"
        } ?: wc
    }

    fun getTempWithDegree() =
        getTempDegree(temp?.temp)


    fun getCity(): String {
        return city ?: ""
    }


    fun getDescription(): String  {
        val note = description?.get(0)?.description ?: ""
        return if (note.isNotEmpty())
            note.replaceFirstChar {note[0].uppercase()}
        else ""
    }

    fun getIconUrl() =
        sourceIconUrl + description?.get(0)?.icon + ".png"

}

class WeatherForecast {
    @SerializedName("list")
    private var items: List<WeatherDay>? = null

    fun getItems() =
        items

    fun setItems(list: List<WeatherDay>) {
        items = list
    }
}