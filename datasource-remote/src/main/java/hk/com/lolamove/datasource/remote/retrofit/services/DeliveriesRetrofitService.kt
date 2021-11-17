package hk.com.lolamove.datasource.remote.retrofit.services

import retrofit2.Response
import retrofit2.http.GET
import hk.com.lolamove.datasource.remote.dto.DTODelivery as DTODelivery

interface DeliveriesRetrofitService {

    @GET("v2/deliveries")
    suspend fun getListOfDeliveries(
        offset: Int,
        limit: Int,
    ): Response<List<DTODelivery>>

}