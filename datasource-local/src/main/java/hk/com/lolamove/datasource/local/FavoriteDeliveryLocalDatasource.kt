package hk.com.lolamove.datasource.local

import hk.com.lolamove.datasource.local.roomdb.LolaMoveDatabase
import hk.com.lolamove.datasource.local.roomdb.dao.FavoriteDeliveryDao
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.datasource.local.roomdb.dto.mapper.DeliveryMapper
import hk.com.lolamove.datasource.local.roomdb.dto.mapper.FavoriteDeliveryMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteDeliveryLocalDatasource(
    private val database: LolaMoveDatabase,
) {

    private val favoriteDao: FavoriteDeliveryDao = database.favoriteDeliveryDao()

    fun getAllFavoriteDeliveries(): Flow<List<Delivery>> =
        favoriteDao.getAllFavoriteDeliveries()
            .map { favorites ->
                favorites.map(DeliveryMapper::map)
            }

    fun isFavorite(id: String): Flow<Boolean> =
        favoriteDao.isFavorite(id)

    suspend fun addToFavorites(vararg delivery: Delivery) {
        favoriteDao.insert(
            *delivery.map(FavoriteDeliveryMapper::map)
                .toTypedArray()
        )
    }

    suspend fun removeFromFavorites(id: String) {
        favoriteDao.delete(id)
    }

}