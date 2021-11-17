package hk.com.lolamove.domain.datamodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Sender(
    val phone: String? = null,
    val name: String? = null,
    val email: String? = null,
): Parcelable
