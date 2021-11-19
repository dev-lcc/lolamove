package hk.com.lolamove.data.usecase

import hk.com.lolamove.datasource.local.FavoriteDeliveryLocalDatasource
import hk.com.lolamove.domain.usecase.ObserveDeliveryIsFavoriteUseCase
import kotlinx.coroutines.flow.Flow

class ObserveDeliveryIsFavoriteUseCaseImpl(
    private val favoritesLocalDatasource: FavoriteDeliveryLocalDatasource,
): ObserveDeliveryIsFavoriteUseCase {
    override fun invoke(id: String): Flow<Boolean> =
        favoritesLocalDatasource.isFavorite(id)
}