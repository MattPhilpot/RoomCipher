package com.philpot.roomcipher.cipher.wrapper

import androidx.sqlite.db.SupportSQLiteProgram
import net.sqlcipher.database.SQLiteProgram

/**
 * Wrapper for the SQLCipher implementation of SupportSQLiteProgram
 *
 * Done so we can support an encrypted database like a boss
 */
open class WrapperProgram<T : SQLiteProgram>(protected val delegate: T) : SupportSQLiteProgram {
    override fun bindBlob(index: Int, value: ByteArray?) = delegate.bindBlob(index, value)
    override fun bindLong(index: Int, value: Long) = delegate.bindLong(index, value)
    override fun bindString(index: Int, value: String?) = delegate.bindString(index, value)
    override fun bindDouble(index: Int, value: Double) = delegate.bindDouble(index, value)
    override fun close() = delegate.close()
    override fun bindNull(index: Int) = delegate.bindNull(index)
    override fun clearBindings() = delegate.clearBindings()
}
