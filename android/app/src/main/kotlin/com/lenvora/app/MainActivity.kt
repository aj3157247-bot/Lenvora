package com.lenvora.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lenvora.app.data.DictionaryEntity
import com.lenvora.app.data.DictionaryViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LenvoraApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LenvoraApp() {
    var fa by rememberSaveable { mutableStateOf(true) }
    var screen by rememberSaveable { mutableStateOf("home") }
    var camera by rememberSaveable { mutableStateOf(false) }

    MaterialTheme {
        if (camera) {
            CameraScreen(fa = fa, onBack = { camera = false })
        } else {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("LENVORA", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                                Text(
                                    if (fa) "دیکشنری هوشمند" else "Smart Dictionary",
                                    fontSize = 11.sp
                                )
                            }
                        },
                        actions = {
                            TextButton(onClick = { fa = !fa }) {
                                Text(if (fa) "EN" else "فا")
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        NavItem("home", screen, { screen = "home" }, Icons.Default.Home, if (fa) "خانه" else "Home")
                        NavItem("dictionary", screen, { screen = "dictionary" }, Icons.Default.Search, if (fa) "دیکشنری" else "Dictionary")
                        NavItem("favorites", screen, { screen = "favorites" }, Icons.Default.Star, if (fa) "ذخیره‌ها" else "Saved")
                        NavItem("history", screen, { screen = "history" }, Icons.Default.History, if (fa) "تاریخچه" else "History")
                    }
                }
            ) { padding ->
                when (screen) {
                    "home" -> HomeScreen(fa, Modifier.padding(padding)) { screen = it }
                    "dictionary" -> DictionaryScreen(
                        fa = fa,
                        modifier = Modifier.padding(padding),
                        onCamera = { camera = true }
                    )
                    "favorites" -> FavoritesScreen(Modifier.padding(padding), fa)
                    else -> HistoryScreen(Modifier.padding(padding), fa)
                }
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    route: String,
    current: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    NavigationBarItem(
        selected = route == current,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) }
    )
}

@Composable
private fun HomeScreen(
    fa: Boolean,
    modifier: Modifier,
    navigate: (String) -> Unit
) {
    val context = LocalContext.current
    var ad by remember { mutableStateOf<MobileAd?>(null) }

    LaunchedEffect(Unit) {
        AdvertisementClient().load { loaded ->
            ad = loaded.firstOrNull()
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 5.dp
        ) {
            Column(
                Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    if (fa) "کلمه بعدی را کشف کن ✨" else "Discover your next word ✨",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (fa) "ترجمه، دیکشنری، تلفظ و OCR در یک تجربه سریع و آفلاین."
                    else "Translation, dictionary, pronunciation and OCR in one fast offline experience.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { navigate("dictionary") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (fa) "شروع جستجو" else "Start searching")
                }
            }
        }

        ad?.let { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        item.targetUrl?.let { url ->
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                )
                            }
                        }
                    },
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        if (fa) "پیشنهاد ویژه" else "Sponsored",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        item.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    item.description?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Text(
            if (fa) "ابزارهای قدرتمند" else "Power tools",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FeatureCard(
                if (fa) "ترجمه سریع" else "Quick translate",
                Icons.Default.Translate,
                Modifier.weight(1f)
            ) { navigate("dictionary") }

            FeatureCard(
                if (fa) "دوربین OCR" else "Camera OCR",
                Icons.Default.CameraAlt,
                Modifier.weight(1f)
            ) { navigate("dictionary") }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FeatureCard(
                if (fa) "علاقه‌مندی" else "Favorites",
                Icons.Default.Star,
                Modifier.weight(1f)
            ) { navigate("favorites") }

            FeatureCard(
                if (fa) "تاریخچه" else "History",
                Icons.Default.History,
                Modifier.weight(1f)
            ) { navigate("history") }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp))
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DictionaryScreen(
    fa: Boolean,
    modifier: Modifier,
    onCamera: () -> Unit,
    vm: DictionaryViewModel = viewModel()
) {
    val q by vm.query.collectAsState()
    val language by vm.language.collectAsState()
    val results by vm.results.collectAsState()

    val languages = listOf(
        "en" to "English",
        "fa" to "فارسی",
        "ar" to "العربية",
        "tr" to "Türkçe",
        "de" to "Deutsch",
        "fr" to "Français",
        "es" to "Español"
    )

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (fa) "دیکشنری + ترجمه" else "Dictionary + Translation",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = q,
            onValueChange = vm::setQuery,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            label = {
                Text(if (fa) "کلمه یا جمله را وارد کن" else "Enter a word or sentence")
            },
            singleLine = true
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onCamera,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.CameraAlt, null)
                Spacer(Modifier.width(6.dp))
                Text(if (fa) "دوربین" else "Camera")
            }

            OutlinedButton(
                onClick = { vm.setQuery("") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (fa) "پاک کردن" else "Clear")
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(languages) { (code, name) ->
                LanguageChip(
                    name = name,
                    selected = code == language,
                    onClick = { vm.setLanguage(code) }
                )
            }

            items(results, key = { it.id }) { item ->
                WordCard(item, vm, fa)
            }
        }
    }
}

