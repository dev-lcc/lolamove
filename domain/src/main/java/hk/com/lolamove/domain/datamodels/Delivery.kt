package hk.com.lolamove.domain.datamodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Delivery(
    val id: String? = null,
    val remarks: String? = null,
    val pickupTime: String? = null, // ISODate
    val goodsPicture: String? = null,   // String URL
    val deliveryFee: Float? = null,
    val surcharge: Float? = null,
    val route: Route? = null,
    val sender: Sender? = null,
): Parcelable
