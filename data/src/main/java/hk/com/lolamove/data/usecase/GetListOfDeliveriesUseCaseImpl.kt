package hk.com.lolamove.data.usecase

import hk.com.lolamove.datasource.remote.DeliveriesRestApiDatasource
import hk.com.lolamove.domain.usecase.GetListOfDeliveriesUseCase
import hk.com.lolamove.domain.usecase.result.GetListOfDeliveriesResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GetListOfDeliveriesUseCaseImpl(
    private val deliveriesRestApiDatasource: DeliveriesRestApiDatasource,
): GetListOfDeliveriesUseCase {
    override fun invoke(offset: Int, limit: Int): Flow<GetListOfDeliveriesResult> = flow {
        val isInitialFetch = offset == 0

        emit(
            when(isInitialFetch) {
                true -> GetListOfDeliveriesResult.Loading
                false -> GetListOfDeliveriesResult.LoadingMore
            }
        )

        val response = withContext(Dispatchers.IO) {
            deliveriesRestApiDatasource.getListOfDeliveries(
                offset = offset,
                limit = limit,
            )
        }

        if(isInitialFetch && response.isEmpty()) {
            // EMIT Empty
            emit(GetListOfDeliveriesResult.Empty)
        } else {
            // EMIT Success
            emit(
                GetListOfDeliveriesResult.Success(deliveries = response)
            )
        }
    }
        .retryWhen { cause, attempt ->
            // Attempt retry(at most 3x) when Network or I/O Error encountered
            (/*cause::class.java !in listOf(
            InvalidAuthTokenError::class.java,
        ) && */attempt < 3)
                // Apply Exponential Back-off Delay
                .also {
                    cause.printStackTrace()
                    delay(2000L * attempt)
                }
        }.catch { err ->
            // EMIT Error
            emit(
                when(err) {
                    else -> GetListOfDeliveriesResult.Error.Exception(err)
                }
            )
        }
}