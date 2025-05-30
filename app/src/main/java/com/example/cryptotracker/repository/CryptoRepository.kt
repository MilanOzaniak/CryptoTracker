package com.example.cryptotracker.repository
import com.example.cryptotracker.data.Crypto
import com.example.cryptotracker.data.CryptoDao
import kotlinx.coroutines.flow.Flow

class CryptoRepository(private val dao: CryptoDao) {
    val allCryptos: Flow<List<Crypto>> = dao.getAllCryptos()

    suspend fun insert(crypto: Crypto) {
        dao.insertCrypto(crypto)
    }

    suspend fun delete(crypto: Crypto) {
        dao.deleteCrypto(crypto)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}