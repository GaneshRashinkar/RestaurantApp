package com.ganeshrashinkar.restaurantsapp

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.ganeshrashinkar.restaurantsapp.ui.theme.RestaurantsAppTheme
import com.ganeshrashinkar.restaurantsapp.util.LocationUtils
import com.ganeshrashinkar.restaurantsapp.views.RestaurantItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val restaurantsViewModel: RestaurantsViewModel by viewModels{
        RestaurantViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       // restaurantsViewModel.fetchRestaurantsData()
        setContent {
            RestaurantsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RestaurantsScreen(modifier=Modifier.padding(innerPadding))
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RestaurantsScreen(modifier: Modifier= Modifier){
        val locationutils= LocationUtils(this@MainActivity)
        val requestPermissionLaunchr= rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                    permissions->
                if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true
                    &&
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true
                ){
                    //have access to both locations
                    locationutils.requestLocationUpdates(restaurantsViewModel)
                }
                else{
                    restaurantsViewModel.locationEnabled=false
                    //ask for permission
                    val rationaleRequired= ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,Manifest.permission.ACCESS_COARSE_LOCATION
                    )||ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,Manifest.permission.ACCESS_FINE_LOCATION
                    )

                    if(rationaleRequired) {
                        Toast.makeText(
                            this@MainActivity,
                            "For using this feature permission is required",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else{
                        Toast.makeText(
                            this@MainActivity,
                            "Location permission is required, Please enable it from settings",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )

        var isRefreshing by remember { mutableStateOf(false) }
        val restaurantFlow =restaurantsViewModel.restaurantFlow.collectAsLazyPagingItems()
        val loadState=restaurantFlow.loadState.refresh

        Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = restaurantsViewModel.locationEnabled, onCheckedChange = {
                    if(!restaurantsViewModel.locationEnabled){
                        Log.e("TAG","location : if")
                        if(locationutils.hasLocationPermission()){
                            locationutils.requestLocationUpdates(restaurantsViewModel)
                            CoroutineScope(Dispatchers.Main).launch{
                                delay(1500)
                                restaurantFlow.refresh()
                            }

                            //restaurantsViewModel.locationEnabled=!restaurantsViewModel.locationEnabled
                        }else{
                            Log.e("TAG","location : else")
                            requestPermissionLaunchr.launch(arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ))
                        }
                    }
                    else{
                        restaurantsViewModel.locationEnabled=false
                    }

                })
                Spacer(Modifier.width(8.dp))
                Text("Search By Location",fontWeight = FontWeight.Bold)
            }
            Spacer(modifier=Modifier.height(12.dp))
            Row(modifier= Modifier.fillMaxWidth()) {
                Text("Radius Selector", fontWeight = FontWeight.Bold)
                Spacer(modifier=Modifier.weight(1f))
                if(restaurantsViewModel.sliderPosition<1000f)
                    Text("${restaurantsViewModel.sliderPosition.toInt()} Meters", fontWeight = FontWeight.Bold)
                else
                    Text("${((restaurantsViewModel.sliderPosition/100).toInt()).toFloat()/10} KM", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier=Modifier.height(12.dp))
            Slider(
                value = restaurantsViewModel.sliderPosition,
                onValueChange = { restaurantsViewModel.sliderPosition = it },
                valueRange = 0f..5000f,
                steps = 500,
                modifier = Modifier.fillMaxWidth().height(5.dp).padding(horizontal = 16.dp),
                track = {sliderState->
                    Box(modifier= Modifier.fillMaxWidth().height(2.dp).background(Color.Black))
                }
            )
            Spacer(Modifier.height(12.dp))
            Row(modifier=Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("0 m")
                Spacer(modifier=Modifier.weight(1f))
                Text("5000 m")
            }
            Spacer(modifier=Modifier.width(8.dp))
        PullToRefreshBox(modifier = Modifier.weight(1f),isRefreshing = isRefreshing, onRefresh = {
            restaurantFlow.refresh()
        }) {
            if(loadState is LoadState.NotLoading){
                Log.e("TAG","not loading")
                isRefreshing=false
            }
            if(loadState is LoadState.Loading){
                Log.e("TAG","loading")
                isRefreshing=true
                Box(Modifier.fillMaxSize())
            }
            else if(loadState is LoadState.Error){
                Log.e("TAG","load state error")
            isRefreshing=false
            LaunchedEffect(key1 = true) {
                delay(2000)
                restaurantFlow.retry()
            }
            Column(modifier = modifier.fillMaxSize()
                .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Text("Error to fetch data")
            }

        }
        else {
                isRefreshing=false
                Log.e("TAG","load state success")
                if(restaurantFlow.itemCount==0){
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,

                        ) {
                        Text("No results for this location")
                    }
                }
                else {
                    LazyColumn() {
                        items(restaurantFlow.itemCount) {
                            val business = restaurantFlow[it]!!
                            RestaurantItem(business)
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }
        }
        }
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val activeNetwork=connectivityManager.activeNetwork?: return false
        val capabilities=connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
        return when{
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else->false
        }
    }
}

