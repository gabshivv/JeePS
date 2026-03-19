package com.example.jeeps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.data.model.RouteType
import com.example.jeeps.ui.theme.*

enum class SignboardSize { MINI, FULL }

@Composable
fun SignboardCard(
    routeCode: String,
    origin: String,
    destination: String,
    viaText: String,
    routeType: RouteType,
    size: SignboardSize = SignboardSize.FULL,
    modifier: Modifier = Modifier,
) {
    val isMini       = size == SignboardSize.MINI
    val cornerRadius = if (isMini) 8.dp  else 10.dp
    val topStripeH   = if (isMini) 5.dp  else 7.dp
    val botStripeH   = if (isMini) 4.dp  else 5.dp
    val bodyPadH     = if (isMini) 8.dp  else 10.dp
    val bodyPadV     = if (isMini) 6.dp  else 9.dp
    val codeSize     = if (isMini) 9.sp  else 10.sp
    val mainSize     = if (isMini) 14.sp else 17.sp
    val viaSize      = if (isMini) 8.5.sp else 9.sp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = if (isMini) 2.dp else 2.5.dp,
                color = Color(0xFF333333),
                shape = RoundedCornerShape(cornerRadius),
            )
            .background(SignboardBg),
    ) {
        // Top stripe — red / yellow alternating
        SignboardStripe(height = topStripeH, startsRed = true)

        Column(
            modifier = Modifier.padding(horizontal = bodyPadH, vertical = bodyPadV)
        ) {
            // Route code line
            Text(
                text          = "$routeCode · Laguna",
                fontSize      = codeSize,
                fontWeight    = FontWeight.Bold,
                color         = AccentYellow.copy(alpha = 0.65f),
                letterSpacing = 1.2.sp,
            )
            Spacer(Modifier.height(3.dp))

            // Origin → Destination
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text       = origin.uppercase(),
                    fontSize   = mainSize,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                )
                SignboardArrow()
                Text(
                    text       = destination.uppercase(),
                    fontSize   = mainSize,
                    fontWeight = FontWeight.Bold,
                    color      = AccentYellow,
                )
            }

            // Via text
            Text(
                text      = "via $viaText",
                fontSize  = viaSize,
                color     = Color.White.copy(alpha = 0.4f),
                modifier  = Modifier.padding(top = 3.dp),
            )

            // Route type badge — only on FULL size
            if (!isMini) {
                Spacer(Modifier.height(7.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    SignboardTypeBadge(routeType)
                    Text(
                        text     = "Look for this sign on the jeepney",
                        fontSize = 9.sp,
                        color    = Color.White.copy(alpha = 0.35f),
                    )
                }
            }
        }

        // Bottom stripe — yellow / red alternating
        SignboardStripe(height = botStripeH, startsRed = false)
    }
}

@Composable
private fun SignboardArrow() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(20.dp).height(2.dp).background(AccentYellow))
        Text("▶", fontSize = 7.sp, color = AccentYellow)
    }
}

@Composable
private fun SignboardStripe(height: Dp, startsRed: Boolean) {
    val colors = if (startsRed)
        listOf(PrimaryRed, AccentYellow, PrimaryRed, AccentYellow,
            PrimaryRed, AccentYellow, PrimaryRed, AccentYellow,
            PrimaryRed, AccentYellow, PrimaryRed, AccentYellow)
    else
        listOf(AccentYellow, PrimaryRed, AccentYellow, PrimaryRed,
            AccentYellow, PrimaryRed, AccentYellow, PrimaryRed,
            AccentYellow, PrimaryRed, AccentYellow, PrimaryRed)
    Row(modifier = Modifier.fillMaxWidth().height(height)) {
        colors.forEach { color ->
            Box(Modifier.weight(1f).fillMaxHeight().background(color))
        }
    }
}

@Composable
private fun SignboardTypeBadge(routeType: RouteType) {
    val (label, bg) = when (routeType) {
        RouteType.BAYAN            -> "BAYAN ROUTE" to PrimaryRed
        RouteType.NATIONAL_HIGHWAY -> "NAT'L HWY"   to Color(0xFF22C55E)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text       = label,
            fontSize   = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color      = Color.White,
            letterSpacing = 0.8.sp,
        )
    }
}