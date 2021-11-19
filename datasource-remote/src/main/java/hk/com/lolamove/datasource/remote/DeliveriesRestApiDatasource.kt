package hk.com.lolamove.datasource.remote

import hk.com.lolamove.datasource.remote.dto.mapper.DeliveryMapper
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.datasource.remote.retrofit.services.DeliveriesRetrofitService
import retrofit2.HttpException

class DeliveriesRestApiDatasource(
    private val deliveriesRetrofitService: DeliveriesRetrofitService,
) {

    /**
     * Protocol HTTPS
     * Hostname mock-api-mobile.dev.lalamove.com
     * Method GET
     * Endpoint /v2/deliveries
     * Query String Parameters
     * @param   offset  Starting index
     * @param   limit  Number of items requested
     *
     * @return  List of deliveries
     */
    suspend fun getListOfDeliveries(
        offset: Int,
        limit: Int,
    ): List<Delivery> {

        val response = deliveriesRetrofitService.getListOfDeliveries(
            offset = offset,
            limit = limit,
        )

        if(response.isSuccessful) {
            val data = response.body()!!
            // Map to Domain
            return data.map(DeliveryMapper::map)
        } else {
            throw HttpException(response).also { it.printStackTrace() }
        }
    }

}