package hk.com.lolamove.domain.usecase

import hk.com.lolamove.domain.datamodels.Delivery
import kotlinx.coroutines.flow.Flow

interface GetDeliveryFavoritesUseCase {
    operator fun invoke(): Flow<List<Delivery>>
}