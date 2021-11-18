package hk.com.lolamove.datasource.local.di

import android.content.Context
import hk.com.lolamove.datasource.local.FavoriteDeliveryLocalDatasource
import hk.com.lolamove.datasource.local.roomdb.LolaMoveDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

class LocalDatasourceModule(
    val isDebug: Boolean
) {
    fun build(): Module = module {
        single { provideRoomDatabase(context = get()) }

        /**
         * Local Datasource
         */
        single {
            FavoriteDeliveryLocalDatasource(database = get())
        }
    }

    private fun provideRoomDatabase(context: Context): LolaMoveDatabase =
        LolaMoveDatabase.build(
            context = context,
            allowOnMainThread = isDebug,
        )
}