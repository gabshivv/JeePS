package com.example.jeeps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeeps.ui.viewmodels.HomeViewModel
import com.example.jeeps.ui.components.*
import com.example.jeeps.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

private const val TAB_HOME     = 0
private const val TAB_EXPLORE  = 1
private const val TAB_MAP      = 2
private const val TAB_SETTINGS = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onFindRoutes      : (origin: String, destination: String) -> Unit = { _, _ -> },
    onSeeAllTerminals : () -> Unit = {},
    darkMode          : Boolean    = false,
    onDarkChange      : (Boolean) -> Unit = {},
    showSettings      : Boolean    = false,
    onShowSettings    : (Boolean) -> Unit = {},
    viewModel         : HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var lang        by remember { mutableStateOf("EN") }
    var selectedNav by remember { mutableIntStateOf(TAB_HOME) }
    var destination by remember { mutableStateOf("") }

    val scope          = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var mapFullScreen  by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(
            onBack       = { onShowSettings(false) },
            lang         = lang,
            onLangChange = { lang = it },
            darkMode     = darkMode,
            onDarkChange = onDarkChange,
        )
        return
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue    = SheetValue.Expanded,
            skipHiddenState = false,
        )
    )

    fun onNavSelected(index: Int) {
        selectedNav = index
        scope.launch {
            when (index) {
                TAB_HOME -> {
                    mapFullScreen = false
                    scaffoldState.bottomSheetState.partialExpand()
                }
                TAB_EXPLORE -> {
                    mapFullScreen = false
                    scaffoldState.bottomSheetState.expand()
                    kotlinx.coroutines.delay(120)
                    try { focusRequester.requestFocus() } catch (_: Exception) { }
                }
                TAB_MAP -> {
                    mapFullScreen = true
                    scaffoldState.bottomSheetState.hide()
                }
                TAB_SETTINGS -> {
                    onShowSettings(true)
                }
            }
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
            mapFullScreen = false
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                lang           = lang,
                selectedIndex  = selectedNav,
                onItemSelected = { onNavSelected(it) },
            )
        },
        contentWindowInsets = WindowInsets.navigationBars,
    ) { innerPadding ->

        // ── Layered layout: map fills full screen, sheet + header float on top ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // 1. Map fills the entire area behind everything
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryBlue),
            ) {
                if (!mapFullScreen) {
                    FlagStripe()
                    HeaderSection(lang = lang, onLangChange = { lang = it })
                }
                MapSection(modifier = Modifier.fillMaxSize())
            }

            // 2. Bottom sheet floats on top of the map
            if (!mapFullScreen) {
                BottomSheetScaffold(
                    scaffoldState       = scaffoldState,
                    sheetContainerColor = MaterialTheme.colorScheme.background,
                    sheetShape          = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    sheetPeekHeight     = 160.dp,
                    sheetDragHandle     = { BottomSheetDefaults.DragHandle() },
                    sheetContent        = {
                        SearchBottomSheetContent(
                            lang                = lang,
                            destination         = destination,
                            onDestinationChange = { destination = it },
                            terminals           = uiState.terminals,
                            focusRequester      = focusRequester,
                            onFindRoutes        = {
                                if (destination.isNotBlank()) {
                                    onFindRoutes(uiState.detectedOrigin, destination)
                                }
                            },
                            onSeeAllTerminals   = onSeeAllTerminals,
                        )
                    },
                ) { /* map is behind, nothing needed here */ }
            }
        }
    }
}