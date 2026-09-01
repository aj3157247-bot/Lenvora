package com.lenvora.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lenvora.app.data.DictionaryViewModel

@Composable
fun DictionaryScreen(vm: DictionaryViewModel = viewModel()) {
    val query by vm.query.collectAsState()
    val results by vm.results.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Offline Dictionary", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = query,
            onValueChange = vm::setQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search word or meaning") }
        )

        Text("Works without internet", style = MaterialTheme.typography.bodySmall)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results, key = { it.id }) { item ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                Text(item.word, style = MaterialTheme.typography.titleLarge)
                                item.pronunciation?.let { Text(it) }
                                item.partOfSpeech?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                            }
                            TextButton(onClick = { vm.toggleFavorite(item) }) {
                                Text(if (item.isFavorite) "★" else "☆")
                            }
                        }
                        Text(item.meaning)
                        item.example?.let {
                            Text("Example: $it", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
