package com.ganeshrashinkar.restaurantsapp.model

import com.ganeshrashinkar.restaurantsapp.util.AppConstants
import com.ganeshrashinkar.restaurantsapp.api.RetrofitInstance
import com.google.gson.internal.NumberLimits

class RestaurantsRepository {
    suspend fun getRestaurantsWithDefaultLocation(pageNumber:Int)= RetrofitInstance.api.getRestaurantsWithDefaultLocation(
        location = "New York City",
        sort_by = "best_match",
        limit = AppConstants.PAGE_LIMIT,
        offset = pageNumber
    )

    suspend fun getRestaurantsWithLocation(
        pageNumber:Int,
        latitude: Double,
        longitude:Double,
        limit:Int,
        radius: Int
    )= RetrofitInstance.api.getRestaurantWithLocation(
        latitude=latitude,
        longitude=longitude,
        offset=pageNumber,
        limit = limit,
        term="restaurants",
        radius = radius,
        sort_by = "best_match"
    )
}
