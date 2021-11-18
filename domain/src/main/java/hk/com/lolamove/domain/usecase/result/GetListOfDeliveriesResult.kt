package hk.com.lolamove.domain.usecase.result

import hk.com.lolamove.domain.datamodels.Delivery

sealed class GetListOfDeliveriesResult {

    object Loading: GetListOfDeliveriesResult()

    data class Success(
        val deliveries: List<Delivery>
    ): GetListOfDeliveriesResult()

    sealed class Error: GetListOfDeliveriesResult() {

        class Exception(val error: Throwable): Error()

    }

}
