package hk.com.lolamove.domain.usecase

import hk.com.lolamove.domain.datamodels.Delivery

interface AddDeliveryToFavoritesUseCase {
    suspend operator fun invoke(vararg deliveries: Delivery)
}