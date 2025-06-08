package com.example.cryptotracker.windows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.example.cryptotracker.CryptoViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.cryptotracker.R
import com.example.cryptotracker.components.CryptoDropdown
import com.example.cryptotracker.components.CryptoItem
import kotlinx.coroutines.flow.StateFlow

/**
 * Hlavné okno aplikácie
 *
 * @param viewModel
 * @param onNavigateToAddForm Callback na navigáciu do okna AddWindow
 * @param onNavigateToDetail Callback na navigáciu do okna DetailWindow
 * @param onNavigateToNotification Callback na navigáciu do okna AddNotificationWindow
 * @param onNavigateToHistory Callback na navigáciu do okna TransactionWindow
 */

@Composable
fun MainWindow(
    viewModel: CryptoViewModel,
    onNavigateToAddForm: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val savedCryptos by viewModel.localCryptos.collectAsState()
    val totalValue by viewModel.portfolioValue.collectAsState()
    val profit = savedCryptos.sumOf { it.price * it.amountOwned - it.boughtSum }
    val isProfit = profit >= 0
    val profitColor = if (isProfit) colorResource(R.color.green) else colorResource(R.color.red)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.main_color)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.portfolio_value),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "€${String.format("%.2f", totalValue)}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)){
                        Text(
                            text = "${String.format("%.2f", profit)}",
                            color = profitColor,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = stringResource(R.string.portfolio_profit),
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onNavigateToAddForm,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(stringResource(R.string.add_transaction))
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
            IconButton(onClick = onNavigateToNotification) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Add notification"
                )
            }
            IconButton(onClick = onNavigateToHistory) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Transaction history"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f)
        ) {
            items(savedCryptos, key = { it.id }) { crypto ->
                CryptoItem(crypto, onClick = {
                    onNavigateToDetail(crypto.id)
                })
            }
        }
    }
}