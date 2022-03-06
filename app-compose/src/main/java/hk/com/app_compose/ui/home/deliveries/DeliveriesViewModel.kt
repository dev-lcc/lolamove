package hk.com.app_compose.ui.home.deliveries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.domain.usecase.AddDeliveryToFavoritesUseCase
import hk.com.lolamove.domain.usecase.GetDeliveryFavoritesUseCase
import hk.com.lolamove.domain.usecase.GetListOfDeliveriesUseCase
import hk.com.lolamove.domain.usecase.RemoveDeliveryFromFavoritesUseCase
import hk.com.lolamove.domain.usecase.result.GetListOfDeliveriesResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeliveriesViewModel(
    private val getListOfDeliveriesUseCase: GetListOfDeliveriesUseCase,
    private val getDeliveryFavoritesUseCase: GetDeliveryFavoritesUseCase,
    private val addDeliveryToFavoritesUseCase: AddDeliveryToFavoritesUseCase,
    private val removeDeliveryFromFavoritesUseCase: RemoveDeliveryFromFavoritesUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ViewState.initial())
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    private val _singleEvent = MutableSharedFlow<SingleEvent>(extraBufferCapacity = 4)
    val singleEvent: SharedFlow<SingleEvent> = _singleEvent.asSharedFlow()

    val intent = object : ViewIntent {
        override fun refresh() {
            viewModelScope.launch {
                offsetStart = PAGINATION_INITIAL_START
                doPerformFetchDeliveries(offsetStart)
            }
        }

        override fun fetchDeliveriesNextPage() {
            if (_viewState.value.hasMoreToFetch) {
                viewModelScope.launch {
                    doPerformFetchDeliveries(offsetStart)
                }
            }
        }

        override fun addToFavorites(which: Delivery) {
            viewModelScope.launch {
                addDeliveryToFavoritesUseCase(which)
            }
        }

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

    // Placeholder variable to store favorite(s)
    private var favorites = listOf<Delivery>()

    init {
        viewModelScope.launch {
            doPerformFetchDeliveries(offset = PAGINATION_INITIAL_START)
        }

        // Observer Favorite(s) List
        getDeliveryFavoritesUseCase()
            .onEach { favorites ->
                this@DeliveriesViewModel.favorites = favorites
                val vs = _viewState.value
                // Update ViewState
                _viewState.value = vs.copy(
                    items = vs.items.map { item ->
                        item.copy(
                            // Item Lookup to derive `isFavorite`
                            isFavorite = favorites.any { fav -> fav.id == item.id }
                        )
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    private var offsetStart: Int = PAGINATION_INITIAL_START

    private suspend fun doPerformFetchDeliveries(offset: Int) {
        getListOfDeliveriesUseCase(
            offset = offset,
            limit = PAGE_LIMIT
        )
            .collect { result ->
                val vs = _viewState.value
                val isFetchMore = offset > 0

                // Update ViewState
                _viewState.value = when (result) {
                    is GetListOfDeliveriesResult.Loading -> vs.copy(isLoading = true)
                    is GetListOfDeliveriesResult.LoadingMore -> vs.copy(isLoadingMore = true)
                    is GetListOfDeliveriesResult.Empty ->
                        vs.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            emptyList = true,
                            items = listOf(),
                        )
                    is GetListOfDeliveriesResult.Success ->
                        vs.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            emptyList = false,
                            hasMoreToFetch = result.deliveries.isNotEmpty(),
                            items = when (isFetchMore) {
                                // If result from Fetch More, append result to list
                                true -> vs.items + result.deliveries
                                // Else, replace list
                                false -> result.deliveries
                            }.map { item ->
                                item.copy(
                                    // Item Lookup to derive `isFavorite`
                                    isFavorite = favorites.any { fav -> fav.id == item.id }
                                )
                            },
                        )
                            .also {
                                // Update Pagination Offset
                                offsetStart = it.items.size
                            }
                    is GetListOfDeliveriesResult.Error ->
                        vs.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            emptyList = false,
                        )
                            .also {
                                // EMIT Error
                                _singleEvent.emit(
                                    when (isFetchMore) {
                                        true -> SingleEvent.ErrorFetchListOfDeliveriesNextPage(result)
                                        false -> SingleEvent.ErrorFetchListOfDeliveries(result)
                                    }
                                )
                            }
                }
            }
    }

    companion object {
        const val PAGINATION_INITIAL_START = 0
        const val PAGE_LIMIT = 10
    }

}

data class ViewState(
    // Progress Indicator(s)
    val isLoading: Boolean,
    val isLoadingMore: Boolean,
    // [true] if most recent item(s) returned is empty.
    val hasMoreToFetch: Boolean,
    // [true] if no item(s) to display. Otherwise, [false].
    val emptyList: Boolean,
    val items: List<Delivery> = listOf(),
) {
    companion object {
        fun initial(): ViewState =
            ViewState(
                isLoading = false,
                isLoadingMore = false,
                hasMoreToFetch = true,
                emptyList = false,
            )
    }
}

interface ViewIntent {
    /**
     * Perform Fetch List of Deliveries
     * - Invoked on screen load or swipe refresh
     */
    fun refresh()

    /**
     * Perform Fetch List Of Deliveries Operation(Paginated)
     * - Invoked on scroll to bottom of list
     */
    fun fetchDeliveriesNextPage()

    /**
     * Add selected delivery to favorite(s).
     * - Invoked on toggle item favorite.
     */
    fun addToFavorites(which: Delivery)

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
     * Dispatched when error encountered during Fetch Deliveries operations.
     */
    data class ErrorFetchListOfDeliveries(val error: GetListOfDeliveriesResult.Error) :
        SingleEvent()

    /**
     * Dispatched when error encountered during Fetch Deliveries(Next Page) operations.
     */
    data class ErrorFetchListOfDeliveriesNextPage(val error: GetListOfDeliveriesResult.Error) :
        SingleEvent()

    /**
     * Dispatched on invoke viewDetails()
     */
    data class NavigateToDeliveryDetails(val item: Delivery): SingleEvent()
}