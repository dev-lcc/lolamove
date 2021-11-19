package hk.com.lolamove.app.ui.home.deliveries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import hk.com.lolamove.app.LolaMoveBaseFragment
import hk.com.lolamove.app.R
import hk.com.lolamove.app.databinding.FragmentDeliveriesBinding
import hk.com.lolamove.app.ui.deliverydetails.DeliveryDetailsFragment
import hk.com.lolamove.app.ui.home.deliveries.adapter.ItemDeliveriesAdapter
import hk.com.lolamove.app.utils.EndlessRecyclerViewScrollListener
import hk.com.lolamove.app.utils.flow.throttleFirst
import hk.com.lolamove.domain.datamodels.Delivery
import hk.com.lolamove.domain.usecase.result.GetListOfDeliveriesResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.swiperefreshlayout.refreshes
import timber.log.Timber

class DeliveriesFragment: LolaMoveBaseFragment() {

    private val binding: FragmentDeliveriesBinding by viewBinding(FragmentDeliveriesBinding::bind)
    private val viewModel: DeliveriesViewModel by viewModel()

    private var adapter: ItemDeliveriesAdapter? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null

    private var errorFetchDeliveriesSnackbar: Snackbar? = null
    private val fetchDeliveriesNextPage = MutableSharedFlow<Int>(extraBufferCapacity = 4)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_deliveries, container, false)

    override fun onDestroyView() {
        // De-init
        binding.rvDeliveries.adapter = null
        adapter = null

        if(scrollListener != null) {
            binding.rvDeliveries.removeOnScrollListener(scrollListener!!)
        }
        scrollListener = null
        errorFetchDeliveriesSnackbar?.dismiss()
        errorFetchDeliveriesSnackbar = null

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setNestedScrollingEnabled(binding.rvDeliveries, true)

        // Swipe Refresh
        binding.swipeRefresh.isRefreshing = false
        binding.swipeRefresh.refreshes()
            .throttleFirst(1000L)
            .onEach {
                // Attempt Refresh
                viewModel.intent.refresh()
            }
            .launchIn(lifecycleScope)

        // Setup Adapter
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvDeliveries.layoutManager = layoutManager
        adapter = ItemDeliveriesAdapter(
            onTapItem = { delivery: Delivery ->
                Timber.d("onTapItem::delivery = $delivery")
                // Attempt View Details
                viewModel.intent.viewDetails(of = delivery)
            },
            onToggleFavorite = { isChecked: Boolean, delivery: Delivery ->
                Timber.d("onToggleFavorite::delivery = $delivery")
                // Add/Remove favorite
                when(isChecked) {
                    true -> viewModel.intent.addToFavorites(which = delivery)
                    false -> viewModel.intent.removeFromFavorites(which = delivery)
                }
            }
        )
        binding.rvDeliveries.itemAnimator = null
        binding.rvDeliveries.adapter = adapter

        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Timber.d("scrollListener::onLoadMore() -> page/totalItemsCount = $page / $totalItemsCount")
                Timber.d("scrollListener::onLoadMore() -> findLastVisibleItemPosition = ${layoutManager.findLastVisibleItemPosition()}")
                Timber.d("scrollListener::onLoadMore() -> itemCount = ${view?.adapter?.itemCount ?: 0}")
                // Attempt perform fetch next page
                fetchDeliveriesNextPage.tryEmit(page)
            }
        }

        // Manage execution of fetch matches next page by applying debounce to avoid duplicate
        fetchDeliveriesNextPage
            .debounce(250)
            .onEach { page ->
                Timber.d("fetchMatchesNextPage -> page = $page")
                // Attempt Fetch More
                viewModel.intent.fetchDeliveriesNextPage()
            }
            .launchIn(lifecycleScope)

        ////////////////////////////////////
        // Observe ViewState
        ////////////////////////////////////
        viewModel.viewState
            .onEach(::render)
            .launchIn(lifecycleScope)

        ////////////////////////////////////
        // Observe Single Event(s)
        ////////////////////////////////////
        viewModel.singleEvent
            .onEach(::takeSingleEvent)
            .launchIn(lifecycleScope)

    }

    private suspend fun render(viewState: ViewState) {
        Timber.d("render() -> viewState = $viewState")

        // Progress Indicator(s)
        binding.swipeRefresh.isRefreshing = viewState.isLoading
        adapter?.setLoading(isLoading = viewState.isLoadingMore)

        // List of Deliveries
        if(viewState.emptyList) {
            // HIDE List and DISPLAY Empty Label
            binding.rvDeliveries.visibility = View.GONE
            binding.containerEmptyState.visibility = View.VISIBLE

            binding.appBarLayout.setExpanded(false)
        } else {
            // REVEAL List
            binding.containerEmptyState.visibility = View.GONE
            binding.rvDeliveries.visibility = View.VISIBLE

            adapter?.replace(viewState.items)
            binding.rvDeliveries.clearOnScrollListeners()
            if (viewState.hasMoreToFetch && scrollListener != null) {
                binding.rvDeliveries.addOnScrollListener(scrollListener!!)
            }
        }

    }

    private suspend fun takeSingleEvent(event: SingleEvent) {
        Timber.d("takeSingleEvent() -> event = $event")

        when(event) {
            is SingleEvent.ErrorFetchListOfDeliveries -> {
                val errMsg = when(event.error) {
                    is GetListOfDeliveriesResult.Error.Exception -> event.error.error.message
                } ?: getString(R.string.something_went_wrong_please_try_again)

                errorFetchDeliveriesSnackbar?.dismiss()
                errorFetchDeliveriesSnackbar?.setText(errMsg)
                errorFetchDeliveriesSnackbar?.show()
            }
            is SingleEvent.ErrorFetchListOfDeliveriesNextPage -> {
                val errMsg = when(event.error) {
                    is GetListOfDeliveriesResult.Error.Exception -> event.error.error.message
                } ?: getString(R.string.something_went_wrong_please_try_again)

                errorFetchDeliveriesSnackbar?.dismiss()
                errorFetchDeliveriesSnackbar?.setText(errMsg)
                errorFetchDeliveriesSnackbar?.show()
            }
            is SingleEvent.NavigateToDeliveryDetails -> {
                // Navigate To Delivery Details
                if(appNavController.currentDestination?.id == R.id.homeFragment) {
                    appNavController.navigate(
                        R.id.action_homeFragment_to_deliveryDetailsFragment,
                        DeliveryDetailsFragment.createInputArguments(
                            item = event.item
                        )
                    )
                }
            }
        }
    }

}