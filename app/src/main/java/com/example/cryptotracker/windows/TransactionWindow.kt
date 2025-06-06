package com.example.cryptotracker.windows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.room.util.TableInfo
import com.example.cryptotracker.CryptoViewModel
import com.example.cryptotracker.components.CryptoItem
import com.example.cryptotracker.components.TransactionItem

@Composable
fun TransactionWindow(viewModel: CryptoViewModel, onBack: () -> Unit){

    val savedTransactions by viewModel.localTransactions.collectAsState()


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.6f)
        ) {
            items(savedTransactions, key = { it.id }) { trans ->
                TransactionItem(trans)
            }
        }
    }
}