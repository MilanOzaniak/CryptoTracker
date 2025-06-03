package com.example.cryptotracker.windows

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cryptotracker.CryptoViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.network.CoinDto

@Composable
fun DetailWindow(coinId: String, viewModel: CryptoViewModel, onBack: () -> Unit) {
    val savedCoins by viewModel.localCryptos.collectAsState()
    val coin = savedCoins.find { it.id == coinId }
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedAmount by remember { mutableStateOf("") }
    var editedPrice by remember { mutableStateOf("") }
    var showSwapDialog by remember { mutableStateOf(false) }
    var selectedNewCoin by remember { mutableStateOf<CoinDto?>(null) }
    var newAmount by remember { mutableStateOf("") }
    var overrideSum by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {

        if (coin != null) {
            Button(onClick = onBack) {
                Text("Back")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = coin.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Symbol: ${coin.symbol}")
            Text(text = "Amount: ${String.format("%.8f", coin.amountOwned)}")
            Text(text = "Bought Sum: ${String.format("%.2f €", coin.boughtSum)}")

            Text(text = "Price: ${coin.price} €")
            val actualProfit = (coin.price * coin.amountOwned) - coin.boughtSum
            Text(text = "Profit: %.2f €".format(actualProfit), color = if (actualProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)

            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Delete")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    editedAmount = coin.amountOwned.toString()
                    editedPrice = coin.price.toString()
                    showEditDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    showSwapDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Swap")
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmation") },
                    text = { Text("Do you want to delete?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteCrypto(coin)
                            showDialog = false
                            onBack()
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = editedAmount,
                                onValueChange = { editedAmount = it },
                                label = { Text("Amount") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editedPrice,
                                onValueChange = { editedPrice = it },
                                label = { Text("Price") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val amount = editedAmount.toDoubleOrNull()
                            val price = editedPrice.toDoubleOrNull()

                            val updated = coin.copy(
                                amountOwned = editedAmount.toDoubleOrNull() ?: coin.amountOwned,
                                price = editedPrice.toDoubleOrNull() ?: coin.price,
                                boughtSum = if (amount != null && price != null) amount * price else coin.boughtSum

                            )
                            viewModel.insertCrypto(updated)
                            showEditDialog = false
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showSwapDialog) {
                val allCoins by viewModel.coinGeckoCoins.collectAsState()
                val calculatedSum = (selectedNewCoin?.current_price ?: 0.0) * (newAmount.toDoubleOrNull() ?: 0.0)

                AlertDialog(
                    onDismissRequest = { showSwapDialog = false },
                    title = { Text("Swap") },
                    text = {
                        Column {
                            Text("Crypto: ${coin.name}")
                            Text("Price: ${String.format("%.4f €", coin.price)}")
                            Text("Amount: ${coin.amountOwned}")

                            Spacer(modifier = Modifier.height(8.dp))

                            CryptoDropdown(
                                coins = allCoins,
                                onSelected = { selectedNewCoin = it },
                                onLoadMore = { viewModel.loadCoinsFromApi() }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newAmount,
                                onValueChange = { newAmount = it },
                                label = { Text("Amount") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = overrideSum.ifBlank { String.format("%.2f", calculatedSum) },
                                onValueChange = { overrideSum = it },
                                label = { Text("Price €") },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val newCrypto = selectedNewCoin ?: return@TextButton
                            val amount = newAmount.toDoubleOrNull() ?: return@TextButton
                            val sum = overrideSum.toDoubleOrNull() ?: calculatedSum

                            val currentCoinValue = coin.amountOwned * coin.price

                            if (sum > currentCoinValue) {
                                return@TextButton
                            }

                            val remainingAmount = coin.amountOwned - (sum / coin.price)
                            val remainingBoughtSum = coin.boughtSum - sum

                            if (remainingAmount <= 0.0) {
                                viewModel.deleteCrypto(coin)
                            } else {
                                viewModel.modifyCrypto(
                                    coin.copy(
                                        amountOwned = remainingAmount,
                                        boughtSum = remainingBoughtSum
                                    )
                                )
                            }

                            viewModel.insertOrUpdateCrypto(newCrypto, amount)

                            showSwapDialog = false
                            onBack()
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSwapDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

        } else {
            Text("---------")
        }

    }
}