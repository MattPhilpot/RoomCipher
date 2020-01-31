package com.philpot.roomcipher.cipher

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.philpot.roomcipher.cipher.wrapper.WrapperDatabase
import net.sqlcipher.DatabaseErrorHandler
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper


class SQLCipherOpenHelper(context: Context,
                          dbName: String,
                          dbVersion: Int,
                          errorHandler: DatabaseErrorHandler,
                          @Volatile
                          private var callback: SupportSQLiteOpenHelper.Callback?) : SQLiteOpenHelper(context, dbName, null, dbVersion, null, errorHandler) {

    private var database: WrapperDatabase? = null

    @Volatile
    private var databaseMigrated: Boolean = false

    fun onCorruption(dbObj: SQLiteDatabase?) {
        database?.let {
            callback?.onCorruption(it)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        callback?.onCreate(getWrappedDatabase(db))
    }

    override fun onConfigure(db: SQLiteDatabase) {
        callback?.onConfigure(getWrappedDatabase(db))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        databaseMigrated = true
        callback?.onUpgrade(getWrappedDatabase(db), oldVersion, newVersion)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        databaseMigrated = true
        callback?.onDowngrade(getWrappedDatabase(db), oldVersion, newVersion)
    }

    override fun onOpen(db: SQLiteDatabase) {
        if (!databaseMigrated) {
            callback?.onOpen(getWrappedDatabase(db))
        }
    }

    override fun close() {
        super.close()
        database = null
    }

    fun getSupportReadableDatabase(passphrase: ByteArray?) : SupportSQLiteDatabase {
        databaseMigrated = false

        val db = super.getReadableDatabase(passphrase)

        return if (databaseMigrated) {
            close()
            getSupportReadableDatabase(passphrase)
        } else {
            getWrappedDatabase(db)
        }
    }

    fun getSupportWriteableDatabase(passphrase: ByteArray?) : SupportSQLiteDatabase {
        databaseMigrated = false

        val db = super.getWritableDatabase(passphrase)

        return if (databaseMigrated) {
            close()
            getSupportWriteableDatabase(passphrase)
        } else {
            getWrappedDatabase(db)
        }
    }

    private fun getWrappedDatabase(db: SQLiteDatabase): WrapperDatabase {
        return database ?: WrapperDatabase(db).apply {
            database = this
        }
    }
}
