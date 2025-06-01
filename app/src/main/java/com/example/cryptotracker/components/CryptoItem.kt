package com.example.cryptotracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cryptotracker.CryptoViewModel
import com.example.cryptotracker.data.Crypto

@Composable
fun CryptoItem(crypto: Crypto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = crypto.image,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    "${crypto.name} (${crypto.symbol.uppercase()})",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Množstvo: ${crypto.amountOwned}")
                Text("Kúpené za: ${crypto.boughtSum} €")
                Text("Aktuálna cena: ${crypto.price} €")
            }
        }
    }
}