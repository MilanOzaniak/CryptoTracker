package com.example.cryptotracker.windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.example.cryptotracker.CryptoViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.components.CryptoItem

@Composable
fun MainWindow(
    viewModel: CryptoViewModel,
    onNavigateToAddForm: () -> Unit
) {
    val allCoins by viewModel.coinGeckoCoins.collectAsState()
    var selectedCoin by remember { mutableStateOf("") }
    val savedCryptos by viewModel.localCryptos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = onNavigateToAddForm) {
            Text("Pridať kryptomenu")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(savedCryptos) { crypto ->
                CryptoItem(crypto)
            }
        }
    }
}