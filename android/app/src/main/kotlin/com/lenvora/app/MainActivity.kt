package com.lenvora.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lenvora.app.data.*
import kotlinx.coroutines.launch

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:android.os.Bundle){
        super.onCreate(savedInstanceState)
        setContent{LenvoraApp()}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LenvoraApp(){
    var fa by remember{mutableStateOf(true)}
    var camera by remember{mutableStateOf(false)}
    var selected by remember{mutableStateOf("dictionary")}
    MaterialTheme{
        if(camera) CameraScreen(fa){camera=false}
        else Scaffold(topBar={TopAppBar(title={Text(AppStrings.title(fa))},actions={
            TextButton({fa=!fa}){Text(if(fa)"EN" else "فا")}
        })},bottomBar={
            NavigationBar{
                NavigationBarItem(selected=="dictionary",{selected="dictionary"},icon={},label={Text(AppStrings.dictionary(fa))})
                NavigationBarItem(selected=="favorites",{selected="favorites"},icon={},label={Text(AppStrings.favorites(fa))})
                NavigationBarItem(selected=="history",{selected="history"},icon={},label={Text(AppStrings.history(fa))})
            }
        }){p->
            when(selected){
                "dictionary"->DictionaryScreen(fa,Modifier.padding(p)){camera=true}
                "favorites"->FavoritesScreen(Modifier.padding(p))
                else->HistoryScreen(Modifier.padding(p))
            }
        }
    }
}

@Composable
fun DictionaryScreen(fa:Boolean,modifier:Modifier,onCamera:()->Unit,vm:DictionaryViewModel=viewModel()){
    val q by vm.query.collectAsState(); val results by vm.results.collectAsState()
    Column(modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
        Text(AppStrings.dictionary(fa),style=MaterialTheme.typography.headlineSmall)
        OutlinedTextField(q,vm::setQuery,Modifier.fillMaxWidth(),label={Text(AppStrings.search(fa))})
        Button(onCamera,Modifier.fillMaxWidth()){Text(AppStrings.camera(fa))}
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){
            items(results,key={it.id}){item->
                Card(Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp)){
                    Row(Modifier.fillMaxWidth()){Column(Modifier.weight(1f)){Text(item.word,style=MaterialTheme.typography.titleLarge);item.pronunciation?.let{Text(it)}}
                        TextButton({vm.toggleFavorite(item)}){Text(if(item.isFavorite)"★" else "☆")}}
                    Text(item.meaning);item.example?.let{Text(it,style=MaterialTheme.typography.bodySmall)}
                }}
            }
        }
    }
}

@Composable fun FavoritesScreen(modifier:Modifier,vm:DictionaryViewModel=viewModel()){
    val items by vm.favorites.collectAsState()
    LazyColumn(modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){items(items){Card(Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp)){Text(it.word);Text(it.meaning)}}}}
}
@Composable fun HistoryScreen(modifier:Modifier,vm:DictionaryViewModel=viewModel()){
    val items by vm.history.collectAsState()
    LazyColumn(modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){items(items){Card(Modifier.fillMaxWidth()){Text("${it.query} • ${it.language}",Modifier.padding(14.dp))}}}
}

@Composable
fun CameraScreen(fa:Boolean,onBack:()->Unit){
    val ctx=LocalContext.current; val owner=LocalLifecycleOwner.current; val scope=rememberCoroutineScope()
    var allowed by remember{mutableStateOf(ContextCompat.checkSelfPermission(ctx,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)}
    val ask=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){allowed=it}
    var scanner by remember{mutableStateOf<CameraScanner?>(null)}; var result by remember{mutableStateOf<ScanResult?>(null)}; var status by remember{mutableStateOf("")}
    val pipeline=remember{CameraOcrPipeline()}; DisposableEffect(Unit){onDispose{pipeline.close()}}
    Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
        Text(AppStrings.camera(fa),style=MaterialTheme.typography.headlineSmall)
        if(!allowed){Button({ask.launch(Manifest.permission.CAMERA)}){Text(if(fa)"اجازه دوربین" else "Allow Camera")}}
        else{
            AndroidView({c->PreviewView(c).also{val s=CameraScanner(c,owner,it);scanner=s;s.start()}},Modifier.fillMaxWidth().weight(1f))
            Button({scope.launch{status=if(fa)"در حال پردازش..." else "Processing...";try{result=pipeline.run(scanner!!.take(),"fa");status=if(fa)"تمام شد" else "Done"}catch(e:Exception){status=e.message?:"Error"}}},enabled=scanner!=null,Modifier.fillMaxWidth()){Text(AppStrings.translate(fa))}
            Text(status);result?.let{Card(Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp)){Text(it.originalText);Text(it.translatedText,style=MaterialTheme.typography.titleLarge)}}}
            OutlinedButton(onBack,Modifier.fillMaxWidth()){Text(if(fa)"بازگشت" else "Back")}
        }
    }
}
