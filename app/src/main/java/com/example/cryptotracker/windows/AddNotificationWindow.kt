package com.example.cryptotracker.windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cryptotracker.CryptoViewModel
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.network.CoinDto
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext


@Composable
fun AddNotificationWindow(viewModel: CryptoViewModel, onBack: () -> Unit) {
    val allCoins by viewModel.coinGeckoCoins.collectAsState()
    var selectedCoinId by rememberSaveable { mutableStateOf("") }
    var triggerValue by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val selectedCoin = allCoins.find { it.id == selectedCoinId }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("Set Price Notification", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))

        CryptoDropdown(
            coins = allCoins,
            onSelected = { coin -> selectedCoinId = coin.id },
            onLoadMore = { viewModel.loadCoinsFromApi() }
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            value = triggerValue,
            onValueChange = {
                triggerValue = it
                error = ""
            },
            label = { Text("Notify when price exceeds (€)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = error.isNotEmpty()
        )

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val value = triggerValue.toDoubleOrNull()
                if (selectedCoin == null) {
                    error = "Please select a coin."
                } else if (value == null) {
                    error = "Enter a valid numeric value."
                } else {
                    viewModel.scheduleNotification(
                        context = context,
                        coin = selectedCoin,
                        targetPrice = value
                    )
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm")
        }
    }
}