package hk.com.lolamove.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DTODelivery(
    val id: String? = null,
    val remarks: String? = null,
    val pickupTime: String? = null, // ISODate
    val goodsPicture: String? = null,   // String URL
    val deliveryFee: String? = null,    // ex. "$95.10"
    val surcharge: String? = null,  // ex. "$5.99"
    val route: DTORoute? = null,
    val sender: DTOSender? = null,
)