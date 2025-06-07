package com.example.cryptotracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entita reprezentujúca kryptomenu uloženú v lokálnej databáze
 *
 * @property id Unikátne ID kryptomeny
 * @property name Názov kryptomeny
 * @property symbol Skratka/znak kryptomeny
 * @property price Aktuálna jednotková cena
 * @property amountOwned Počet kryptomien vlastnených používateľom
 * @property boughtSum Suma, za ktorú bola kryptomena nakúpená
 * @property image URL na obrázok kryptomeny
 * @property change Percentuálna zmena ceny za posledných 24h
 */
@Entity
data class Crypto(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val price: Double,
    val amountOwned: Double,
    val boughtSum: Double,
    val image: String,
    val change: Double
)
