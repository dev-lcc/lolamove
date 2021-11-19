package hk.com.lolamove.data.di

import hk.com.lolamove.data.usecase.*
import hk.com.lolamove.datasource.local.di.LocalDatasourceModule
import hk.com.lolamove.datasource.remote.di.RemoteDatasourceModule
import hk.com.lolamove.domain.usecase.*
import org.koin.core.module.Module
import org.koin.dsl.module

class DataModule(
    private val restApiUrlEndPoint: String,
    private val isDebug: Boolean = false,
) {

    fun build(): List<Module> =
        /**
         * `datasource-local`
         */
        LocalDatasourceModule(isDebug = isDebug).build() +
                /**
                 * `datasource-remote`
                 */
                RemoteDatasourceModule(
                    restApiUrlEndPoint = restApiUrlEndPoint,
                    isDebug = isDebug,
                ).build() +
                /**
                 * `Use Cases`
                 */
                module {
                    /**
                     * Use Case - Deliveries
                     */
                    single<GetListOfDeliveriesUseCase> {
                        GetListOfDeliveriesUseCaseImpl(
                            deliveriesRestApiDatasource = get(),
                        )
                    }
                    single<GetDeliveryFavoritesUseCase> {
                        GetDeliveryFavoritesUseCaseImpl(
                            favoritesLocalDatasource = get(),
                        )
                    }
                    single<ObserveDeliveryIsFavoriteUseCase> {
                        ObserveDeliveryIsFavoriteUseCaseImpl(
                            favoritesLocalDatasource = get(),
                        )
                    }
                    single<AddDeliveryToFavoritesUseCase> {
                        AddDeliveryToFavoritesUseCaseImpl(
                            favoritesLocalDatasource = get(),
                        )
                    }
                    single<RemoveDeliveryFromFavoritesUseCase> {
                        RemoveDeliveryFromFavoritesUseCaseImpl(
                            favoritesLocalDatasource = get(),
                        )
                    }
                }

}