package com.education.appcomposeretrofit

import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.education.appcomposeretrofit.ui.theme.AppComposeRetrofitTheme
import com.education.appcomposeretrofit.weather.WeatherApi
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import com.skydoves.landscapist.glide.GlideImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    Today(dataToday)
    DaysOfWeek(dataWeek.getItems())

        //Text(text = "temperatureToday ${dataToday.getTemp()}\ntemperatureWeek ${dataWeek.getItems()?.get(0)?.getTemp()}")
}

@Composable
fun Today(data: WeatherDay){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp)
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
                    .width(50.dp)
                    .height(50.dp)
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
            text = "Ощущается как: ${data.getFeelLike()}"
        )

    }

}

@Composable
fun DaysOfWeek(data: List<WeatherDay>?){
    //Text(text = "temperatureWeek ${data?.get(0)?.getTemp()}")
}

@Composable
fun RowOfDay(data: WeatherDay){

}

/*@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppComposeRetrofitTheme {
        Greeting("Android")
    }
}*/