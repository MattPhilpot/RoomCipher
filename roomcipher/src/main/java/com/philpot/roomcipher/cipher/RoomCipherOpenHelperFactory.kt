package com.philpot.roomcipher.cipher

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper

class RoomCipherOpenHelperFactory(context: Context) : SupportSQLiteOpenHelper.Factory {

    private val defaultName = "${context.packageName}.db"

    private val databaseKey = DatabaseKeyHelper.fetchDbKey(context)

    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
        return SQLCipherHelper(configuration.context, configuration.callback, configuration.name ?: defaultName, databaseKey)
    }
}
