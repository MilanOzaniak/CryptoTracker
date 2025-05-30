package com.example.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptotracker.data.CryptoDatabase
import com.example.cryptotracker.repository.CryptoRepository
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = CryptoDatabase.getDatabase(applicationContext)
        val repository = CryptoRepository(db.cryptoDao())
        val viewModel = CryptoViewModel(repository)
        enableEdgeToEdge()
        setContent {
            val cryptos by viewModel.cryptos.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp), // posunutie dole
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { viewModel.insertTestCrypto() }) {
                    Text("Vložiť testovaciu kryptomenu")
                }

                LazyColumn {
                    items(cryptos) { crypto ->
                        Text("${crypto.name} (${crypto.symbol}) - ${crypto.amountOwned} ks za ${crypto.boughtSum} €")
                    }
                }
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CryptoTrackerTheme {
        Greeting("Android")
    }
}