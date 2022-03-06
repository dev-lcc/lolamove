package hk.com.app_compose.di

import hk.com.lolamove.domain.datamodels.Delivery
import org.koin.androidx.viewmodel.dsl.viewModel
import hk.com.app_compose.ui.deliverydetails.DeliveryDetailsViewModel
import hk.com.app_compose.ui.home.deliveries.DeliveriesViewModel
import hk.com.app_compose.ui.home.favorites.FavoritesViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object ViewModelModules {

    fun build(): Module = module {
        viewModel {
            DeliveriesViewModel(
                getListOfDeliveriesUseCase = get(),
                getDeliveryFavoritesUseCase = get(),
                addDeliveryToFavoritesUseCase = get(),
                removeDeliveryFromFavoritesUseCase = get(),
            )
        }
        viewModel {
            FavoritesViewModel(
                getDeliveryFavoritesUseCase = get(),
                removeDeliveryFromFavoritesUseCase = get(),
            )
        }

        viewModel { (item: Delivery) ->
            DeliveryDetailsViewModel(
                item = item,
                observeDeliveryIsFavoriteUseCase = get(),
                addDeliveryToFavoritesUseCase = get(),
                removeDeliveryFromFavoritesUseCase = get(),
            )
        }
    }

}