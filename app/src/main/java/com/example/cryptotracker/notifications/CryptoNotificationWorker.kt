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
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.cryptotracker.R
import com.example.cryptotracker.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// šablóna nájdená na internete, upravená na moje požiadavky
// vzťahuje sa na celý package notifications

/**
 * Worker, ktorý pravidelne kontroluje cenu kryptomeny cez CoinGecko API
 * Keď cena presiahne (alebo klesne pod) cieľovú hodnotu, pošle notifikáciu
 * Po prvej úspešnej notifikácii worker zruší ďalšie spúšťanie.
 * @constructor
 * @param context Kontext aplikácie
 * @param params Worker parametre
 */
class CryptoNotificationWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {


    //Hlavná logika, ktorá sa vykoná pri spustení workera.
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // Načítanie vstupných parametrov (worker parametrov)
        val coinId = inputData.getString("coinId") ?: return@withContext Result.failure()
        val coinName = inputData.getString("coinName") ?: return@withContext Result.failure()
        val targetPrice = inputData.getDouble("targetPrice", -1.0)
        val choose = inputData.getBoolean("choose", false)
        if (targetPrice <= 0) return@withContext Result.failure()
        try {
            // Zavolanie CoinGecko API pre aktuálnu cenu
            val response = RetrofitInstance.api.getCoinsByIds(ids = coinId)
            val price = response[0].current_price

            // Podmienka na posielanie notifikácie
            if ((!choose && price >= targetPrice) || (choose && price <= targetPrice)) {
                // Overenie povolenia pre notifikácie
                if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    showNotification(
                        "$coinName Alert",
                        "Price ${if (choose) "is less than" else "is above"} $targetPrice  (current price $price)€"
                    )
                    // Po úspešnej notifikácii worker zruší ďalšie opakovania
                    WorkManager.getInstance(applicationContext).cancelWorkById(id)

                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Ak zlyhá spojenie alebo API tak sa znova opakuje
            Result.retry()
        }
    }

    /**
     * Zobrazí notifikáciu s daným názvom a správou
     * @param title Názov notifikácie
     * @param message Správa v notifikácii
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private suspend fun showNotification(title: String, message: String) {
        val channelId = "crypto_alerts"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Vytvorí kanál pre notifikácie, ak neexistuje
        val channel = NotificationChannel(channelId, "Crypto Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Notifications for crypto price targets"
        }
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(System.currentTimeMillis().toInt(), notification)
    }
}