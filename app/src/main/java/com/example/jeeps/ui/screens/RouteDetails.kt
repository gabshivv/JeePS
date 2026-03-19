package com.example.jeeps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.jeeps.data.model.*
import com.example.jeeps.ui.components.*
import com.example.jeeps.ui.theme.*

@Composable
fun RouteDetailScreen(
    routeId: Int = 1,
    onBack: () -> Unit = {},
) {
    // TODO: replace with ViewModel / repository lookup by routeId
    val result = sampleRoutes.find { it.route.id == routeId } ?: sampleRoutes.first()
    val route  = result.route
    val fare   = result.fare

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Stops", "Landmarks", "Info")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgApp),
    ) {
        FlagStripe()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 28.dp),
            ) {
                // Back button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier
                        .clickable { onBack() }
                        .padding(bottom = 8.dp),
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Results",
                        tint               = Color.White.copy(alpha = 0.65f),
                        modifier           = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = "Results",
                        fontSize = 11.sp,
                        color    = Color.White.copy(alpha = 0.65f),
                    )
                }

                Text(
                    text       = route.routeCode,
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                    lineHeight = 28.sp,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text     = route.displayName,
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.6f),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(BgApp),
            )
        }

        // Signboard
        Box(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-6).dp)) {
            SignboardCard(
                routeCode   = route.routeCode,
                origin      = route.origin,
                destination = route.destination,
                viaText     = route.stops
                    .drop(1)
                    .dropLast(1)
                    .joinToString(" · ") { it.barangayName },
                routeType   = route.routeType,
                size        = SignboardSize.FULL,
            )
        }

        // Fare Banner
        Box(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 10.dp)) {
            FareBanner(
                regularFare    = fare.regularFare,
                discountedFare = fare.discountedFare,
                distanceKm     = fare.distanceKm,
                stopCount      = fare.stopCount,
            )
        }

        // Bayan Route Alert
        if (route.routeType == RouteType.BAYAN) {
            BayanAlert(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgApp)
                .border(width = 2.dp, color = BorderLight, shape = RoundedCornerShape(0.dp))
                .padding(horizontal = 20.dp),
        ) {
            tabs.forEachIndexed { index, label ->
                val isActive = selectedTab == index
                Box(
                    modifier = Modifier
                        .clickable { selectedTab = index }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .then(
                            if (isActive) Modifier.border(
                                width = 2.dp,
                                color = PrimaryBlue,
                                shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
                            ) else Modifier
                        ),
                ) {
                    Text(
                        text       = label,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = if (isActive) PrimaryBlue else TextMuted,
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> StopsTabContent(route = route)
            1 -> LandmarksTabContent(route = route)
            2 -> InfoTabContent(route = route, fare = fare)
        }
    }
}

// Alert for Bayan
@Composable
private fun BayanAlert(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp))
            .background(YellowTint)
            .border(
                width = 3.dp,
                color = AccentYellow,
                shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp),
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(text = "⚠", fontSize = 11.sp, color = BayanBadgeText)
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

// Stops
@Composable
private fun StopsTabContent(route: Route) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        // Reuse StopTimeline component — already handles all dot states + landmark tags
        StopTimeline(
            stops       = route.stops,
            landmarks   = route.landmarks,
            origin      = route.origin,
            destination = route.destination,
        )
    }
}

// Landmarks
@Composable
private fun LandmarksTabContent(route: Route) {
    if (route.landmarks.isEmpty()) {
        Box(
            modifier         = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text     = "No landmarks recorded for this route yet.",
                fontSize = 13.sp,
                color    = TextMuted,
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        route.landmarks.forEach { landmark ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgCard)
                    .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier         = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueTint),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "📍", fontSize = 18.sp)
                }
                Column {
                    Text(
                        text       = landmark.name,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextDark,
                    )
                    Text(
                        text     = landmark.barangayName,
                        fontSize = 11.sp,
                        color    = TextMuted,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

// Information
@Composable
private fun InfoTabContent(route: Route, fare: FareResult) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InfoCard(title = "Fare") {
            InfoRow("Regular",      "₱${"%.2f".format(fare.regularFare)}")
            InfoRow("PWD / Senior", "₱${"%.2f".format(fare.discountedFare)}")
            InfoRow("Base fare",    "₱13.00 (first 4 km)")
            InfoRow("Per km after", "₱1.80")
        }
        InfoCard(title = "Route") {
            InfoRow("Type",      if (route.routeType == RouteType.BAYAN) "Bayan (Barangay roads)" else "National Highway")
            InfoRow("Distance",  "${"%.1f".format(fare.distanceKm)} km")
            InfoRow("Stops",     "${fare.stopCount}")
            InfoRow("Direction", "${route.origin} → ${route.destination}")
            InfoRow("Status",    route.status.name.lowercase().replaceFirstChar { it.uppercase() })
        }
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.5.dp, BorderLight, RoundedCornerShape(12.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgApp)
                .padding(horizontal = 14.dp, vertical = 9.dp),
        ) {
            Text(
                text          = title.uppercase(),
                fontSize      = 9.5.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextMuted,
                letterSpacing = 0.6.sp,
            )
        }
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun RouteDetailPreview() {
    RouteDetailScreen(routeId = 1)
}