package hk.com.lolamove.domain.usecase

import hk.com.lolamove.domain.usecase.result.GetListOfDeliveriesResult
import kotlinx.coroutines.flow.Flow

interface GetListOfDeliveriesUseCase {
    operator fun invoke(
        offset: Int,
        limit: Int,
    ): Flow<GetListOfDeliveriesResult>
}