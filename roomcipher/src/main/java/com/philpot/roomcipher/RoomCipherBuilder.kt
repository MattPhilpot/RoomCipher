package com.philpot.roomcipher

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.philpot.roomcipher.cipher.RoomCipherOpenHelperFactory

/**
 * Convenience method for creating a Room database
 */
object RoomCipherBuilder {

    fun <D : RoomDatabase>buildDatabase(context: Context,
                                         database: Class<D>,
                                         databaseName: String? = null) : RoomDatabase.Builder<D> {
        return if (databaseName?.isNotBlank() == true) {
            Room.databaseBuilder(context, database, databaseName)
                .openHelperFactory(RoomCipherOpenHelperFactory(context))
        } else {
            Room.databaseBuilder(context, database, "${context.packageName}.db")
                .openHelperFactory(RoomCipherOpenHelperFactory(context))
        }
    }
}
