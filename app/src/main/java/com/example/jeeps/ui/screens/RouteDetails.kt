package com.example.jeeps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jeeps.data.model.*
import com.example.jeeps.ui.components.*
import com.example.jeeps.ui.theme.*
import com.example.jeeps.ui.viewmodels.RouteDetailViewModel

@Composable
fun RouteDetailScreen(
    routeId   : Int = 1,
    onBack    : () -> Unit = {},
    viewModel : RouteDetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(routeId) { viewModel.load(routeId) }

    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val onSurf  = MaterialTheme.colorScheme.onSurface

    if (uiState.isLoading) {
        Box(
            modifier         = Modifier.fillMaxSize().background(bg),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    val route = uiState.route
    val fare  = uiState.fare
    if (route == null || fare == null) {
        Box(
            modifier         = Modifier.fillMaxSize().background(bg).padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(uiState.error ?: "Route not found", fontSize = 13.sp, color = onSurf.copy(alpha = 0.5f))
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Map", "Stops", "Landmarks", "Info")

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
                    .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 28.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.clickable { onBack() }.padding(bottom = 8.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Results",
                        tint = Color.White.copy(alpha = 0.65f), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Results", fontSize = 11.sp, color = Color.White.copy(alpha = 0.65f))
                }
                Text(route.routeCode, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                    color = Color.White, lineHeight = 28.sp)
                Spacer(Modifier.height(3.dp))
                Text(route.displayName, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(20.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(bg),
            )
        }

        Box(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-6).dp)) {
            SignboardCard(
                routeCode   = route.routeCode,
                origin      = route.originName,
                destination = route.destinationName,
                viaText     = route.stops.drop(1).dropLast(1).joinToString(" · ") { it.barangayName },
                routeType   = route.routeType,
                size        = SignboardSize.FULL,
            )
        }

        Box(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 10.dp)) {
            FareBanner(
                regularFare    = fare.regularFare,
                discountedFare = fare.discountedFare,
                distanceKm     = fare.distanceKm,
                stopCount      = fare.stopCount,
            )
        }

        if (route.routeType == "bayan") {
            BayanAlert(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp))
        }

        // Tab row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg)
                .border(width = 2.dp, color = outline, shape = RoundedCornerShape(0.dp))
                .padding(horizontal = 20.dp),
        ) {
            tabs.forEachIndexed { i, label ->
                val isActive = selectedTab == i
                Box(
                    modifier = Modifier
                        .clickable { selectedTab = i }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        label,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = if (isActive) PrimaryBlue else onSurf.copy(alpha = 0.5f),
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> MapTabContent(route = route)
            1 -> StopsTabContent(route = route, bg = bg)
            2 -> LandmarksTabContent(route = route, bg = bg, surface = surface, outline = outline, onSurf = onSurf)
            3 -> InfoTabContent(route = route, fare = fare, bg = bg, surface = surface, outline = outline, onSurf = onSurf)
        }
    }
}

@Composable
private fun MapTabContent(route: Route) {
    Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        MapSection(
            activeRoute = route,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
        )
    }
}

@Composable
private fun BayanAlert(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp))
            .background(YellowTint)
            .border(3.dp, AccentYellow,
                RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text("⚠", fontSize = 11.sp, color = BayanBadgeText)
        Spacer(Modifier.width(6.dp))
        Text(
            text       = "Bayan route — passes through barangay roads, not the national highway.",
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = BayanBadgeText,
            lineHeight = 16.sp,
        )
    }
}

@Composable
private fun StopsTabContent(route: Route, bg: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(bg)
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        StopTimeline(
            stops       = route.stops,
            landmarks   = route.landmarks,
            origin      = route.originName,
            destination = route.destinationName,
        )
    }
}

@Composable
private fun LandmarksTabContent(
    route   : Route,
    bg      : Color,
    surface : Color,
    outline : Color,
    onSurf  : Color,
) {
    if (route.landmarks.isEmpty()) {
        Box(
            Modifier.fillMaxSize().background(bg).padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("No landmarks recorded for this route yet.", fontSize = 13.sp, color = onSurf.copy(alpha = 0.5f))
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(bg)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        route.landmarks.forEach { landmark ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(surface)
                    .border(1.5.dp, outline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier         = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(BlueTint),
                    contentAlignment = Alignment.Center,
                ) { Text("📍", fontSize = 18.sp) }
                Column {
                    Text(landmark.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = onSurf)
                    Text(landmark.barangayName, fontSize = 11.sp, color = onSurf.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoTabContent(
    route   : Route,
    fare    : FareResult,
    bg      : Color,
    surface : Color,
    outline : Color,
    onSurf  : Color,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(bg)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InfoCard(title = "Fare", bg = bg, surface = surface, outline = outline, onSurf = onSurf) {
            InfoRow("Regular",      "₱${"%.2f".format(fare.regularFare)}",      onSurf = onSurf)
            InfoRow("PWD / Senior", "₱${"%.2f".format(fare.discountedFare)}",   onSurf = onSurf)
            InfoRow("Base fare",    "₱13.00 (first 4 km)",                      onSurf = onSurf)
            InfoRow("Per km after", "₱1.80",                                    onSurf = onSurf)
        }
        InfoCard(title = "Route", bg = bg, surface = surface, outline = outline, onSurf = onSurf) {
            InfoRow("Type",      if (route.routeType == "bayan") "Bayan (Barangay roads)" else "National Highway", onSurf = onSurf)
            InfoRow("Distance",  "${"%.1f".format(fare.distanceKm)} km",         onSurf = onSurf)
            InfoRow("Stops",     "${fare.stopCount}",                            onSurf = onSurf)
            InfoRow("Direction", "${route.originName} → ${route.destinationName}",       onSurf = onSurf)
            InfoRow("Status",    route.status.name.lowercase().replaceFirstChar { it.uppercase() }, onSurf = onSurf)
        }
    }
}

@Composable
private fun InfoCard(
    title   : String,
    bg      : Color,
    surface : Color,
    outline : Color,
    onSurf  : Color,
    content : @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(surface)
            .border(1.5.dp, outline, RoundedCornerShape(12.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 9.dp),
        ) {
            Text(
                title.uppercase(),
                fontSize      = 9.5.sp,
                fontWeight    = FontWeight.Bold,
                color         = onSurf.copy(alpha = 0.5f),
                letterSpacing = 0.6.sp,
            )
        }
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) { content() }
    }
}

@Composable
private fun InfoRow(label: String, value: String, onSurf: Color) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment     = Alignment.Top,
    ) {
        Text(
            text     = label,
            fontSize = 12.sp,
            color    = onSurf.copy(alpha = 0.5f),
            modifier = Modifier.width(80.dp) 
        )
        Text(
            text       = value,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = onSurf,
            modifier   = Modifier.weight(1f),
            textAlign  = TextAlign.End,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun RouteDetailPreview() {
    RouteDetailScreen(routeId = 1)
}
