package com.example.cryptotracker.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cryptotracker.R
import com.example.cryptotracker.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CryptoNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val coinId = inputData.getString("coinId") ?: return@withContext Result.failure()
        val coinName = inputData.getString("coinName") ?: return@withContext Result.failure()
        val targetPrice = inputData.getDouble("targetPrice", -1.0)
        if (targetPrice <= 0) return@withContext Result.failure()

        try {
            val response = RetrofitInstance.api.getCoinsByIds(ids = coinId)
            val price = response[0].current_price

            if (price >= targetPrice) {
                if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    showNotification(
                        "$coinName Alert",
                        "Price reached $price €"
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String, message: String) {
        val channelId = "crypto_alerts"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, "Crypto Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Notifications for crypto price targets"
        }
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(System.currentTimeMillis().toInt(), notification)
    }
}