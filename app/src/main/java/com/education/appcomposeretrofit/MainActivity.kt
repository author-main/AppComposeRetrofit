package com.education.appcomposeretrofit
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.education.appcomposeretrofit.ui.theme.AppComposeRetrofitTheme
import com.education.appcomposeretrofit.weather.WeatherDay
import com.education.appcomposeretrofit.weather.WeatherForecast
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: WeatherViewModel by viewModels(factoryProducer = {
        FactoryViewModel(
            this,
            (application as AppComposeRetrofit).repository
        )
    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionSetup()
        setContent {
            AppComposeRetrofitTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Screen(viewModel)
                }
            }
        }
    }

    private fun toast(text: String){
        val spannable: Spannable =  SpannableString(text)
        spannable.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0, text.length - 1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        Toast.makeText(this, spannable, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getLocation(){
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        viewModel.setLocation(it.latitude, it.longitude)
                    }
                }
                .addOnFailureListener {
                    toast(resources.getText(R.string.error_location).toString())
                }
        } catch (ex: SecurityException) {
            toast(resources.getText(R.string.grant_location).toString())
        }
    }


    private fun permissionSetup() {
        val permission = ContextCompat.checkSelfPermission(
            baseContext, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getLocation()
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        when (it) {
            true -> { getLocation() }
            false -> {
                toast(resources.getText(R.string.grant_location).toString())
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

    val isRefreshing by viewModel.isRefreshing.observeAsState(false)
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { viewModel.updateForecast() },
        indicator = {state, dp ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = dp,
                contentColor = Color(150,0,0)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Today(dataToday, dataWeekMore)
            DaysOfWeek(dataWeek.getItems())
        }
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
        .verticalScroll(ScrollState(0))
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
        HourLazyRow(data, dataHour)

    }

}


@Composable
fun HourLazyRow(dataDay: WeatherDay, dataHour: WeatherForecast){
    val listState = rememberLazyListState(0)
    val indexVisible = remember {
        listState.firstVisibleItemIndex
    }

    LazyRow( modifier = Modifier
        .height(150.dp)
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
        state = listState,
        verticalAlignment = Alignment.CenterVertically
    ) {
      //  log("indexVisible $indexVisible")
        if (indexVisible !=0)
        CoroutineScope(Dispatchers.Main).launch {
        //    log("sctollTo $indexVisible")
            listState.scrollToItem(indexVisible)
        }
        item {
            Column(
                modifier = Modifier
                    .padding(end = 32.dp)
                    .height(150.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                RowWPH(index = 0, data = dataDay)
                RowWPH(index = 1, data = dataDay)
                RowWPH(index = 2, data = dataDay)
            }
        }
        dataHour.getItems()?.let { itemsHour ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = itemsHour[0].getTimeStamp() * 1000
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            var count = 0
            var indexLast = 0
            for (i in itemsHour.indices){
                calendar.timeInMillis = itemsHour[i].getTimeStamp() * 1000
                val itemDay = calendar.get(Calendar.DAY_OF_MONTH)
                if (day != itemDay) {
                    day = itemDay
                    count++
                    if (count > 2) {
                        indexLast = i - 1
                        break
                    }
                }

            }

            itemsIndexed(itemsHour) { index, item ->
                if (index <= indexLast)
                    ColumnForecastHour(item, index)
            }

        }
    }
}


@Composable
fun ColumnForecastHour(item: WeatherDay, index: Int){
    val textColor = Color(255,255,255,200)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = item.getTimeStamp() * 1000
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    Column(horizontalAlignment = Alignment.CenterHorizontally,
           modifier = Modifier.padding(horizontal = 8.dp)
        ) {
        Text(
            text = item.getTime(),
            color = textColor,
            fontSize = 13.sp
        )
        val textDay = if (index == 0) {
                        stringResource(id = R.string.now)
                      }
                      else {
                        if (hour == 0) {
                            item.getDate(short = true)
                        }
                        else
                            ""
                      }
        Text(
            modifier = Modifier.offset(y = (-4).dp),
            text = textDay,
            color = textColor,
            fontSize = 13.sp
        )
        GlideImage(
            imageModel = item.getIconUrl(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(40.dp)
                .height(40.dp)
        )
        Text(
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

        var arrayDirection = arrayOf<Array<Float>>()
        for (i in 0..15) {
            val array = arrayOf<Float>(i * 22.5f, (i + 1) * 22.5f)
            arrayDirection += array
        }

        val degree = data.getWindDeg().toFloat()
        var indexDirection = ""
        for (i in 0..15) {
            if (degree >= arrayDirection[i][0] && degree < arrayDirection[i][1]) {
                indexDirection = stringArrayResource(id = R.array.direction)[i]
                break
            }
        }

        when (index) {
            0 -> {
                "${data.getWindSpeed()} ${stringResource(R.string.speed)}, $indexDirection"//, ${data.getWindDeg()}\u00b0"
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
        Row(
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
            data.let {
                if (index == 0) {
                    Image(
                        painterResource(R.drawable.ic_arrow),
                        alpha = 0.7f,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .rotate(data.getWindDeg().toFloat())
                    )
                }
            }

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
                itemsIndexed(items) { index, item, -> RowOfDay(item)
                if (index < items.size-1)
                    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .1f))
          }
        }
    }
}

@Composable
fun RowOfDay(item: WeatherDay){
    val dayWeek = stringArrayResource(id = R.array.days)
    val color = if (item.isWeekend())
            Color.Red
        else
            Color.DarkGray
    val calendar = Calendar.getInstance()
    val now = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.add(Calendar.DATE, 1)
    val nextday = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.timeInMillis = item.getTimeStamp() * 1000
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val textDayWeek = if (now == day)
        dayWeek[0]
    else {
        if (day == nextday)
            dayWeek[1]
        else
            item.getDayWeek()
    }
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