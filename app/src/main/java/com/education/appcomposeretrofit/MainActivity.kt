package com.education.appcomposeretrofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.education.appcomposeretrofit.ui.theme.AppComposeRetrofitTheme
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import com.skydoves.landscapist.glide.GlideImage

class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels(factoryProducer = {
        FactoryViewModel(
            this,
            (application as AppComposeRetrofit).repository
        )
    })
  //  val todayTemp = mutableStateOf(WeatherDay())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppComposeRetrofitTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Screen(viewModel)
                }
            }
        }
    }
}

@Composable
fun Screen(viewModel: WeatherViewModel){
    val dataToday: WeatherDay by viewModel
        .forecastToday
        .observeAsState(WeatherDay())

    val dataWeek: WeatherForecast by viewModel
        .forecastWeek
        .observeAsState(WeatherForecast())

    Column(modifier = Modifier
        .fillMaxSize()) {
            Today(dataToday)
            DaysOfWeek(dataWeek.getItems())
        }


        //Text(text = "temperatureToday ${dataToday.getTemp()}\ntemperatureWeek ${dataWeek.getItems()?.get(0)?.getTemp()}")
}

@Composable
fun Today(data: WeatherDay){
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colors.primary),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(modifier = Modifier
            .wrapContentSize()
            .padding(top = 16.dp),
            color = Color.White,
            text = "${data.getCity()}"
        )
        
        Row(modifier = Modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.wrapContentSize(),
                color = Color.White,
                style = MaterialTheme.typography.h1,
                text = data.getTempWithDegree()
            )
            GlideImage(
                imageModel = data.getIconUrl(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
            )
        }

        Text(modifier = Modifier.wrapContentSize(),
            color = Color.White,
            text = data.getDescription()
        )

        Text(modifier = Modifier
            .wrapContentSize()
            .padding(bottom = 16.dp),
            color = Color(255,255,255,200),
            text = "Ощущается как: ${data.getFeelLike()}",
        )

    }

}

@Composable
fun DaysOfWeek(data: List<WeatherDay>?){
    data?.let {items ->
        LazyColumn( modifier = Modifier.fillMaxSize()
            .padding(16.dp)
        ) {
            itemsIndexed(items) { index, item, -> RowOfDay(item, index) }
        }
    }
}

@Composable
fun RowOfDay(item: WeatherDay, index: Int){
    val dayWeek = arrayOf("сегодня", "завтра")
    val color = if (item.isWeekend())
            Color.Red
        else
            Color.DarkGray
    val textDayWeek = if (index > 1)
            item.getDayWeek()
        else
            dayWeek[index]



    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            modifier = Modifier.wrapContentSize(),
            color = Color.LightGray,
            fontSize = 14.sp,
            text = item.getDate()
        )
        Text(
            modifier = Modifier.wrapContentSize(),
            color = color,
            text = textDayWeek
            //.padding(start = 16.dp, end = 16.dp),
        )
    }
}

/*@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppComposeRetrofitTheme {
        Greeting("Android")
    }
}*/