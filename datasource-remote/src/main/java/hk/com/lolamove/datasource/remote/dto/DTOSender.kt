package hk.com.lolamove.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DTOSender(
    val phone: String? = null,
    val name: String? = null,
    val email: String? = null,
)
