package com.example.cryptotracker

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.Crypto
import com.example.cryptotracker.data.Transaction
import com.example.cryptotracker.network.CoinDto
import com.example.cryptotracker.network.RetrofitInstance
import com.example.cryptotracker.notifications.cryptoNotification
import com.example.cryptotracker.repository.CryptoRepository
import com.example.cryptotracker.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CryptoViewModel(private val Crepository: CryptoRepository, private val Trepository: TransactionRepository) : ViewModel() {

    private val _coinGeckoCoins = MutableStateFlow<List<CoinDto>>(emptyList())
    val coinGeckoCoins = _coinGeckoCoins.asStateFlow()
    val localCryptos: StateFlow<List<Crypto>> = Crepository.allCryptos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val localTransactions: StateFlow<List<Transaction>> = Trepository.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList() )
    private var currentPage = 1
    private var isLoading = false
    private var allLoaded = false
    private var isWriting = false
    val portfolioValue: StateFlow<Double> = localCryptos
        .map { list -> list.sumOf { it.amountOwned * it.price } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        if (_coinGeckoCoins.value.isEmpty()) {
            loadCoinsFromApi()
        }
        startPriceUpdater()
    }

    fun loadCoinsFromApi() {
        if (isLoading || allLoaded) return
        isLoading = true

        viewModelScope.launch {
            try {
                val newCoins = RetrofitInstance.api.getCoins(page = currentPage)
                if (newCoins.isEmpty()) {
                    allLoaded = true
                } else {
                    _coinGeckoCoins.value = _coinGeckoCoins.value + newCoins
                    currentPage++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun insertOrUpdateCrypto(
        coin: CoinDto,
        amount: Double,
        price: Double
    ) {
        viewModelScope.launch {
            isWriting = true
            val existing = withContext(Dispatchers.IO) { Crepository.getCryptoById(coin.id) }

             if (existing != null) {

                 val crypto = Crypto(
                     id = coin.id,
                     name = coin.name,
                     symbol = coin.symbol,
                     image = coin.image,
                     amountOwned = existing.amountOwned + amount,
                     boughtSum = existing.boughtSum + (price * amount),
                     price = coin.current_price,
                     change = coin.price_change_percentage_24h
                 )
                 Crepository.insert(crypto)


            } else {
                val crypto = Crypto(
                    id = coin.id,
                    name = coin.name,
                    symbol = coin.symbol,
                    image = coin.image,
                    amountOwned = amount,
                    boughtSum = price * amount,
                    price = coin.current_price,
                    change = coin.price_change_percentage_24h
                )
                 Crepository.insert(crypto)
            }
            isWriting = false
        }
    }

    fun insertCrypto(coin: Crypto){
        viewModelScope.launch {
            val crypto = Crypto(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol,
                image = coin.image,
                amountOwned = coin.amountOwned,
                boughtSum = coin.boughtSum,
                price = coin.price,
                change = coin.change
            )
            Crepository.insert(crypto)
        }
    }

    fun deleteCrypto(crypto: Crypto) {
        viewModelScope.launch {
            Crepository.delete(crypto)
        }
    }

    fun modifyCrypto(updatedCrypto: Crypto) {
        viewModelScope.launch {
            Crepository.update(updatedCrypto)
        }
    }

    fun startPriceUpdater() {
        viewModelScope.launch {
            while (true) {
                updatePrices()
                delay(20000)
            }
        }
    }

    private suspend fun updatePrices() {
        if (isWriting) return

        val savedCryptos = Crepository.allCryptos.first()

        val ids = savedCryptos.joinToString(",") { it.id }

        try {
            val updatedList = RetrofitInstance.api.getCoinsByIds(ids = ids)

            updatedList.forEach { updated ->
                val original = savedCryptos.find { it.id == updated.id } ?: return@forEach
                Crepository.update(original.copy(price = updated.current_price, change = updated.price_change_percentage_24h) )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun insertTransaction(trans: Transaction){
        viewModelScope.launch {
            val transaction = Transaction(
                id = trans.id,
                Type = trans.Type,
                date = trans.date,
                description = trans.description
            )
            Trepository.insert(transaction)
        }
    }

    fun scheduleNotification(
        context: Context,
        coin: CoinDto,
        targetPrice: Double,
        choice: Boolean
    ) {
        cryptoNotification(context = context, coin = coin, targetPrice = targetPrice, choice)
    }


}