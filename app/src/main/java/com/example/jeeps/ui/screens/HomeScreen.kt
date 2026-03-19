package com.example.jeeps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.jeeps.data.model.sampleTerminals
import com.example.jeeps.ui.components.*
import com.example.jeeps.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onFindRoutes: (origin: String, destination: String) -> Unit = { _, _ -> },
) {
    var lang        by remember { mutableStateOf("EN") }
    var selectedNav by remember { mutableIntStateOf(0) }
    var destination by remember { mutableStateOf("") }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded
        )
    )

    Scaffold(
        bottomBar = {
            BottomNavBar(
                lang           = lang,
                selectedIndex  = selectedNav,
                onItemSelected = { selectedNav = it },
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
            sheetContent = {
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    SearchBottomSheetContent(
                        lang                = lang,
                        destination         = destination,
                        onDestinationChange = { destination = it },
                        terminals           = sampleTerminals,
                        onFindRoutes        = {
                            if (destination.isNotBlank()) {
                                onFindRoutes("Crossing, Calamba", destination)
                            }
                        },
                        onSeeAllTerminals   = { /* TODO */ },
                    )
                }
            },
            modifier = Modifier.padding(innerPadding),
        ) { bottomSheetPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryBlue)
                    .padding(bottomSheetPadding),
            ) {
                FlagStripe()
                HeaderSection(lang = lang, onLangChange = { lang = it })
                MapSection()
            }
        }
    }
}