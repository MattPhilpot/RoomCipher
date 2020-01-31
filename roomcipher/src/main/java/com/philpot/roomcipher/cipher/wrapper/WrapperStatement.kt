package com.philpot.roomcipher.cipher.wrapper

import androidx.sqlite.db.SupportSQLiteStatement
import net.sqlcipher.database.SQLiteStatement

/**
 * Wrapper for the SQLCipher implementation of SupportSQLiteStatement
 *
 * Done so we can support an encrypted database like a boss
 */
class WrapperStatement(statement: SQLiteStatement) : WrapperProgram<SQLiteStatement>(statement), SupportSQLiteStatement {
    override fun simpleQueryForLong(): Long = delegate.simpleQueryForLong()
    override fun simpleQueryForString(): String = delegate.simpleQueryForString()
    override fun execute() = delegate.execute()
    override fun executeInsert(): Long = delegate.executeInsert()
    override fun executeUpdateDelete(): Int = delegate.executeUpdateDelete()
}
