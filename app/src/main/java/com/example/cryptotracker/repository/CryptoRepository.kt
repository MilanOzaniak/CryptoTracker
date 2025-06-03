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

    suspend fun update(crypto: Crypto) {
        dao.update(crypto)
    }

    suspend fun updateManually(crypto: Crypto) {
        dao.updateCrypto(
            id = crypto.id,
            name = crypto.name,
            symbol = crypto.symbol,
            image = crypto.image,
            amountOwned = crypto.amountOwned,
            boughtSum = crypto.boughtSum,
            price = crypto.price
        )
    }

    suspend fun getCryptoById(id: String): Crypto? {
        return dao.getCryptoById(id)
    }


}