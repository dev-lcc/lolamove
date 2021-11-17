package hk.com.lolamove.domain.datamodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Route(
    val start: String? = null,
    val end: String? = null,
): Parcelable
