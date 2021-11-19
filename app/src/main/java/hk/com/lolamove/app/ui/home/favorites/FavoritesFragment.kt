package hk.com.lolamove.app.ui.home.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import hk.com.lolamove.app.LolaMoveBaseFragment
import hk.com.lolamove.app.R
import hk.com.lolamove.app.databinding.FragmentFavoritesBinding
import hk.com.lolamove.app.ui.deliverydetails.DeliveryDetailsFragment
import hk.com.lolamove.app.ui.home.favorites.adapter.ItemFavoritesAdapter
import hk.com.lolamove.domain.datamodels.Delivery
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavoritesFragment: LolaMoveBaseFragment() {

    private val binding: FragmentFavoritesBinding by viewBinding(FragmentFavoritesBinding::bind)
    private val viewModel: FavoritesViewModel by viewModel()

    private var adapter: ItemFavoritesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onDestroyView() {
        // De-init
        binding.rvFavorites.adapter = null
        adapter = null

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setNestedScrollingEnabled(binding.rvFavorites, true)

        // Setup Adapter
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvFavorites.layoutManager = layoutManager
        adapter = ItemFavoritesAdapter(
            onTapItem = { favorite: Delivery ->
                Timber.d("onTapItem::favorite = $favorite")
                // Attempt View Details
                viewModel.intent.viewDetails(of = favorite)
            },
            onRemoveFromFavorites = { item: Delivery ->
                Timber.d("onRemoveFromFavorites::item = $item")
                // Attempt Remove from Favorites
                viewModel.intent.removeFromFavorites(which = item)
            }
        )
        binding.rvFavorites.adapter = adapter

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

        // List of Favorites
        if(viewState.emptyList) {
            // HIDE List and DISPLAY Empty Label
            binding.rvFavorites.visibility = View.GONE
            binding.containerEmptyState.visibility = View.VISIBLE

            binding.appBarLayout.setExpanded(false)
        } else {
            // REVEAL List
            binding.containerEmptyState.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE

            adapter?.replace(viewState.items)
        }
    }

    private suspend fun takeSingleEvent(event: SingleEvent) {
        Timber.d("takeSingleEvent() -> event = $event")

        when(event) {
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