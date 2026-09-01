package com.lenvora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LenvoraApp() }
    }
}

@Composable
fun LenvoraApp() {
    var query by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("English") }
    var target by remember { mutableStateOf("فارسی") }

    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Lenvora V2") }) }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Offline Dictionary", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value=query, onValueChange={query=it},
                    modifier=Modifier.fillMaxWidth(),
                    label={Text("Word or sentence")}
                )
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick={source=if(source=="English")"فارسی" else "English"},
                        label={Text("From: $source")})
                    AssistChip(onClick={target=if(target=="فارسی")"English" else "فارسی"},
                        label={Text("To: $target")})
                }
                Button(onClick={}, modifier=Modifier.fillMaxWidth()) { Text("Translate") }
                OutlinedButton(onClick={}, modifier=Modifier.fillMaxWidth()) { Text("📷 Translate from Camera") }
                if(query.isNotBlank()) {
                    Card(modifier=Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Result", style=MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("Offline translation engine will be connected here.")
                        }
                    }
                }
            }
        }
    }
}
