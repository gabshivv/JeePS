package com.example.jeeps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.jeeps.data.model.*
import com.example.jeeps.ui.components.FlagStripe
import com.example.jeeps.ui.components.RouteResultCard
import com.example.jeeps.ui.theme.*

@Composable
fun RouteResultsScreen(
    origin: String = "Crossing",
    destination: String = "Cabuyao Bayan",
    routes: List<RouteSearchResult> = sampleRoutes,
    onBack: () -> Unit = {},
    onRouteSelected: (routeId: Int) -> Unit = {},
) {
    var activeFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Bayan", "Nat'l Hwy", "Lowest Fare")

    val displayedRoutes = when (activeFilter) {
        "Bayan"       -> routes.filter { it.route.routeType == RouteType.BAYAN }
        "Nat'l Hwy"   -> routes.filter { it.route.routeType == RouteType.NATIONAL_HIGHWAY }
        "Lowest Fare" -> routes.sortedBy { it.fare.regularFare }
        else          -> routes
    }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier
                        .clickable { onBack() }
                        .padding(bottom = 10.dp),
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = Color.White.copy(alpha = 0.65f),
                        modifier           = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = "Back",
                        fontSize = 11.sp,
                        color    = Color.White.copy(alpha = 0.65f),
                    )
                }

                // From - To
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = origin,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint               = Color.White.copy(alpha = 0.5f),
                        modifier           = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = destination,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = AccentYellow,
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text     = "${displayedRoutes.size} route${if (displayedRoutes.size != 1) "s" else ""} found · Laguna",
                    fontSize = 11.sp,
                    color    = Color.White.copy(alpha = 0.55f),
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

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filters.forEach { label ->
                val isActive = activeFilter == label
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isActive) PrimaryBlue else BgCard)
                        .border(
                            width = 1.5.dp,
                            color = if (isActive) PrimaryBlue else BorderLight,
                            shape = RoundedCornerShape(20.dp),
                        )
                        .clickable { activeFilter = label }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        text       = label,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color      = if (isActive) Color.White else TextSubtle,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (displayedRoutes.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text     = "No routes match this filter.",
                        fontSize = 13.sp,
                        color    = TextMuted,
                    )
                }
            } else {
                displayedRoutes.forEach { result ->
                    RouteResultCard(
                        result        = result,
                        onViewDetails = { onRouteSelected(result.route.id) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun RouteResultsPreview() {
    RouteResultsScreen()
}