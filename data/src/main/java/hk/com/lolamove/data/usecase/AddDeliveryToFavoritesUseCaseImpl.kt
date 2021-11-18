package hk.com.lolamove.data.usecase

import hk.com.lolamove.datasource.local.FavoriteDeliveryLocalDatasource
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.domain.usecase.AddDeliveryToFavoritesUseCase

class AddDeliveryToFavoritesUseCaseImpl(
    private val favoritesLocalDatasource: FavoriteDeliveryLocalDatasource,
): AddDeliveryToFavoritesUseCase {
    override suspend fun invoke(vararg deliveries: Delivery) {
        // Fire and forget. Subscribers to RoomDB `favorites` will be notified of the changes.
        favoritesLocalDatasource.addToFavorites(*deliveries)
    }
}