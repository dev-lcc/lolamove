package hk.com.lolamove.datasource.local.roomdb.dto

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = DTOFavoriteDelivery.DB_TABLE_NAME,
)
data class DTOFavoriteDelivery(
    @PrimaryKey
    val id: String,
    val remarks: String? = null,
    val pickupTime: String? = null, // ISODate
    val goodsPicture: String? = null,   // String URL
    val deliveryFee: Float? = null,
    val surcharge: Float? = null,
    @Embedded(prefix = "route_")
    val route: DTORoute? = null,
    @Embedded(prefix = "sender_")
    val sender: DTOSender? = null,
    val currencySymbol: String? = null, // i.e. "$"
) {
    companion object {
        const val DB_TABLE_NAME = "favorites"
    }
}
