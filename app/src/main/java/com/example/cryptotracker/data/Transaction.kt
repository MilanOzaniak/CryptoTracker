package com.example.cryptotracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity trieda reprezentujúca jednu transakciu v lokálnej databáze
 *
 * @property id Primárny kľúč je automaticky generovaný
 * @property date Dátum transakcie vo formáte String.
 * @property description Popis transakcie.
 * @property Type Typ transakcie.
 */
@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val description: String,
    val Type: String
    )
