package com.example.cryptotracker.notifications
import android.content.Context
import androidx.work.*
import com.example.cryptotracker.network.CoinDto
import java.util.concurrent.TimeUnit

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

    val request = PeriodicWorkRequestBuilder<CryptoNotificationWorker>(15, TimeUnit.MINUTES)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}