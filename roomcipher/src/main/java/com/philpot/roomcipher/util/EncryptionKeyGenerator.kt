package com.philpot.roomcipher.util

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.philpot.roomcipher.BuildConfig
import java.math.BigInteger
import java.security.*
import java.util.*
import javax.crypto.KeyGenerator
import javax.security.auth.x500.X500Principal

object EncryptionKeyGenerator {
    private val TAG = EncryptionKeyGenerator::class.java.simpleName

    internal const val ANDROID_KEY_STORE = "AndroidKeyStore"
    internal const val KEY_ALIAS = "RoomCipherKeyAlias"

    @TargetApi(Build.VERSION_CODES.M)
    internal fun generateSecretKey(keyStore: KeyStore?): SecurityKey? {
        try {
            if (keyStore != null && !keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(
                        KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build())
                return SecurityKey(keyGenerator.generateKey())
            }
        } catch (e: KeyStoreException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: NoSuchProviderException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: NoSuchAlgorithmException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: InvalidAlgorithmParameterException) {
            ExceptionUtil.maybeLog(TAG, e)
        }

        try {
            val entry = keyStore?.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            return SecurityKey(entry.secretKey)
        } catch (e: KeyStoreException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: NoSuchAlgorithmException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: UnrecoverableEntryException) {
            ExceptionUtil.maybeLog(TAG, e)
        }

        return null
    }

    @Suppress("DEPRECATION")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    internal fun generateKeyPairPreM(context: Context, keyStore: KeyStore?): SecurityKey? {
        try {
            if (keyStore != null && !keyStore.containsAlias(KEY_ALIAS)) {
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                //1 Year validity
                end.add(Calendar.YEAR, 5)

                val spec = KeyPairGeneratorSpec.Builder(context).setAlias(KEY_ALIAS)
                    .setSubject(X500Principal("CN=$KEY_ALIAS"))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()

                val kpg = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE)
                kpg.initialize(spec)
                kpg.generateKeyPair()
            }
        } catch (e: KeyStoreException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: NoSuchAlgorithmException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: InvalidAlgorithmParameterException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: NoSuchProviderException) {
            ExceptionUtil.maybeLog(TAG, e)
        }

        try {
            val entry = keyStore?.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
            return SecurityKey(KeyPair(entry.certificate.publicKey, entry.privateKey))
        } catch (e: KeyStoreException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: NoSuchAlgorithmException) {
            ExceptionUtil.maybeLog(TAG, e)
        } catch (e: UnrecoverableEntryException) {
            ExceptionUtil.maybeLog(TAG, e)
        }

        return null
    }
}
