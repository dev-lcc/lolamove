package hk.com.lolamove.data.usecase

import hk.com.lolamove.datasource.local.FavoriteDeliveryLocalDatasource
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.domain.usecase.GetDeliveryFavoritesUseCase
import kotlinx.coroutines.flow.Flow

class GetDeliveryFavoritesUseCaseImpl(
    private val favoritesLocalDatasource: FavoriteDeliveryLocalDatasource,
): GetDeliveryFavoritesUseCase {
    override fun invoke(): Flow<List<Delivery>> =
        favoritesLocalDatasource.getAllFavoriteDeliveries()
}