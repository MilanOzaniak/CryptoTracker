package com.example.cryptotracker.network

import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Rozhranie pre komunikáciu s CoinGecko API
 */
interface CoinGeckoService {

    /**
     * Získa zoznam kryptomien
     * @param vsCurrency Mena
     * @param perPage Počet položiek na jednu stránku
     * @param page Číslo stránky
     * @param sparkline Vracia sparkline dáta (graf) ak true
     * @return List kryptomien
     */
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "eur",
        @Query("per_page") perPage: Int = 5,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<CoinDto>


    /**
     * Získa zoznam kryptomien podľa ID
     * @param vsCurrency Mena
     * @param ids Zoznam ID kryptomien
     * @return List kryptomien
     */
    @GET("coins/markets")
    suspend fun getCoinsByIds(
        @Query("vs_currency") vsCurrency: String = "eur",
        @Query("ids") ids: String
    ): List<CoinDto>

}

/**
 * Dátová trieda reprezentujúca kryptomenu z CoinGecko API
 * @property id ID kryptomeny
 * @property symbol Symbol kryptomeny
 * @property name Názov kryptomeny
 * @property image URL obrázka
 * @property current_price Aktuálna cena
 * @property market_cap Trhová kapitalizácia
 * @property price_change_percentage_24h Zmena ceny za posledných 24h
 */
data class CoinDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val current_price: Double,
    val market_cap: Double,
    val price_change_percentage_24h: Double
)