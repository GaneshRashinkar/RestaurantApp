package com.ganeshrashinkar.restaurantsapp.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ganeshrashinkar.restaurantsapp.RestaurantsViewModel
import com.ganeshrashinkar.restaurantsapp.model.LocationData
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Job


class LocationUtils(val context: Context){

    private val _fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    //use flag to check previous request is still processing
    var processingRequest=false


    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(locationViewModel: RestaurantsViewModel){
        //do not add new request until previous request completes
        if(!processingRequest) {
            processingRequest=true
            val locationRequest = CurrentLocationRequest.Builder().build()

            val location = _fusedLocationClient.getCurrentLocation(locationRequest, null)
            location.addOnFailureListener { processingRequest=false }
            location.addOnSuccessListener { location ->
                processingRequest=false
                location?.let {
                    val locationData = LocationData(it.latitude, it.longitude)
                    locationViewModel.updateLocation(locationData)
                }
            }
        }
        Log.e("TAG","location : loction request added")
    }
    fun hasLocationPermission(): Boolean{
        return ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED

    }
}