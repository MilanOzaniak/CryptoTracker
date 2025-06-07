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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.cryptotracker.R


@Composable
fun AddNotificationWindow(viewModel: CryptoViewModel, onBack: () -> Unit) {
    val allCoins by viewModel.coinGeckoCoins.collectAsState()
    var selectedCoinId by rememberSaveable { mutableStateOf("") }
    var triggerValue by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var choice by rememberSaveable { mutableStateOf(false) }

    val selectedCoin = allCoins.find { it.id == selectedCoinId }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.03f))


        Row( modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {

            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }

            IconButton(
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
                            targetPrice = value,
                            choice = choice

                        )
                        onBack()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Confirm",
                )
            }
        }

        Spacer(Modifier.fillMaxHeight(0.1f))
        Text(stringResource(R.string.price_notification), style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))

        CryptoDropdown(
            coins = allCoins,
            onSelected = { coin -> selectedCoinId = coin.id },
            onLoadMore = { viewModel.loadCoinsFromApi() }
        )
        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Button(
                onClick = { choice = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!choice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.above))
            }

            Button(
                onClick = { choice = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (choice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.less))
            }
        }
        Spacer(Modifier.height(16.dp))

        TextField(
            value = triggerValue,
            onValueChange = {
                triggerValue = it
                error = ""
            },
            label = { Text(stringResource(R.string.price)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = error.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))

    }
}