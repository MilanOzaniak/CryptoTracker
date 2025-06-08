package com.example.cryptotracker.windows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.TableInfo
import com.example.cryptotracker.CryptoViewModel
import com.example.cryptotracker.R
import com.example.cryptotracker.components.CryptoItem
import com.example.cryptotracker.components.TransactionItem

/**
 * Composable okno zobrazuje zoznam všetkých transakcií v aplikácii
 * @param viewModel
 * @param onBack Callback, pre návrat na hlavnú obrazovku
 */
@Composable
fun TransactionWindow(viewModel: CryptoViewModel, onBack: () -> Unit){

    val savedTransactions by viewModel.localTransactions.collectAsState()
    val sortedTransactions = savedTransactions.sortedByDescending { it.id }


    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
        Text(stringResource(R.string.history),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.fillMaxHeight(0.03f))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight()
        ) {
            items(sortedTransactions, key = { it.id }) { trans ->
                TransactionItem(trans)
            }
        }
    }
}