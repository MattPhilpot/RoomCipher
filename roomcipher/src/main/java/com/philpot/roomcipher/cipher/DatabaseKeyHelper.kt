package com.philpot.roomcipher.cipher

import android.content.Context
import android.util.Base64
import com.philpot.roomcipher.util.EncryptionUtil
import java.security.SecureRandom
import kotlin.random.Random

object DatabaseKeyHelper {

    fun fetchDbKey(context: Context): String {
        val preferences = context.getSharedPreferences(DB_VALUE, Context.MODE_PRIVATE)
        val encryptedKey = preferences.getString(KEY_VALUE, "") ?: ""
        return if (encryptedKey.isBlank()) {
            val newKey = generateRandomString()
            preferences.edit().putString(KEY_VALUE, EncryptionUtil.encrypt(context, newKey) ?: "").apply()
            newKey
        } else {
            EncryptionUtil.decrypt(context, encryptedKey) ?: ""
        }
    }

    private fun generateRandomString(): String {
        val secureRandom = SecureRandom()
        val generator = Random.Default
        val randomLength = generator.nextInt(PASSWORD_LENGTH) + PASSWORD_LENGTH
        val key = ByteArray(randomLength)
        secureRandom.nextBytes(key)
        return Base64.encodeToString(key, Base64.NO_PADDING or Base64.URL_SAFE).trim()
    }

    private const val PASSWORD_LENGTH = 34
    private const val DB_VALUE = "RoomCipherParameters"
    private const val KEY_VALUE = "DatabaseKey"
}