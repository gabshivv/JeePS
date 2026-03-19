package com.example.jeeps.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.ui.theme.*

@Composable
fun FareBanner(
    regularFare: Double,
    discountedFare: Double,
    distanceKm: Double,
    stopCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        color    = AccentYellow,
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text          = "YOUR FARE",
                    fontSize      = 9.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = FareLabel,
                    letterSpacing = 0.5.sp,
                )
                Text(
                    text       = "₱${"%.2f".format(regularFare)}",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = FareText,
                    lineHeight = 32.sp,
                )
                Text(
                    text     = "₱${"%.2f".format(discountedFare)} for PWD / Senior",
                    fontSize = 10.sp,
                    color    = FareSubText,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = "${"%.1f".format(distanceKm)} km",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = FareText,
                )
                Text(
                    text     = "$stopCount stops",
                    fontSize = 10.sp,
                    color    = FareLabel,
                )
            }
        }
    }
}