@Composable
private fun LanguageChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(name) },
        leadingIcon = if (selected) {
            { Icon(Icons.Default.Check, contentDescription = null) }
        } else {
            null
        }
    )
}

@Composable
private fun WordCard(
    item: DictionaryEntity,
    vm: DictionaryViewModel,
    fa: Boolean
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        item.word,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    item.pronunciation?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                    item.partOfSpeech?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium)
                    }
                }
                IconButton(onClick = { vm.toggleFavorite(item) }) {
                    Icon(
                        if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null
                    )
                }
            }

            Text(item.meaning, style = MaterialTheme.typography.bodyLarge)
            item.example?.let {
                Text("“$it”", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    modifier: Modifier,
    fa: Boolean,
    vm: DictionaryViewModel = viewModel()
) {
    val items by vm.favorites.collectAsState()
    SimpleList(
        modifier = modifier,
        title = if (fa) "علاقه‌مندی‌ها" else "Favorites",
        items = items
    ) {
        Text(it.word, fontWeight = FontWeight.Bold)
        Text(it.meaning)
    }
}

@Composable
fun HistoryScreen(
    modifier: Modifier,
    fa: Boolean,
    vm: DictionaryViewModel = viewModel()
) {
    val items by vm.history.collectAsState()

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                if (fa) "تاریخچه جستجو" else "Search history",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(items) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.History, null)
                    Column {
                        Text(it.query, fontWeight = FontWeight.SemiBold)
                        Text(it.language)
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleList(
    modifier: Modifier,
    title: String,
    items: List<DictionaryEntity>,
    body: @Composable (DictionaryEntity) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        items(items) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    body(it)
                }
            }
        }
    }
}

@Composable
fun CameraScreen(
    fa: Boolean,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var allowed by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val ask = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { allowed = it }

    var scanner by remember { mutableStateOf<CameraScanner?>(null) }
    var result by remember { mutableStateOf<ScanResult?>(null) }
    val pipeline = remember { CameraOcrPipeline() }

    DisposableEffect(Unit) {
        onDispose { pipeline.close() }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text(
                if (fa) "اسکن و ترجمه" else "Scan & Translate",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (!allowed) {
            Button(onClick = { ask.launch(Manifest.permission.CAMERA) }) {
                Text(if (fa) "اجازه دسترسی به دوربین" else "Allow camera")
            }
        } else {
            AndroidView(
                factory = { c ->
                    PreviewView(c).also { preview ->
                        CameraScanner(c, owner, preview).also {
                            scanner = it
                            it.start()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
            )

            Button(
                onClick = {
                    scope.launch {
                        scanner?.take()?.let { image ->
                            result = pipeline.run(image, "fa")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.DocumentScanner, null)
                Spacer(Modifier.width(8.dp))
                Text(if (fa) "اسکن و ترجمه" else "Scan & translate")
            }

            result?.let { r ->
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(r.originalText, fontWeight = FontWeight.Bold)
                        Text(r.translatedText)
                    }
                }
            }
        }
    }
}
