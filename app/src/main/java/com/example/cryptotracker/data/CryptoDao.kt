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

    @Query("""
    UPDATE Crypto 
    SET 
        name = :name,
        symbol = :symbol,
        image = :image,
        amountOwned = :amountOwned,
        boughtSum = :boughtSum,
        price = :price
    WHERE id = :id
""")
    suspend fun updateCrypto(
        id: String,
        name: String,
        symbol: String,
        image: String,
        amountOwned: Double,
        boughtSum: Double,
        price: Double
    )

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(crypto: Crypto)

    @Query("SELECT * FROM Crypto WHERE id = :id LIMIT 1")
    suspend fun getCryptoById(id: String): Crypto?
}