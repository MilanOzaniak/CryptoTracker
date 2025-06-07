package com.example.cryptotracker.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO rozhranie pre operácie nad tabuľkou v lokálnej databáze
 */
@Dao
interface CryptoDao {
    /**
     * Získa všetky kryptomeny v databáze a vráti ako Flow
     */
    @Query("SELECT * FROM Crypto")
    fun getAllCryptos(): Flow<List<Crypto>>

    /**
     * Vloží kryptomenu do lokálnej databázy.
     * @param crypto Kryptomena na vloženie
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrypto(crypto: Crypto)

    /**
     * Vymaže konkrétnu kryptomenu z lokálnej databázy
     * @param crypto Kryptomena na vymazanie
     */
    @Delete
    suspend fun deleteCrypto(crypto: Crypto)

    /**
     * Vymaže všetky kryptomeny z lokálnej databázy
     */
    @Query("DELETE FROM Crypto")
    suspend fun deleteAll()

    /**
     * Aktualizuje kryptomenu podľa ID v lokálnej databáze
     */
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


    /**
     * Aktualizuje kryptomenu na základe objektu Crypto v lokálnej databáze
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(crypto: Crypto)

    /**
     * Získa kryptomenu podľa ID z lokálnej databázy
     */
    @Query("SELECT * FROM Crypto WHERE id = :id LIMIT 1")
    suspend fun getCryptoById(id: String): Crypto?
}