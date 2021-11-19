package hk.com.lolamove.datasource.remote.retrofit.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import hk.com.lolamove.datasource.remote.dto.DTODelivery as DTODelivery

interface DeliveriesRetrofitService {

    @GET("v2/deliveries")
    suspend fun getListOfDeliveries(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Response<List<DTODelivery>>

}