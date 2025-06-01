package com.example.cryptotracker.windows

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cryptotracker.CryptoViewModel
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.network.CoinDto

@Composable
fun AddWindow(viewModel: CryptoViewModel, onBack: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val allCoins by viewModel.coinGeckoCoins.collectAsState()
    var selectedCoin by remember { mutableStateOf<CoinDto?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(8.dp))

        CryptoDropdown(
            coins = allCoins,
            onSelected = {
                selectedCoin = it
            },
            onLoadMore = {
                viewModel.loadCoinsFromApi()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = amount,
            onValueChange = {
                val cleaned = it.replace(',', '.')
                if (cleaned.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    amount = it
                    val amt = it.toDoubleOrNull()
                    val coin = selectedCoin
                    if (amt != null && coin != null) {
                        price = (amt * coin.current_price).toString()
                    }
                }
            },
            label = { Text("Zadaj množstvo") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = price,
            onValueChange = {
                val cleaned = it.replace(',', '.')
                if (cleaned.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    price = it
                }
            },
            label = { Text("Zadaj cenu") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val coin = selectedCoin
            if (coin != null && amount.isNotBlank() && price.isNotBlank()) {
                viewModel.insertCrypto(
                    id = coin.id,
                    name = coin.name,
                    symbol = coin.symbol,
                    image = coin.image,
                    amountOwned = amount.toDouble(),
                    boughtSum = price.toDouble(),
                    price = coin.current_price
                )
                onBack()
            }
        }) {
            Text("Uložiť a späť")
        }
    }
}