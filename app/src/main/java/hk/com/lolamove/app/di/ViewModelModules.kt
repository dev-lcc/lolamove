package hk.com.lolamove.app.di

import hk.com.lolamove.app.ui.deliverydetails.DeliveryDetailsViewModel
import hk.com.lolamove.app.ui.home.deliveries.DeliveriesViewModel
import hk.com.lolamove.domain.datamodels.Delivery
import org.koin.androidx.viewmodel.dsl.viewModel
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