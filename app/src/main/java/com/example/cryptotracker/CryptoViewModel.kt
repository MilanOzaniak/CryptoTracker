package com.example.cryptotracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.Crypto
import com.example.cryptotracker.network.CoinDto
import com.example.cryptotracker.network.RetrofitInstance
import com.example.cryptotracker.repository.CryptoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CryptoViewModel(private val repository: CryptoRepository) : ViewModel() {

    private val _coinGeckoCoins = MutableStateFlow<List<CoinDto>>(emptyList())
    val coinGeckoCoins = _coinGeckoCoins.asStateFlow()
    val localCryptos: StateFlow<List<Crypto>> = repository.allCryptos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private var currentPage = 1
    private var isLoading = false
    private var allLoaded = false

    init {
        if (_coinGeckoCoins.value.isEmpty()) {
            loadCoinsFromApi()
        }
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

    fun insertCrypto(
        id: String,
        name: String,
        symbol: String,
        image: String,
        amountOwned: Double,
        boughtSum: Double,
        price: Double
    ) {
        viewModelScope.launch {
            repository.insert(
                Crypto(
                    id = id,
                    name = name,
                    symbol = symbol,
                    image = image,
                    amountOwned = amountOwned,
                    boughtSum = boughtSum,
                    price = price
                )
            )
        }
    }


}