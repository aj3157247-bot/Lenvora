package com.lenvora.app
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
@Composable fun CameraTranslateScreen(target:String="fa",onBack:()->Unit={}){
 val ctx=LocalContext.current;val owner=LocalLifecycleOwner.current;val scope=rememberCoroutineScope()
 var allowed by remember{mutableStateOf(ContextCompat.checkSelfPermission(ctx,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)}
 val ask=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){allowed=it}
 var scanner by remember{mutableStateOf<CameraScanner?>(null)};var result by remember{mutableStateOf<ScanResult?>(null)};var status by remember{mutableStateOf("Point camera at clear text")}
 val pipeline=remember{CameraOcrPipeline()}
 DisposableEffect(Unit){onDispose{pipeline.close()}}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  Text("Scan & Translate",style=MaterialTheme.typography.headlineSmall)
  if(!allowed){Text("Camera permission is required.");Button({ask.launch(Manifest.permission.CAMERA)}){Text("Allow Camera")}}
  else{
   AndroidView(factory={c->PreviewView(c).also{val s=CameraScanner(c,owner,it);scanner=s;s.start()}},modifier=Modifier.fillMaxWidth().weight(1f))
   Button(enabled=scanner!=null,onClick={scope.launch{status="Reading and translating…";try{result=pipeline.scanAndTranslate(scanner!!.captureBitmap(),target);status="Done"}catch(e:Exception){status=e.message?:"Failed"}}},modifier=Modifier.fillMaxWidth()){Text("Capture & Translate")}
   Text(status)
   result?.let{r->Card(Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp)){Text("Detected: ${r.detectedLanguage}");Text("Original");Text(r.originalText);Text("Translation");Text(r.translatedText)}}}
   OutlinedButton(onClick=onBack,modifier=Modifier.fillMaxWidth()){Text("Back")}
  }
 }
}
