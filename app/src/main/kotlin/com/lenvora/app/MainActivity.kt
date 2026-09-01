package com.lenvora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LenvoraApp() }
    }
}

@Composable
fun LenvoraApp() {
    val scope = rememberCoroutineScope()
    var source by remember { mutableStateOf("en") }
    var target by remember { mutableStateOf("fa") }
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Ready") }
    val translator = remember { OfflineTranslator() }

    MaterialTheme {
        Scaffold(topBar={TopAppBar(title={Text("Lenvora V2")})}) { padding ->
            Column(
                Modifier.padding(padding).padding(20.dp),
                verticalArrangement=Arrangement.spacedBy(12.dp)
            ) {
                Text("Offline Translation", style=MaterialTheme.typography.headlineSmall)
                Text("Models are kept on-device after download.", style=MaterialTheme.typography.bodySmall)

                OutlinedTextField(
                    value=input, onValueChange={input=it},
                    modifier=Modifier.fillMaxWidth(),
                    label={Text("Word or sentence")}
                )

                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    Button(onClick={ source = if(source=="en") "fa" else "en" }) { Text("From: $source") }
                    Button(onClick={ target = if(target=="fa") "en" else "fa" }) { Text("To: $target") }
                }

                Button(
                    enabled=input.isNotBlank(),
                    onClick={
                        scope.launch {
                            status="Preparing offline model…"
                            try {
                                result=translator.translate(input, source, target)
                                status="Translated offline"
                            } catch(e:Exception) {
                                status=e.message ?: "Translation failed"
                            }
                        },
                    },
                    modifier=Modifier.fillMaxWidth()
                ) { Text("Translate") }

                OutlinedButton(
                    onClick={ status="Camera/OCR screen is ready for CameraX integration" },
                    modifier=Modifier.fillMaxWidth()
                ) { Text("📷 Scan text from camera") }

                Text(status)
                if(result.isNotBlank()) Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Translation", style=MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(result)
                    }
                }
            }
        }
    }
}
