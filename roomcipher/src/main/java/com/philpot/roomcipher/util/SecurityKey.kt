package com.philpot.roomcipher.util

import android.os.Build
import android.util.Base64
import java.security.GeneralSecurityException
import java.security.KeyPair
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal class SecurityKey {

    var secretKey: SecretKey? = null
        private set
    var keyPair: KeyPair? = null
        private set

    constructor(secretKey: SecretKey) {
        this.secretKey = secretKey
    }

    constructor(keyPair: KeyPair) {
        this.keyPair = keyPair
    }

    fun encrypt(token: String?): String? {
        if (token == null) return null

        try {
            val cipher = getCipher(Cipher.ENCRYPT_MODE)

            val encrypted = cipher.doFinal(token.toByteArray())
            return Base64.encodeToString(encrypted, Base64.URL_SAFE)
        } catch (e: GeneralSecurityException) {
            ExceptionUtil.maybeLog(TAG, e)
        }

        //Unable to encrypt Token
        return null
    }

    fun decrypt(encryptedToken: String?): String? {
        if (encryptedToken == null) return null

        try {
            val cipher = getCipher(Cipher.DECRYPT_MODE)

            val decoded = Base64.decode(encryptedToken, Base64.URL_SAFE)
            val original = cipher.doFinal(decoded)
            return String(original)
        } catch (e: GeneralSecurityException) {
            ExceptionUtil.maybeLog(TAG, e)
        }

        //Unable to decrypt encrypted Token
        return null
    }

    @Throws(GeneralSecurityException::class)
    private fun getCipher(mode: Int): Cipher {
        val cipher: Cipher

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cipher = Cipher.getInstance(AES_MODE_FOR_POST_API_23)
            cipher.init(mode, secretKey, GCMParameterSpec(128, AES_MODE_FOR_POST_API_23.toByteArray(), 0, 12))
        } else {
            cipher = Cipher.getInstance(RSA_MODE)
            cipher.init(mode, if (mode == Cipher.DECRYPT_MODE) keyPair?.public else keyPair?.private)
        }

        return cipher
    }

    companion object {
        private val TAG = SecurityKey::class.java.simpleName

        private val RSA_MODE = "RSA/ECB/PKCS1Padding"
        private val AES_MODE_FOR_POST_API_23 = "AES/GCM/NoPadding"
        private val AES_MODE_FOR_PRE_API_18 = "AES/CBC/PKCS5Padding"
    }
}
