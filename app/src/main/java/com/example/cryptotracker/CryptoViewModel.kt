package com.example.cryptotracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.Crypto
import com.example.cryptotracker.repository.CryptoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CryptoViewModel(private val repository: CryptoRepository) : ViewModel() {
    private val _cryptos = MutableStateFlow<List<Crypto>>(emptyList())
    val cryptos = _cryptos.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allCryptos.collect {
                _cryptos.value = it
            }
        }
    }

    fun insertTestCrypto() {
        viewModelScope.launch {
            val test = Crypto(
                id = "btc",
                name = "Bitcoin",
                symbol = "BTC",
                price = 45000.0,
                amountOwned = 0.5,
                boughtSum = 20000.0,
                image = "https://example.com/btc.png"
            )
            repository.insert(test)
        }
    }
}