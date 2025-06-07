package com.example.cryptotracker.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cryptotracker.data.Transaction

/**
 * Zobrazuje jednu položku transakcie v zozname
 *
 * @param trans Transakcia
 */
@Composable
fun TransactionItem(trans: Transaction){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF2F2F2)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //ID transakcie
            Text(
                text = trans.id.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(0.1f)
            )

            //Typ a dátum
            Column(
                modifier = Modifier
                    .weight(0.2f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = trans.Type,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = trans.date.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            //Popis transakcie
            Text(
                text = trans.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp)
            )
        }
    }
}

