package hk.com.lolamove.domain.usecase

interface RemoveDeliveryFromFavoritesUseCase {
    suspend operator fun invoke(deliveryId: String)
}