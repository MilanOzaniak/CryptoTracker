package com.example.cryptotracker.windows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cryptotracker.CryptoViewModel
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.components.ErrorDialog
import com.example.cryptotracker.network.CoinDto
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cryptotracker.R
import com.example.cryptotracker.data.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable okno na pridanie novej kryptomeny do portfólia
 * @param viewModel Inštancia na prácu s kryptomenami a transakciami
 * @param onBack Callback, pre návrat na hlavnú obrazovku
 */
@Composable
fun AddWindow(viewModel: CryptoViewModel, onBack: () -> Unit) {
    var amount by rememberSaveable  { mutableStateOf("") }
    var price by rememberSaveable  { mutableStateOf("") }
    val allCoins by viewModel.coinGeckoCoins.collectAsState()
    var selectedCoinId by rememberSaveable { mutableStateOf("") }
    var showErrorDialog by rememberSaveable  { mutableStateOf(false) }
    var errorDialogMessage by rememberSaveable  { mutableStateOf<String?>(null) }
    val selectedCoin = allCoins.find { it.id == selectedCoinId }
    val context = LocalContext.current

    // Zobrazenie dialógu pri chybe
    if (errorDialogMessage != null) {
        ErrorDialog(
            message = errorDialogMessage ?: stringResource(R.string.unknown_error),
            onDismiss = {
                showErrorDialog = false
                errorDialogMessage = null
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.fillMaxHeight(0.03f))

        Row( modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {

            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorResource(R.color.icon_color)
                )
            }

            IconButton(
                onClick = {
                    val coin = selectedCoin
                    val newAmount = amount.toDoubleOrNull()

                    // Kontrola výberu coinu a správnosti množstva
                    if (coin == null) {
                        errorDialogMessage = context.getString(R.string.select_error)
                        showErrorDialog = true
                        return@IconButton
                    }

                    if (newAmount == null || newAmount <= 0) {
                        errorDialogMessage = context.getString(R.string.amount_error)
                        showErrorDialog = true
                        return@IconButton
                    }

                    // Pridanie alebo update kryptomeny v databáze
                    viewModel.insertOrUpdateCrypto(coin, newAmount, price.toDouble())

                    // Vytvorenie záznamu o transakcii
                    val trans = Transaction(
                        Type = "BUY",
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        description = coin.name + ", bought amount: " + newAmount
                    )

                    viewModel.insertTransaction(trans)
                    onBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Confirm",
                    tint = colorResource(R.color.icon_color)
                )
            }
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        Column(    modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(stringResource(R.string.add_crypto),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(18.dp),)

            CryptoDropdown(

                coins = allCoins,
                onSelected = { coin ->
                    selectedCoinId = coin.id
                    amount = ""
                    price = ""
                },
                onLoadMore = { viewModel.loadCoinsFromApi() },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(colorResource(R.color.main_color), shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(18.dp))

            TextField(
                value = amount,
                onValueChange = {
                    val cleaned = it.replace(',', '.')
                    if (cleaned.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        amount = it
                        val amt = it.toDoubleOrNull()
                        if (amt != null && selectedCoin != null) {
                            price = (amt * selectedCoin.current_price).toString()
                        }
                    }
                },
                label = { Text(stringResource(R.string.amount)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(colorResource(R.color.main_color), shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(18.dp))

            TextField(
                value = price,
                onValueChange = {
                    val cleaned = it.replace(',', '.')
                    if (cleaned.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        price = it
                    }
                },
                label = { Text(stringResource(R.string.price)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(colorResource(R.color.main_color), shape = RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

    }
}