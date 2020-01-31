package com.philpot.roomcipher.util

import android.content.Context
import android.os.Build
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException

object EncryptionUtil {
    private val TAG = EncryptionUtil::class.java.simpleName

    private val keyStore: KeyStore?
        get() {
            return try {
                KeyStore.getInstance(EncryptionKeyGenerator.ANDROID_KEY_STORE).apply {
                    load(null)
                }
            } catch (e: KeyStoreException) {
                ExceptionUtil.maybeLog(TAG, e)
                null
            } catch (e: CertificateException) {
                ExceptionUtil.maybeLog(TAG, e)
                null
            } catch (e: NoSuchAlgorithmException) {
                ExceptionUtil.maybeLog(TAG, e)
                null
            } catch (e: IOException) {
                ExceptionUtil.maybeLog(TAG, e)
                null
            }
        }

    fun encrypt(context: Context, token: String): String? {
        return getSecurityKey(context)?.encrypt(token)
    }

    fun decrypt(context: Context, token: String): String? {
        return getSecurityKey(context)?.decrypt(token)
    }

    private fun getSecurityKey(context: Context): SecurityKey? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EncryptionKeyGenerator.generateSecretKey(keyStore)
        } else {
            EncryptionKeyGenerator.generateKeyPairPreM(context, keyStore)
        }
    }

    fun clear() {
        try {
            keyStore?.let {
                if (it.containsAlias(EncryptionKeyGenerator.KEY_ALIAS)) {
                    it.deleteEntry(EncryptionKeyGenerator.KEY_ALIAS)
                }
            }
        } catch (e: KeyStoreException) {
            ExceptionUtil.maybeLog(TAG, e)
        }

    }
}
