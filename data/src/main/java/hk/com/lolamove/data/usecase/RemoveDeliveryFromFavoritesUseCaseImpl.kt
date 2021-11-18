package hk.com.lolamove.data.usecase

import hk.com.lolamove.datasource.local.FavoriteDeliveryLocalDatasource
import hk.com.lolamove.domain.usecase.RemoveDeliveryFromFavoritesUseCase

class RemoveDeliveryFromFavoritesUseCaseImpl(
    private val favoritesLocalDatasource: FavoriteDeliveryLocalDatasource,
) : RemoveDeliveryFromFavoritesUseCase {
    override suspend fun invoke(deliveryId: String) {
        // Fire and forget. Subscribers to RoomDB `favorites` will be notified of the changes.
        favoritesLocalDatasource.removeFromFavorites(id = deliveryId)
    }
}