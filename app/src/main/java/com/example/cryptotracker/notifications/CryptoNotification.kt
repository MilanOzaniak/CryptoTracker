package com.example.cryptotracker.notifications
import android.content.Context
import androidx.work.*
import com.example.cryptotracker.network.CoinDto
import java.util.concurrent.TimeUnit

/**
 * Každých 15 minút sa spustí worker a ak je podmienka splnená spustí notifikáciu
 * @param context Kontext aplikácie
 * @param coin Kryptomena
 * @param targetPrice Cieľová cena na upozornenie
 * @param choose True = keď cena klesne pod úroveň, False = keď stúpne nad úroveň
 */
fun cryptoNotification(
    context: Context,
    coin: CoinDto,
    targetPrice: Double,
    choose: Boolean

) {
    val inputData = workDataOf(
        "coinId" to coin.id,
        "coinName" to coin.name,
        "targetPrice" to targetPrice,
        "choose" to choose
    )

    // Každých 15 minút skontroluje cenu kryptomeny
    val request = PeriodicWorkRequestBuilder<CryptoNotificationWorker>(15, TimeUnit.MINUTES)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}