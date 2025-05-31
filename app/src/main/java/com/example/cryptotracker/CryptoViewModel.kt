package com.example.cryptotracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.Crypto
import com.example.cryptotracker.network.CoinDto
import com.example.cryptotracker.network.RetrofitInstance
import com.example.cryptotracker.repository.CryptoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CryptoViewModel(private val repository: CryptoRepository) : ViewModel() {

    private val _allCryptos = MutableStateFlow<List<Crypto>>(emptyList())
    private val _filteredCryptos = MutableStateFlow<List<Crypto>>(emptyList())
    val filteredCryptos = _filteredCryptos.asStateFlow()

    private val _coinGeckoCoins = MutableStateFlow<List<CoinDto>>(emptyList())
    val coinGeckoCoins = _coinGeckoCoins.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allCryptos.collect {
                _allCryptos.value = it
                _filteredCryptos.value = it
            }
        }
        loadCoinsFromApi()
    }

    fun search(query: String) {
        _filteredCryptos.value = _allCryptos.value.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.symbol.contains(query, ignoreCase = true)
        }
    }

    fun loadCoinsFromApi() {
        viewModelScope.launch {
            try {
                val coins = RetrofitInstance.api.getAllCoins()
                _coinGeckoCoins.value = coins
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}