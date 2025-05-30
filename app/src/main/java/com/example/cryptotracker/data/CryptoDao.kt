package com.example.cryptotracker.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoDao {
    @Query("SELECT * FROM Crypto")
    fun getAllCryptos(): Flow<List<Crypto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrypto(crypto: Crypto)

    @Delete
    suspend fun deleteCrypto(crypto: Crypto)

    @Query("DELETE FROM Crypto")
    suspend fun deleteAll()
}