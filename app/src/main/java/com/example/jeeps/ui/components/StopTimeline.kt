package com.example.jeeps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.data.model.Landmark
import com.example.jeeps.data.model.RouteSegment
import com.example.jeeps.data.model.barangayName
import com.example.jeeps.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon

@Composable
fun StopTimeline(
    stops       : List<RouteSegment>,
    landmarks   : List<Landmark>,
    origin      : String,
    destination : String,
    modifier    : Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        stops.forEachIndexed { index, stop ->
            val isOrigin = index == 0
            val isDest   = index == stops.lastIndex

            val stopLandmarks = landmarks.filter { it.barangayId == stop.barangayId }

            StopRow(
                name      = when {
                    isOrigin -> origin
                    isDest   -> destination
                    else     -> stop.barangayName
                },
                subLabel  = when {
                    isOrigin -> "Origin · 0 km"
                    isDest   -> "${"%.1f".format(stop.distanceFromOriginKm)} km · Destination"
                    else     -> "${stop.barangayName} · ${"%.1f".format(stop.distanceFromOriginKm)} km"
                },
                isOrigin  = isOrigin,
                isDest    = isDest,
                isLast    = isDest,
                landmarks = stopLandmarks,
            )
        }
    }
}

@Composable
private fun StopRow(
    name      : String,
    subLabel  : String,
    isOrigin  : Boolean,
    isDest    : Boolean,
    isLast    : Boolean,
    landmarks : List<Landmark>,
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(top = 3.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = when {
                            isOrigin -> PrimaryBlue
                            isDest   -> PrimaryRed
                            else     -> Color.White
                        },
                        shape = CircleShape,
                    )
                    .border(
                        width = 2.dp,
                        color = when {
                            isOrigin -> PrimaryBlue
                            isDest   -> PrimaryRed
                            else     -> BorderMedium
                        },
                        shape = CircleShape,
                    )
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (landmarks.isNotEmpty()) 56.dp else 36.dp)
                        .background(BorderLight)
                )
            }
        }

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text       = name,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold,
                color      = TextDark,
            )
            Text(
                text     = subLabel,
                fontSize = 10.5.sp,
                color    = TextMuted,
            )
            if (landmarks.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    landmarks.forEach { LandmarkTag(name = it.name) }
                }
            }
        }
    }
}

@Composable
fun LandmarkTag(name: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(BlueTint, RoundedCornerShape(7.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = BlueLight,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = name,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = BlueLight,
        )
    }
}
