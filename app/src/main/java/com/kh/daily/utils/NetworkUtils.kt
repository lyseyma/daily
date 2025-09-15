package com.kh.daily.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log

/**
 * NetworkUtils provides network connectivity checking utilities.
 * Used to determine if the device has internet access before making Firebase calls.
 */
object NetworkUtils {

    /**
     * Checks if the device has an active internet connection.
     * @param context Application context
     * @return true if connected to internet, false otherwise
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.d("NetworkUtils", "Connected via WiFi")
                    true
                }

                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.d("NetworkUtils", "Connected via Cellular")
                    true
                }

                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.d("NetworkUtils", "Connected via Ethernet")
                    true
                }

                else -> {
                    Log.d("NetworkUtils", "No active network connection")
                    false
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            val isConnected = networkInfo?.isConnectedOrConnecting == true
            Log.d("NetworkUtils", "Network connected (legacy): $isConnected")
            isConnected
        }
    }

    /**
     * Gets a user-friendly network status message.
     * @param context Application context
     * @return String describing the current network status
     */
    fun getNetworkStatusMessage(context: Context): String {
        return if (isNetworkAvailable(context)) {
            "Connected to internet"
        } else {
            "No internet connection. Using offline data."
        }
    }

    /**
     * Checks if the specific firestore.googleapis.com host is reachable.
     * This is a more specific check for Firebase connectivity issues.
     * Note: This should be called from a background thread.
     */
    fun isFirebaseReachable(): Boolean {
        return try {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("/system/bin/ping -c 1 firestore.googleapis.com")
            val exitValue = process.waitFor()
            exitValue == 0
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error checking Firebase connectivity: ${e.message}")
            false
        }
    }
}