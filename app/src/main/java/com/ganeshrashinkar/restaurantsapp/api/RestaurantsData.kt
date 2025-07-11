package com.ganeshrashinkar.restaurantsapp.api

data class RestaurantsData(
    val businesses: List<Businesse>,
    val region: Region,
    val total: Int
)