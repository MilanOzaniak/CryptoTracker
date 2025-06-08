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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cryptotracker.R
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.components.TransactionItem
import com.example.cryptotracker.data.Transaction
import com.example.cryptotracker.network.CoinDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable okno pre detaily kryptomeny, zobrazuje základné údaje a transakcie
 *
 * @param coinId ID kryptomeny
 * @param viewModel
 * @param onBack Callback, pre návrat na hlavnú obrazovku
 */
@Composable
fun DetailWindow(coinId: String, viewModel: CryptoViewModel, onBack: () -> Unit) {
    val savedCoins by viewModel.localCryptos.collectAsState()
    val coin = savedCoins.find { it.id == coinId }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var editedAmount by rememberSaveable { mutableStateOf("") }
    var editedPrice by rememberSaveable { mutableStateOf("") }
    var showSwapDialog by rememberSaveable { mutableStateOf(false) }
    var selectedNewCoin by remember { mutableStateOf<CoinDto?>(null) }
    var newAmount by rememberSaveable { mutableStateOf("") }
    var overrideSum by rememberSaveable { mutableStateOf("") }
    val savedTransactions by viewModel.localTransactions.collectAsState()
    val filteredSortedTransactions = coin?.let { c ->
        savedTransactions
            .filter { it.description.contains(c.name, ignoreCase = true) }
            .sortedByDescending { it.id }
    } ?: emptyList()
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
                )
            }
        }

        if (coin != null) {
            val changeColor = if (coin.change >= 0) colorResource(R.color.green) else colorResource(R.color.red)

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
                        color = colorResource(R.color.gray)
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
                    color = if (profit >= 0) colorResource(R.color.green) else colorResource(R.color.red)
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
                        contentDescription = "Delete"
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
            Text(stringResource(R.string.history))
            Spacer(modifier = Modifier.fillMaxHeight(0.03f))


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) {
                items(filteredSortedTransactions, key = { it.id }) { trans ->
                    TransactionItem(trans)
                }
            }

            // delete dialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.confirmation)) },
                    text = { Text(stringResource(R.string.delete_question)) },
                    confirmButton = {
                        TextButton(onClick = {
                            // Pri zmazaní pridáme aj transakciu SELL
                            val trans = Transaction(
                                Type = "SELL",
                                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                                    Date()
                                ),
                                description ="Sold "+ coin.name + ", amount: " + coin.amountOwned + " " + coin.price
                            )

                            viewModel.insertTransaction(trans)
                            viewModel.deleteCrypto(coin)
                            showDialog = false
                            onBack()
                        }) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }

            // edit dialog
            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text(stringResource(R.string.edit)) },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = editedAmount,
                                onValueChange = { editedAmount = it },
                                label = { Text(stringResource(R.string.amount)) },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editedPrice,
                                onValueChange = { editedPrice = it },
                                label = { Text(stringResource(R.string.price))},
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            // Logika prepočíta boughtSum podľa zmien v amount/cene
                            val amount = editedAmount.toDoubleOrNull()
                            val price = editedPrice.toDoubleOrNull()

                            val updatedAmount = amount ?: coin.amountOwned
                            val updatedPrice = price ?: coin.price

                            val updatedBoughtSum = if (amount != null && amount < coin.amountOwned) {
                                // Ak používateľ zníži množstvo (predáva časť mincí), boughtSum sa zníži pomerne podľa podielu nového množstva
                                coin.boughtSum * (updatedAmount / coin.amountOwned)
                            } else if (amount != null && amount > coin.amountOwned) {
                                // Ak používateľ zvýši množstvo (dokúpi mince), k pôvodnému boughtSum sa pripočíta cena dokúpených mincí
                                val diff = amount - coin.amountOwned
                                coin.boughtSum + (diff * updatedPrice)
                            } else {
                                // Ak sa množstvo nemení alebo nastala chyba pri prevode, boughtSum ostáva nezmenený.
                                coin.boughtSum
                            }

                            val updated = coin.copy(
                                amountOwned = updatedAmount,
                                price = updatedPrice,
                                boughtSum = updatedBoughtSum
                            )
                            viewModel.insertCrypto(updated)

                            // Pridaj transakciu o zmene
                            val trans = Transaction(
                                Type = "MOD",
                                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                description = "Modified ${coin.name} (${coin.amountOwned}, ${coin.price}), -> (${updated.amountOwned} ${updated.price})"

                            )

                            viewModel.insertTransaction(trans)
                            showEditDialog = false
                        }) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }

            //Swap dialog
            if (showSwapDialog) {
                val allCoins by viewModel.coinGeckoCoins.collectAsState()
                val calculatedSum = (selectedNewCoin?.current_price ?: 0.0) * (newAmount.toDoubleOrNull() ?: 0.0)
                val scrollState = rememberScrollState()



                AlertDialog(
                    onDismissRequest = { showSwapDialog = false },
                    title = { Text(stringResource(R.string.swap)) },
                    text = {
                        Column (modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)){
                            Text("Crypto: ${coin.name}")
                            Text("Price: ${String.format("%.4f €", coin.price)}")
                            Text("Amount: ${coin.amountOwned}")


                            CryptoDropdown(
                                coins = allCoins,
                                onSelected = { selectedNewCoin = it },
                                onLoadMore = { viewModel.loadCoinsFromApi() },
                                modifier = Modifier.fillMaxWidth(1f)
                            )


                            OutlinedTextField(
                                value = newAmount,
                                onValueChange = { newAmount = it },
                                label = { Text(stringResource(R.string.amount)) },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = overrideSum.ifBlank { String.format("%.2f", calculatedSum) },
                                onValueChange = { overrideSum = it },
                                label = { Text(stringResource(R.string.price)) },
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

                            //nemôžeš swapnúť za viac než máš
                            if (sum > currentCoinValue) {
                                return@TextButton
                            }

                            val remainingAmount = coin.amountOwned - (sum / coin.price)
                            val remainingBoughtSum = coin.boughtSum - sum

                            //Ak si swapol všetko, zmaž pôvodnú mincu
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
                            // Pridanie noveho crypta
                            viewModel.insertOrUpdateCrypto(
                                newCrypto,
                                amount,
                                price = sum / amount
                            )

                            val amountSwapped = sum / coin.price
                            val trans = Transaction(
                                Type = "SWAP",
                                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                description = "Swapped ${coin.name} (amount: %.4f), for ${newCrypto.name} (amount: %.4f)".format(amountSwapped, amount)
                            )

                            viewModel.insertTransaction(trans)
                            showSwapDialog = false
                            onBack()
                        }) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSwapDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }

        } else {
            Text(stringResource(R.string.blank))
        }

    }
}