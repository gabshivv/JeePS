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
    viewModel         : HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var lang        by remember { mutableStateOf("EN") }
    var selectedNav by remember { mutableIntStateOf(TAB_HOME) }
    var destination by remember { mutableStateOf("") }

    val scope          = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var mapFullScreen by remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue        = SheetValue.Expanded,
            skipHiddenState     = false,
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
                    // TODO (P4): navigate to SettingsScreen
                }
            }
        }
    }

    // If user drags the sheet back up manually while on Map tab,
    // reset the full-screen flag so the nav state stays in sync.
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
        BottomSheetScaffold(
            scaffoldState       = scaffoldState,
            sheetContainerColor = Color(0xFFF4F6FA),
            sheetShape          = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            sheetPeekHeight     = 200.dp,
            sheetDragHandle     = { BottomSheetDefaults.DragHandle() },
            sheetContent        = {
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
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
                }
            },
            modifier = Modifier.padding(innerPadding),
        ) { bottomSheetPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryBlue)
                    .padding(if (mapFullScreen) PaddingValues(0.dp) else bottomSheetPadding),
            ) {
                FlagStripe()
                if (!mapFullScreen) {
                    HeaderSection(lang = lang, onLangChange = { lang = it })
                }
                MapSection(modifier = Modifier.weight(1f))
            }
        }
    }
}