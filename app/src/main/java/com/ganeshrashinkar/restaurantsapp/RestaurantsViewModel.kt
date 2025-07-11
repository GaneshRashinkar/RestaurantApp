package com.ganeshrashinkar.restaurantsapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.ganeshrashinkar.restaurantsapp.api.Businesse
import com.ganeshrashinkar.restaurantsapp.api.RestaurantsData
import com.ganeshrashinkar.restaurantsapp.model.LocationData
import com.ganeshrashinkar.restaurantsapp.model.RestaurantPageSource
import com.ganeshrashinkar.restaurantsapp.model.RestaurantsRepository
import com.ganeshrashinkar.restaurantsapp.util.AppConstants
import com.ganeshrashinkar.restaurantsapp.util.Resource
import kotlinx.coroutines.launch
import okhttp3.Response

class RestaurantsViewModel(): ViewModel() {
    var locationEnabled by mutableStateOf(false)
    var sliderPosition by  mutableStateOf(10f)
    private val _location= mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    fun updateLocation(location: LocationData?){
        Log.e("TAG","inside viewmodel : $location")
        locationEnabled=true
        _location.value=location

    }
    val repository= RestaurantsRepository()
    val restaurantFlow=Pager(PagingConfig(pageSize = AppConstants.PAGE_LIMIT)){
        RestaurantPageSource(repository,this)
    }.flow.cachedIn(viewModelScope)
}

class RestaurantViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RestaurantsViewModel::class.java)){
            return RestaurantsViewModel() as T
        }
        return super.create(modelClass)
    }
}