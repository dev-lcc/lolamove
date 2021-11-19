package hk.com.lolamove.domain.usecase

import kotlinx.coroutines.flow.Flow

interface ObserveDeliveryIsFavoriteUseCase {
    operator fun invoke(id: String): Flow<Boolean>
}