package com.education.appcomposeretrofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    val dataWeekMore: WeatherForecast by viewModel
        .forecastWeekMore
        .observeAsState(WeatherForecast())

    Column(modifier = Modifier
        .fillMaxSize()) {
            Today(dataToday, dataWeekMore)
            DaysOfWeek(dataWeek.getItems())
        }
}

@Composable
fun Today(data: WeatherDay, dataHour: WeatherForecast){
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colors.primaryVariant,
                    MaterialTheme.colors.primary
                )
            )
        )
        .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(modifier = Modifier
            .wrapContentSize(),
            color = Color.White,
            text = data.getCity()
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
            text = "${stringResource(R.string.feel_like)}${data.getFeelLike()}",
        )


            LazyRow(
                modifier = Modifier
                    // .height(150.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(end = 32.dp)
                    ) {
                        RowWPH(index = 0, data = data)
                        RowWPH(index = 1, data = data)
                        RowWPH(index = 2, data = data)
                    }
                }
                dataHour.getItems()?.let { itemsHour ->
                    items(itemsHour) { item ->
                        item.getTime()
                        ColumnForecastHour(item)
                    }
                }
            }


       /* Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painterResource(R.drawable.ic_wind),
                alpha = 0.7f,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.wrapContentSize()
            )
        }*/
    }

}

@Composable
fun ColumnForecastHour(item: WeatherDay){
    val textColor = Color(255,255,255,200)
    Column(horizontalAlignment = Alignment.CenterHorizontally,
           modifier = Modifier.padding(horizontal = 8.dp)
        ) {
        Text(modifier = Modifier.padding(vertical = 8.dp),
            text = item.getTime(),
            color = textColor,
            fontSize = 13.sp
        )
        GlideImage(
            imageModel = item.getIconUrl(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(30.dp)
                .height(30.dp)

        )
        Text(modifier = Modifier.padding(vertical = 8.dp),
            text = item.getTempWithDegree(),
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

    }
}

@Composable
fun RowWPH(index: Int, data: WeatherDay?){
    val drawableId = arrayOf(
        R.drawable.ic_wind,
        R.drawable.ic_pressure,
        R.drawable.ic_humidity,
    )

    val value = data?.let {
        when (index) {
            0 -> {
                "${data.getWindSpeed()} ${stringResource(R.string.speed)}, ${data.getWindDeg()}\u00b0"
            }
            1 -> {
                "${data.getPressure()} ${stringResource(R.string.pressure)}"
            }
            2 -> {
                "${data.getHumidity()}%"
            }
            else -> null
        }
    }
    if (!value.isNullOrBlank()){
        Row(modifier = Modifier
            .padding(vertical = 8.dp)
            .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
            ){
            Image(
                painterResource(drawableId[index]),
                alpha = 0.7f,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(end = 8.dp)
            )
            Text(
                modifier = Modifier.wrapContentSize(),
                color = Color.White,
                fontSize = 13.sp,
                text = value
            )

        }
    }
}

@Composable
fun DaysOfWeek(data: List<WeatherDay>?){
    data?.let {items ->
        LazyColumn( modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
                itemsIndexed(items) { index, item, -> RowOfDay(item, index)
                if (index < items.size-1)
                    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
          }

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


    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(vertical = 4.dp)

        ) {
            Text(
                modifier = Modifier.wrapContentSize(),
                color = Color.LightGray,
                fontSize = 12.sp,
                text = item.getDate()
            )
            Text(
                modifier = Modifier.wrapContentSize(),
                color = color,
                text = textDayWeek
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
            ) {
            GlideImage(
                imageModel = item.getIconUrl(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )
            Text(
                modifier = Modifier
                    .width(60.dp),
                textAlign = TextAlign.Right,
                color = Color.DarkGray,
                //fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                text = item.getTempWithDegree()
            )
            Text(
                modifier = Modifier
                    .width(50.dp),
                textAlign = TextAlign.Right,
                color = Color(120,120,120),
                fontSize = 18.sp,
                text = item.getFeelLike()
            )
        }

    }

}

/*@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppComposeRetrofitTheme {
        Greeting("Android")
    }
}*/