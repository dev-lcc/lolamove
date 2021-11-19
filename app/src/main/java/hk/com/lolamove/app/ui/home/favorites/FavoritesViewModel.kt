package hk.com.lolamove.app.ui.home.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.domain.usecase.GetDeliveryFavoritesUseCase
import hk.com.lolamove.domain.usecase.RemoveDeliveryFromFavoritesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getDeliveryFavoritesUseCase: GetDeliveryFavoritesUseCase,
    private val removeDeliveryFromFavoritesUseCase: RemoveDeliveryFromFavoritesUseCase,
): ViewModel() {

    private val _viewState = MutableStateFlow(ViewState.initial())
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    private val _singleEvent = MutableSharedFlow<SingleEvent>(extraBufferCapacity = 4)
    val singleEvent: SharedFlow<SingleEvent> = _singleEvent.asSharedFlow()

    val intent = object: ViewIntent {
        override fun removeFromFavorites(which: Delivery) {
            viewModelScope.launch {
                val deliveryId = which.id ?: return@launch
                removeDeliveryFromFavoritesUseCase(deliveryId)
            }
        }

        override fun viewDetails(of: Delivery) {
            _singleEvent.tryEmit(SingleEvent.NavigateToDeliveryDetails(of))
        }
    }

    init {
        // Subscribe to Favorites List
        getDeliveryFavoritesUseCase()
            .onEach { favorites: List<Delivery> ->
                val vs = _viewState.value
                // Update ViewState
                _viewState.value = vs.copy(
                    emptyList = favorites.isEmpty(),
                    items = favorites,
                )
            }
            .launchIn(viewModelScope)
    }

}

data class ViewState(
    // [true] if no item(s) to display. Otherwise, [false].
    val emptyList: Boolean,
    val items: List<Delivery> = listOf(),
) {
    companion object {
        fun initial(): ViewState =
            ViewState(
                emptyList = false,
            )
    }
}

interface ViewIntent {
    /**
     * Remove selected delivery from favorite(s).
     * - Invoked on toggle item favorite.
     */
    fun removeFromFavorites(which: Delivery)

    /**
     * Navigate to Delivery Detail.
     * - Invoked on tap item.
     */
    fun viewDetails(of: Delivery)

}

sealed class SingleEvent {
    /**
     * Dispatched on invoke viewDetails()
     */
    data class NavigateToDeliveryDetails(val item: Delivery): SingleEvent()
}
