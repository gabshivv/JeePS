package com.example.jeeps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.data.model.*
import com.example.jeeps.ui.theme.*

@Composable
fun RouteResultCard(
    result: RouteSearchResult,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val route = result.route
    val fare  = result.fare

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (result.isBestMatch) 2.dp else 1.5.dp,
                color = if (result.isBestMatch) PrimaryBlue else BorderLight,
                shape = RoundedCornerShape(16.dp),
            )
            .background(BgCard),
    ) {
        if (result.isBestMatch) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text(
                    text          = "★  BEST MATCH",
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = AccentYellow,
                    letterSpacing = 0.6.sp,
                )
            }
        }

        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            SignboardCard(
                routeCode   = route.routeCode,
                origin      = route.originName, // Updated
                destination = route.destinationName, // Updated
                viaText     = route.stops
                    .drop(1)
                    .dropLast(1)
                    .take(3)
                    .joinToString(" · ") { it.barangayName },
                routeType   = route.routeType,
                size        = SignboardSize.MINI,
            )
        }

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text     = route.displayName,
                fontSize = 10.5.sp,
                color    = TextMuted,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Spacer(Modifier.width(8.dp))
            RouteTypeBadge(routeType = route.routeType)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 4.dp),
        ) {
            StatCell(
                value      = "₱${fare.regularFare.toInt()}",
                label      = "Fare",
                valueColor = PrimaryRed,
                modifier   = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(BorderLight)
                    .align(Alignment.CenterVertically),
            )
            StatCell(
                value    = "${"%.1f".format(fare.distanceKm)} km",
                label    = "Distance",
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(BorderLight)
                    .align(Alignment.CenterVertically),
            )
            StatCell(
                value    = "${fare.stopCount}",
                label    = "Stops",
                modifier = Modifier.weight(1f),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgApp)
                .border(
                    width = 1.dp,
                    color = BorderLight,
                    shape = RoundedCornerShape(0.dp),
                )
                .clickable { onViewDetails() }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = "View Details →",
                fontSize   = 11.sp,
                fontWeight = FontWeight.Bold,
                color      = PrimaryBlue,
            )
        }
    }
}

@Composable
private fun RouteTypeBadge(routeType: RouteType) {
    val (label, bg, fg) = when (routeType) {
        RouteType.BAYAN            -> Triple("BAYAN",     BayanBadgeBg,  BayanBadgeText)
        RouteType.NATIONAL_HIGHWAY -> Triple("NAT'L HWY", NhwyBadgeBg,   NhwyBadgeText)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text       = label,
            fontSize   = 9.sp,
            fontWeight = FontWeight.Bold,
            color      = fg,
        )
    }
}

// Stat Cell
@Composable
private fun StatCell(
    value: String,
    label: String,
    valueColor: Color = TextDark,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Text(
            text       = value,
            fontSize   = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = valueColor,
        )
        Text(
            text          = label.uppercase(),
            fontSize      = 9.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = TextMuted,
            letterSpacing = 0.4.sp,
        )
    }
}
