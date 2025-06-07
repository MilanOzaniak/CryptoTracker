package com.example.cryptotracker.network
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.cryptotracker.BuildConfig

// šablóna nájdená na internete, upravená na moje požiadavky
/**
 * Objekt pre inicializáciu Retrofit klienta s CoinGecko API kľúčom
 * Pridáva hlavičku s API kľúčom do každého requestu
 */
object RetrofitInstance {

    //API kľúč uložený v BuildConfig
    private val apiKey = BuildConfig.COINGECKO_API_KEY

    // Nastavenie HTTP klienta, pridanie API kľúča do hlavičky, zapnutie logovania requestov a odpovedí

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-cg-demo-api-key", apiKey )
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    /**
     * Vytvorenie samotného API rozhrania (CoinGeckoService).
     * Používa sa na sťahovanie údajov o kryptomenách.
     * napr. (val coins = RetrofitInstance.api.getCoins())
     */
    val api: CoinGeckoService by lazy {

        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoService::class.java)
    }
}