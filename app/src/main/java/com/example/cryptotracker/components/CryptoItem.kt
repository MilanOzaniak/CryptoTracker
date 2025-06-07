package com.example.cryptotracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cryptotracker.CryptoViewModel
import com.example.cryptotracker.R
import com.example.cryptotracker.data.Crypto

@Composable
fun CryptoItem(crypto: Crypto, onClick: () -> Unit) {
    val profit = (crypto.price * crypto.amountOwned) - crypto.boughtSum

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.main_color)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = crypto.image,
                contentDescription = crypto.name,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = crypto.name, style = MaterialTheme.typography.bodyMedium)
                Text(text = crypto.symbol.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(40.dp)
                    .padding(end = 4.dp)
            ) {
                Text(
                    text = String.format("%+.2f €", profit),
                    color = if (profit >= 0) colorResource(R.color.green) else colorResource(R.color.red),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = String.format("%.4f €", crypto.amountOwned * crypto.price),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}