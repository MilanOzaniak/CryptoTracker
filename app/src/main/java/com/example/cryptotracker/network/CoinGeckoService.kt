package com.example.cryptotracker.network

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoService {

    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "eur",
        @Query("per_page") perPage: Int = 5,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<CoinDto>


    @GET("coins/markets")
    suspend fun getCoinsByIds(
        @Query("vs_currency") vsCurrency: String = "eur",
        @Query("ids") ids: String
    ): List<CoinDto>

    @GET("coins/markets")
    suspend fun getCoinsByIdListSorted(
        @Query("vs_currency") vsCurrency: String = "eur",
        @Query("ids") idList: String,
        @Query("order") order: String = "market_cap_desc",
        @Query("sparkline") sparkline: Boolean = false
    ): List<CoinDto>
}


data class CoinDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: Double,
    val price_change_percentage_24h: Double
)