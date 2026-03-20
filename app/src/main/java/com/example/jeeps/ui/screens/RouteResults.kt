package com.example.jeeps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeeps.data.model.*
import com.example.jeeps.ui.components.FlagStripe
import com.example.jeeps.ui.components.RouteResultCard
import com.example.jeeps.ui.theme.*
import com.example.jeeps.ui.viewmodels.RouteResultsViewModel

@Composable
fun RouteResultsScreen(
    origin          : String = "Crossing",
    destination     : String = "Cabuyao Bayan",
    onBack          : () -> Unit = {},
    onRouteSelected : (routeId: Int) -> Unit = {},
    viewModel       : RouteResultsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(origin, destination) {
        viewModel.search(origin, destination)
    }

    var activeFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Bayan", "Nat'l Hwy", "Lowest Fare")

    val displayedRoutes = when (activeFilter) {
        "Bayan"       -> uiState.routes.filter { it.route.routeType == RouteType.BAYAN }
        "Nat'l Hwy"   -> uiState.routes.filter { it.route.routeType == RouteType.NATIONAL_HIGHWAY }
        "Lowest Fare" -> uiState.routes.sortedBy { it.fare.regularFare }
        else          -> uiState.routes
    }

    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val onSurf  = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
    ) {
        FlagStripe()

        Box(modifier = Modifier.fillMaxWidth().background(PrimaryBlue)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 24.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier
                        .clickable { onBack() }
                        .padding(bottom = 8.dp),
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = Color.White.copy(alpha = 0.65f),
                        modifier           = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Back", fontSize = 11.sp, color = Color.White.copy(alpha = 0.65f))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(origin, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                        tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(destination, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentYellow)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = if (uiState.isLoading) "Searching…"
                    else "${displayedRoutes.size} route${if (displayedRoutes.size != 1) "s" else ""} found",
                    fontSize = 10.sp,
                    color    = Color.White.copy(alpha = 0.55f),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(16.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(bg),
            )
        }

        // Filter chips
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filters.forEach { label ->
                val isActive = activeFilter == label
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isActive) PrimaryBlue else surface)
                        .border(1.dp, if (isActive) PrimaryBlue else outline, RoundedCornerShape(20.dp))
                        .clickable { activeFilter = label }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text       = label,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color      = if (isActive) Color.White else onSurf.copy(alpha = 0.6f),
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.error != null) {
            Box(Modifier.weight(1f).fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(uiState.error!!, fontSize = 12.sp, color = onSurf.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(displayedRoutes) { result ->
                    RouteResultCard(
                        result        = result,
                        onViewDetails = { onRouteSelected(result.route.id) },
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
