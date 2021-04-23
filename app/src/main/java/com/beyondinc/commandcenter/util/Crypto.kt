package com.beyondinc.commandcenter.util

import org.apache.commons.codec.binary.Base64
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {

    fun generateMD5Hash(plainText: String): String? {
        return generateOneWayHash("MD5", plainText)
    }

    fun generateSHA512Hash(plainText: String): String? {
        return generateOneWayHash("SHA-512", plainText)
    }

    private fun generateOneWayHash(algorithm: String, plainText: String): String? {
        return try {
            val digest = MessageDigest.getInstance(algorithm)
            digest.update(plainText.toByteArray())
            val byteData = digest.digest()
            val stringBuffer = StringBuffer()
            for (byteItem in byteData) {
                stringBuffer.append(Character.forDigit((byteItem.toInt() and 0xf0) shr 4, 16))
                stringBuffer.append(Character.forDigit((byteItem.toInt() and 0x0f), 16))
            }
            stringBuffer.toString()
        } catch (e: NoSuchAlgorithmException) {
            null
        }
    }

    fun getCurrentTimeKey(): String {
        return try {
            val data = Date(System.currentTimeMillis())
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
            val timeString = dateFormat.format(data)
            generateMD5Hash(timeString)!!.toLowerCase(Locale.ROOT)
        } catch (e: Exception) {
            ""
        }
    }

    object AES256 {

        fun encrypt(plainText: String, secretKey: String): String {
            val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(plainText.toByteArray())
            return String(Base64.encodeBase64(encrypted))
        }

        fun decrypt(encodedText: String, secretKey: String): String {
            val byteStr = Base64.decodeBase64(encodedText.toByteArray())
            return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
        }

        private fun cipher(cryptoMode: Int, secretKey: String): Cipher {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val modifiedKey = makeOver32BytesKey(secretKey)
            val secretKeySpec = SecretKeySpec(modifiedKey.toByteArray(), "AES")
            val ivParameterSpec = IvParameterSpec(modifiedKey.substring(0, 16).toByteArray())
            cipher.init(cryptoMode, secretKeySpec, ivParameterSpec)

            return cipher
        }

        // 키가 32bytes 보다 짧으면 exception 방지를 위해 붙여서 길게 만든다
        private fun makeOver32BytesKey(key: String): String {
            val enhanceKeyBuilder = StringBuilder().append(key)
            while (enhanceKeyBuilder.length < 32) {
                enhanceKeyBuilder.append(key)
            }
            return enhanceKeyBuilder.trim().toString()
        }
    }
}