package hk.com.lolamove.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DTORoute(
    val start: String? = null,
    val end: String? = null,
)
