package com.example.cryptotracker.windows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
        .fillMaxSize())
    {

        Spacer(modifier = Modifier.fillMaxHeight(0.03f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        if (coin != null) {
            val changeColor = if (coin.change >= 0) Color(0xFF2E7D32) else Color(0xFFD32F2F)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = coin.image,
                    contentDescription = coin.name,
                    modifier = Modifier
                        .size(128.dp)
                        .padding(end = 16.dp)
                )

                Column {
                    Text(
                        text = coin.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = coin.symbol.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Price: %.4f €".format(coin.price))
                    Text(
                        text = String.format("%.2f %%", coin.change),
                        color = changeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(Modifier.padding(start = 32.dp)) {
                Text("Amount: %.4f".format(coin.amountOwned), fontSize = 20.sp)
                Text("Owned: %.2f €".format(coin.amountOwned * coin.price), fontSize = 20.sp)
                val profit = coin.amountOwned * coin.price - coin.boughtSum
                Text(
                    text = "Profit: %+.2f€".format(profit),
                    color = if (profit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    editedAmount = coin.amountOwned.toString()
                    editedPrice = coin.price.toString()
                    showEditDialog = true
                }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }

                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black
                    )
                }

                IconButton(
                    onClick = { showSwapDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap"
                    )
                }
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