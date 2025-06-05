package com.example.cryptotracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey

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
