package hk.com.lolamove.app.ui.deliverydetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import hk.com.lolamove.app.LolaMoveBaseFragment
import hk.com.lolamove.app.databinding.FragmentDeliveryDetailsBinding
import hk.com.lolamove.app.R
import hk.com.lolamove.app.utils.AppBarStateChangedListener
import hk.com.lolamove.app.utils.flow.throttleFirst
import hk.com.lolamove.domain.datamodels.Delivery
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.appcompat.navigationClicks
import timber.log.Timber
import java.text.DecimalFormat

class DeliveryDetailsFragment: LolaMoveBaseFragment() {

    private val binding: FragmentDeliveryDetailsBinding by viewBinding(FragmentDeliveryDetailsBinding::bind)
    private val viewModel: DeliveryDetailsViewModel by viewModel() {
        val item: Delivery = requireArguments().getParcelable<Delivery>(ARG_INPUT_DELIVERY_ITEM)!!
        parametersOf(item)
    }

    private var appBarOffsetChange: AppBarLayout.OnOffsetChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_delivery_details, container, false)

    override fun onDestroyView() {
        // De-init
        if(appBarOffsetChange != null) {
            binding.appBarLayout.removeOnOffsetChangedListener(appBarOffsetChange)
        }
        appBarOffsetChange = null

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // HOME UP Click
        binding.toolbar.navigationClicks()
            .throttleFirst(1000L)
            .onEach {
                appNavController.popBackStack()
            }
            .launchIn(lifecycleScope)

        // AppBar Expand/Collapse Behavior
        appBarOffsetChange = getAppBarOffsetChangeListener()
        binding.appBarLayout.addOnOffsetChangedListener(appBarOffsetChange)

        // On-tap toggle Favorites
        binding.efabFavorite.clicks()
            .throttleFirst(1000L)
            .onEach {
                // Attempt toggle favorites
                viewModel.intent.toggleFavorites()
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

        // Delivery Photo
        viewState.deliveryPhotoUrl?.let { deliveryPhotoUrl ->
            Glide.with(requireContext())
                .asDrawable()
                .load(deliveryPhotoUrl)
                .error(R.drawable.ic_delivery_white)
                .into(binding.acivDeliveryPhoto)
        } ?: run {
            // Set default
            binding.acivDeliveryPhoto.setImageResource(R.drawable.ic_delivery_white)
        }

        // Remarks
        binding.actvRemarks.text = "\"${viewState.remarks}\""

        // Sender Info
        binding.actvSenderName.text = viewState.senderName ?: "--"
        binding.actvSenderPhone.text = viewState.senderPhoneNumber ?: "--"
        binding.actvSenderEmail.text = viewState.senderEmail ?: "--"

        // Summary
        binding.actvDeliveryFee.text = getString(
            R.string.delivery_details_summary_delivery_fee,
            "${viewState.currencySymbol}${PRICE_FORMAT.format((viewState.deliveryFee ?: 0f).toDouble())}"
        )
        binding.actvSurcharge.text = getString(
            R.string.delivery_details_summary_surcharge,
            "${viewState.currencySymbol}${PRICE_FORMAT.format((viewState.surcharge ?: 0f).toDouble())}"
        )
        binding.actvTotalFee.text = getString(
            R.string.delivery_details_summary_total,
            "${viewState.currencySymbol}${PRICE_FORMAT.format((viewState.totalFee ?: 0f).toDouble())}"
        )

        // Route Info
        binding.actvFromRouteValue.text = viewState.routeFrom ?: "--"
        binding.actvToRouteValue.text = viewState.routeTo ?: "--"

        // Favorites Toggle Action
        when(viewState.isFavorite) {
            true -> {
                // Reveal Favorite Indicator
                binding.acivFavoriteIndicator.visibility = View.VISIBLE

                with(binding.efabFavorite) {
                    backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite)
                    iconTint = ContextCompat.getColorStateList(requireContext(), R.color.red_a700)
                    text = getString(R.string.delivery_details_btn_remove_from_favorites)
                    setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red_a700)
                    )
                }
            }
            false -> {
                // HIDE Favorite Indicator
                binding.acivFavoriteIndicator.visibility = View.GONE

                with(binding.efabFavorite) {
                    backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red_a400)
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_outline)
                    iconTint = ContextCompat.getColorStateList(requireContext(), R.color.white)
                    text = getString(R.string.delivery_details_btn_add_to_favorites)
                    setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.white)
                    )
                }
            }
        }

    }

    private suspend fun takeSingleEvent(event: SingleEvent) {
        Timber.d("takeSingleEvent() -> event = $event")
    }

    /**
     * Helper function to provide Expand/Collapse TabLayout Text Appearance Behaviour
     */
    private fun getAppBarOffsetChangeListener(): AppBarLayout.OnOffsetChangedListener =
        object : AppBarStateChangedListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                Timber.d("binding.appBarLayout.addOnOffsetChangedListener() -> state = ${state.name}")
                when (state) {
                    State.EXPANDED -> {
                        // HIDE Toolbar Title
                        binding.toolbar.title = null
                    }
                    State.COLLAPSED -> {
                        // DISPLAY Toolbar Title
                        binding.toolbar.title = getString(R.string.delivery_details_appbar_title)
                    }
                    State.IDLE -> { }
                }
            }
        }

    companion object {
        const val ARG_INPUT_DELIVERY_ITEM = "arg-input-delivery-item"

        fun createInputArguments(item: Delivery): Bundle =
            bundleOf(
                ARG_INPUT_DELIVERY_ITEM to item,
            )

        private val PRICE_FORMAT = DecimalFormat("###,###,###,##0.00")
    }

}