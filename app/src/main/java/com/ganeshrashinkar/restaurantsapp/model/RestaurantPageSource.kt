package com.ganeshrashinkar.restaurantsapp.model

import android.util.Log
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ganeshrashinkar.restaurantsapp.RestaurantsViewModel
import com.ganeshrashinkar.restaurantsapp.api.Businesse
import com.ganeshrashinkar.restaurantsapp.util.AppConstants
import kotlin.math.roundToInt

class RestaurantPageSource(val repository: RestaurantsRepository,val viewModel: RestaurantsViewModel): PagingSource<Int, Businesse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Businesse> {
        try {
            if(viewModel.locationEnabled){
                var nextPageNumber=params.key?:1
                Log.e("TAG","page: $nextPageNumber")
                val response=repository.getRestaurantsWithLocation(
                    nextPageNumber,
                    viewModel.location.value!!.latitude,
                    viewModel.location.value!!.longitude,
                    AppConstants.PAGE_LIMIT,
                    viewModel.sliderPosition.roundToInt()
                )
                if(response.isSuccessful){
                    //if we get empty data that means end of pages
                    var nextPage:Int?=1
                    if (response.body()?.businesses?.isEmpty()?:true){
                        nextPage=null
                    }else{
                        nextPage=nextPageNumber+1
                    }
                    return LoadResult.Page(
                        data = response.body()?.businesses?:listOf(),
                        prevKey = null,
                        nextKey = nextPage
                    )
                }else
                {
                    Log.e("TAg","${response.errorBody()}__${response.message()}__${response.body()}")
                    return LoadResult.Error(Exception(response.message()))
                }
            }else{
            val nextPageNumber=params.key?:1
            Log.e("TAG","page: $nextPageNumber")
            val response=repository.getRestaurantsWithDefaultLocation(nextPageNumber)
            if(response.isSuccessful){
                //if we get empty data that means end of pages
                var nextPage:Int?=1
                if (response.body()?.businesses?.isEmpty()?:true){
                    nextPage=null
                }else{
                    nextPage=nextPageNumber+1
                }
                return LoadResult.Page(
                    data = response.body()?.businesses?:listOf(),
                    prevKey = null,
                    nextKey = nextPage
                )
            }else
            {
                Log.e("TAg","${response.errorBody()}__${response.message()}__${response.body()}")
                return LoadResult.Error(Exception(response.message()))
            }
            }
        }catch (e: Exception){
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Businesse>): Int? {
        Log.e("TAG","anchorposition= ${state.anchorPosition}")
        return (state.anchorPosition?:0)+1
    }

}