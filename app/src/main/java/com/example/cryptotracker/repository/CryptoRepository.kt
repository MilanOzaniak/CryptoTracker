package com.example.cryptotracker.repository
import com.example.cryptotracker.data.Crypto
import com.example.cryptotracker.data.CryptoDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository trieda pre správu kryptomien v lokálnej databáze
 * @property dao Referencia na DAO pre kryptomeny.
 */
class CryptoRepository(private val dao: CryptoDao) {
    /**
     * Zoznam všetkých kryptomien v lokálnej databáze
     * Dáta sa automaticky obnovujú pri zmene v lokálnej databáze
     */
    val allCryptos: Flow<List<Crypto>> = dao.getAllCryptos()

    /**
     * Vloží kryptomenu do lokálnej databázy.
     * @param crypto Kryptomena
     */
    suspend fun insert(crypto: Crypto) {
        dao.insertCrypto(crypto)
    }

    /**
     * Vymaže kryptomenu z lokálnej databázy
     * @param crypto Kryptomena
     */
    suspend fun delete(crypto: Crypto) {
        dao.deleteCrypto(crypto)
    }

    /**
     * Vymaže všetky kryptomeny z lokálnej databázy.
     */
    suspend fun deleteAll() {
        dao.deleteAll()
    }

    /**
     * Aktualizuje kryptomenu v lokálnej databáze
     *
     * @param crypto Kryptomena
     */
    suspend fun update(crypto: Crypto) {
        dao.update(crypto)
    }


    /**
     * Vráti kryptomenu podľa jej ID
     * @param id ID kryptomeny
     * @return Kryptomena s daným ID alebo null ak neexistuje
     */
    suspend fun getCryptoById(id: String): Crypto? {
        return dao.getCryptoById(id)
    }


}