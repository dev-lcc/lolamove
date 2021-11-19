package hk.com.lolamove.datasource.local.roomdb

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import hk.com.lolamove.datasource.local.roomdb.dao.FavoriteDeliveryDao
import hk.com.lolamove.datasource.local.roomdb.dto.DTOFavoriteDelivery

@Database(
    entities = [
        DTOFavoriteDelivery::class,
    ],
    version = LolaMoveDatabase.VERSION,
    exportSchema = true,
)
abstract class LolaMoveDatabase: RoomDatabase() {

    abstract fun favoriteDeliveryDao(): FavoriteDeliveryDao

    companion object {

        const val VERSION = 1
        const val DB_NAME = "LolaMove.db"

        fun build(
            context: Context,
            allowOnMainThread: Boolean,
        ): LolaMoveDatabase =
            Room.databaseBuilder(
                context,
                LolaMoveDatabase::class.java,
                DB_NAME,
            )
                .apply {
                    if(allowOnMainThread) allowMainThreadQueries()
                }
                .addMigrations(
                    /* MIGRATION_1_2 */
                )
                .build()

        // TODO:: Declare new RoomDB Data Migration(s) HERE...

        @VisibleForTesting
        internal val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration script...
            }
        }
    }

}