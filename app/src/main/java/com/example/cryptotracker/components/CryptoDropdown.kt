package com.example.cryptotracker.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cryptotracker.network.CoinDto
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import coil.compose.AsyncImage
import com.example.cryptotracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoDropdown(
    coins: List<CoinDto>,
    onSelected: (CoinDto) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable  { mutableStateOf(false) }
    var search by rememberSaveable  { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    val filtered = coins.filter {
        it.name.contains(search, ignoreCase = true) || it.symbol.contains(search, ignoreCase = true)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth(0.8f)
    ) {
        TextField(
            value = search,
            onValueChange = {
                search = it
                expanded = true
            },
            label = { Text(stringResource(R.string.search)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.fillMaxWidth(0.74f)
        ) {
            val lastIndex = filtered.lastIndex
            filtered.forEachIndexed { index, coin ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = coin.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            )
                            Column {
                                Text(coin.name)
                                Text(coin.symbol.uppercase(), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    },
                    onClick = {
                        onSelected(coin)
                        search = coin.name
                        expanded = false
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
                if (index == lastIndex) {
                    LaunchedEffect(Unit) {
                        onLoadMore()
                    }
                }

            }
        }
    }
}