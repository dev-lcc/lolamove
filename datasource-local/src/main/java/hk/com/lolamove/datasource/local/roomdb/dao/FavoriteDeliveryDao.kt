package hk.com.lolamove.datasource.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hk.com.lolamove.datasource.local.roomdb.dto.DTOFavoriteDelivery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class FavoriteDeliveryDao {

    @Query("SELECT * FROM favorites")
    abstract fun getAllFavoriteDeliveries(): Flow<List<DTOFavoriteDelivery>>

    @Query("SELECT * FROM favorites WHERE id = :id")
    protected abstract fun internalGetFavoriteDelivery(id: String): Flow<List<DTOFavoriteDelivery>>

    fun isFavorite(id: String): Flow<Boolean> =
        internalGetFavoriteDelivery(id)
            .map { list -> list.isNotEmpty() }

    @Insert(entity = DTOFavoriteDelivery::class, onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(vararg delivery: DTOFavoriteDelivery)

    @Query("DELETE FROM favorites WHERE id = :id")
    abstract suspend fun delete(id: String)

}