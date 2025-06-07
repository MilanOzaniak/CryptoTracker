package com.example.cryptotracker.repository

import com.example.cryptotracker.data.Transaction
import com.example.cryptotracker.data.TransactionDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository trieda pre správu transakcií v lokálnej databáze
 * @property dao DAO pre transakcie
 */
class TransactionRepository(private val dao: TransactionDao) {

    /**
     * Zoznam všetkých transakcií v lokálnej databáze
     * Dáta sa automaticky obnovujú pri zmene v lokálnej databáze
     */
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    /**
     * Vloží transakciu do lokálnej databázy
     * @param trans Transakcia, ktorú chceš vložiť
     */
    suspend fun insert(trans: Transaction){
        dao.insertTransaction(trans)
    }
}