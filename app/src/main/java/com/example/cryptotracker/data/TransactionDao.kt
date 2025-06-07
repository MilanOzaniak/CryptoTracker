package com.example.cryptotracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO rozhranie pre správu transakcií v lokálnej databáze
 */
@Dao
interface TransactionDao {

    /**
     * Získa všetky transakcie uložené v lokálnej databáze a vráti ako Flow zoznam
     */
    @Query("SELECT * FROM `Transaction`")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Vloží novú transakciu do databázy,
     * ak už existuje rovnaký primárny kľúč tak ju prepíše
     * @param trans Transakcia na vloženie
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(trans: Transaction)
}