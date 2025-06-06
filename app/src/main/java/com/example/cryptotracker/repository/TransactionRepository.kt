package com.example.cryptotracker.repository

import com.example.cryptotracker.data.Transaction
import com.example.cryptotracker.data.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    suspend fun insert(trans: Transaction){
        dao.insertTransaction(trans)
    }
}