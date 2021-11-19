package hk.com.lolamove.app.ui.deliverydetails

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.domain.usecase.AddDeliveryToFavoritesUseCase
import hk.com.lolamove.domain.usecase.ObserveDeliveryIsFavoriteUseCase
import hk.com.lolamove.domain.usecase.RemoveDeliveryFromFavoritesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DeliveryDetailsViewModel(
    val item: Delivery,
    private val observeDeliveryIsFavoriteUseCase: ObserveDeliveryIsFavoriteUseCase,
    private val addDeliveryToFavoritesUseCase: AddDeliveryToFavoritesUseCase,
    private val removeDeliveryFromFavoritesUseCase: RemoveDeliveryFromFavoritesUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ViewState.initial(item))
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    private val _singleEvent = MutableSharedFlow<SingleEvent>(extraBufferCapacity = 4)
    val singleEvent: SharedFlow<SingleEvent> = _singleEvent.asSharedFlow()

    val intent = object : ViewIntent {
        override fun toggleFavorites() {
            viewModelScope.launch {
                val vs = _viewState.value

                when(vs.isFavorite) {
                    // Already added, NOW perform REMOVE
                    true -> {
                        val deliveryId = item.id ?: return@launch
                        removeDeliveryFromFavoritesUseCase(deliveryId)
                    }
                    // NOT yet added. NOW perform ADD
                    false -> {
                        addDeliveryToFavoritesUseCase(item)
                    }
                }
            }
        }
    }

    init {
        item.id?.let { deliveryId ->
            observeDeliveryIsFavoriteUseCase(deliveryId)
                .onEach { isFavorite: Boolean ->
                    val vs = _viewState.value
                    // Update ViewState
                    _viewState.value = vs.copy(
                        isFavorite = isFavorite
                    )
                }
                .launchIn(viewModelScope)
        }
    }

}

data class ViewState(
    val senderName: String? = null,
    val senderPhoneNumber: String? = null,
    val senderEmail: String? = null,
    val deliveryPhotoUrl: String? = null,
    val remarks: String? = null,
    val pickupTime: Date? = null,
    val deliveryFee: Float? = null,
    val surcharge: Float? = null,
    val totalFee: Float? = null,
    val currencySymbol: String? = null,
    val routeFrom: String? = null,
    val routeTo: String? = null,
    val isFavorite: Boolean = false,
) {
    companion object {
        fun initial(
            item: Delivery,
        ): ViewState =
            ViewState(
                senderName = item.sender?.name,
                senderPhoneNumber = item.sender?.phone,
                senderEmail = item.sender?.email,
                deliveryPhotoUrl = item.goodsPicture,
                remarks = item.remarks,
                pickupTime = item.pickupTime?.let { pickupTime ->
                    try {
                        UTC_DATE_FORMATTER.parse(pickupTime)
                    } catch (err: ParseException) {
                        null
                    }
                },
                deliveryFee = item.deliveryFee,
                surcharge = item.surcharge,
                totalFee = (item.deliveryFee ?: 0f) + (item.surcharge ?: 0f),
                currencySymbol = item.currencySymbol,
                routeFrom = item.route?.start,
                routeTo = item.route?.end,
                isFavorite = item.isFavorite == true,
            )

        @SuppressLint("NewApi")
        internal val UTC_DATE_FORMATTER: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
                .apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
    }
}

interface ViewIntent {

    /**
     * If already added to favorites, perform REMOVE from Favorites.
     * If NOT yet, perform ADD from Favorites
     * - Invoked on toggle favorite.
     */
    fun toggleFavorites()

}

sealed class SingleEvent {
    // TODO:: Define other possible SingleEvent constants in here...
}