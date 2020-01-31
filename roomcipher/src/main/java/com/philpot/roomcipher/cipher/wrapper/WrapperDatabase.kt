package com.philpot.roomcipher.cipher.wrapper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteTransactionListener
import android.os.CancellationSignal
import android.util.Pair
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteStatement
import net.sqlcipher.database.SQLiteCursor
import net.sqlcipher.database.SQLiteDatabase
import java.util.Locale

class WrapperDatabase(private val db: SQLiteDatabase) : SupportSQLiteDatabase {

    override fun setMaximumSize(numBytes: Long): Long = db.setMaximumSize(numBytes)

    override fun insert(table: String?, conflictAlgorithm: Int, values: ContentValues?): Long =
        db.insertWithOnConflict(table, null, values, conflictAlgorithm)

    override fun enableWriteAheadLogging(): Boolean = db.enableWriteAheadLogging()

    override fun isDatabaseIntegrityOk(): Boolean = db.isDatabaseIntegrityOk

    override fun isWriteAheadLoggingEnabled(): Boolean = db.isWriteAheadLoggingEnabled

    override fun disableWriteAheadLogging() = db.disableWriteAheadLogging()

    override fun compileStatement(sql: String?): SupportSQLiteStatement = WrapperStatement(db.compileStatement(sql))

    override fun beginTransactionWithListenerNonExclusive(transactionListener: SQLiteTransactionListener?) {
        db.beginTransactionWithListenerNonExclusive(object : net.sqlcipher.database.SQLiteTransactionListener {
            override fun onCommit() { transactionListener?.onCommit() }
            override fun onRollback() { transactionListener?.onRollback() }
            override fun onBegin() { transactionListener?.onBegin() }
        })
    }

    override fun isDbLockedByCurrentThread(): Boolean = db.isDbLockedByCurrentThread

    override fun setPageSize(numBytes: Long) {
        db.pageSize = numBytes
    }

    override fun query(query: String?): Cursor = query(query, null)

    override fun query(query: String?, bindArgs: Array<out Any>?): Cursor = query(SimpleSQLiteQuery(query, bindArgs))

    override fun query(query: SupportSQLiteQuery?): Cursor = query(query, null)

    override fun query(
        outerQuery: SupportSQLiteQuery?,
        cancellationSignal: CancellationSignal?): Cursor {

        val bindings = WrapperBindings()
        outerQuery?.bindTo(bindings)

        return db.rawQueryWithFactory(
            { db, masterQuery, editTable, query -> SQLiteCursor(db, masterQuery, editTable, query) },
            outerQuery?.sql, bindings.getBindings(), null)
    }

    override fun beginTransaction() = db.beginTransaction()

    override fun endTransaction() = db.endTransaction()

    override fun getMaximumSize(): Long = db.maximumSize

    override fun setLocale(locale: Locale?) = db.setLocale(locale)

    override fun update(
        table: String?,
        conflictAlgorithm: Int,
        values: ContentValues?,
        whereClause: String?,
        whereArgs: Array<out Any>?): Int {

        val newArgs = Array(whereArgs?.size ?: 0) { "" }
        whereArgs?.withIndex()?.forEach {
            newArgs[it.index] = it.value.toString()
        }

        return db.update(table, values, whereClause, newArgs)
    }

    override fun isOpen(): Boolean = db.isOpen

    override fun getAttachedDbs(): MutableList<Pair<String, String>> = db.attachedDbs

    override fun getVersion(): Int = db.version

    override fun execSQL(sql: String?) = db.execSQL(sql)

    override fun execSQL(sql: String?, bindArgs: Array<out Any>?) = db.execSQL(sql, bindArgs)

    override fun yieldIfContendedSafely(): Boolean = db.yieldIfContendedSafely()

    override fun yieldIfContendedSafely(sleepAfterYieldDelay: Long): Boolean = db.yieldIfContendedSafely(sleepAfterYieldDelay)

    override fun close() = db.close()

    override fun delete(table: String?, whereClause: String?, whereArgs: Array<out Any>?): Int {
        val newArgs = Array(whereArgs?.size ?: 0) { "" }
        whereArgs?.withIndex()?.forEach {
            newArgs[it.index] = it.value.toString()
        }

        return db.delete(table, whereClause, newArgs)
    }

    override fun needUpgrade(newVersion: Int): Boolean = db.needUpgrade(newVersion)

    override fun setMaxSqlCacheSize(cacheSize: Int) {
        db.maxSqlCacheSize = cacheSize
    }

    override fun setForeignKeyConstraintsEnabled(enable: Boolean) = db.setForeignKeyConstraintsEnabled(enable)

    override fun beginTransactionNonExclusive() = db.beginTransactionNonExclusive()

    override fun setTransactionSuccessful() = db.setTransactionSuccessful()

    override fun setVersion(version: Int) {
        db.version = version
    }

    override fun beginTransactionWithListener(transactionListener: SQLiteTransactionListener?) {
        db.beginTransactionWithListener(object : net.sqlcipher.database.SQLiteTransactionListener {
            override fun onCommit() { transactionListener?.onCommit() }
            override fun onRollback() { transactionListener?.onRollback() }
            override fun onBegin() { transactionListener?.onBegin() }
        })
    }

    override fun inTransaction(): Boolean = db.inTransaction()

    override fun isReadOnly(): Boolean = db.isReadOnly

    override fun getPath(): String = db.path

    override fun getPageSize(): Long = db.pageSize
}