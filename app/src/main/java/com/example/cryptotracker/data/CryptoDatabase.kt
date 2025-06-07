package com.example.cryptotracker.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// šablóna nájdená na oficálnej stránke, upravená na moje požiadavky
// platí pre celý data package
// https://developer.android.com/training/data-storage/room

/**
 * Lokálna databáza pre aplikáciu
 * Obsahuje tabuľky Crypto a Transaction.
 * @property cryptoDao DAO pre prácu s kryptomenami
 * @property transDao DAO pre prácu s transakciami
 */
@Database(entities = [Crypto::class, Transaction::class], version = 3, exportSchema = false)
abstract class CryptoDatabase : RoomDatabase() {

    /**
     * Získa DAO pre tabuľku Crypto
     */
    abstract fun cryptoDao(): CryptoDao

    /**
     * Získa DAO pre tabuľku Transaction
     */
    abstract fun transDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: CryptoDatabase? = null

        /**
         * Získa inštanciu databázy.
         *
         * @param context Kontext aplikácie
         * @return Inštancia CryptoDatabase
         */
        fun getDatabase(context: Context): CryptoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CryptoDatabase::class.java,
                    "crypto_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}