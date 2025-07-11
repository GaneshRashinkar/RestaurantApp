package com.ganeshrashinkar.restaurantsapp.api

import com.ganeshrashinkar.restaurantsapp.util.AppConstants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RestaurantsApi {

    @Headers("Authorization: Bearer $API_KEY")
    @GET("businesses/search")
    suspend fun getRestaurantsWithDefaultLocation(
        @Query("location")
        location: String,
        @Query("sort_by")
        sort_by: String,
        @Query("limit")
        limit: Int,
        @Query("offset")
        offset: Int
    ): Response<RestaurantsData>

    @Headers("Authorization: Bearer $API_KEY")
    @GET("businesses/search")
    suspend fun getRestaurantWithLocation(
        @Query("latitude")
        latitude: Double,
        @Query("longitude")
        longitude: Double,
        @Query("term")
        term:String,
        @Query("radius")
        radius:Int,
        @Query("sort_by")
        sort_by: String,
        @Query("limit")
        limit:Int,
        @Query("offset")
        offset: Int
    ): Response<RestaurantsData>
}