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
import androidx.compose.ui.text.style.TextOverflow
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
    val topStripeH   = if (isMini) 4.dp  else 7.dp
    val botStripeH   = if (isMini) 3.dp  else 5.dp
    val bodyPadH     = if (isMini) 10.dp else 12.dp
    val bodyPadV     = if (isMini) 6.dp  else 10.dp
    val codeSize     = if (isMini) 8.sp  else 10.sp
    val mainSize     = if (isMini) 13.sp else 16.sp
    val viaSize      = if (isMini) 8.sp  else 9.sp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = if (isMini) 1.5.dp else 2.dp,
                color = Color(0xFF333333),
                shape = RoundedCornerShape(cornerRadius),
            )
            .background(SignboardBg),
    ) {
        SignboardStripe(height = topStripeH, startsRed = true)

        Column(
            modifier = Modifier.padding(horizontal = bodyPadH, vertical = bodyPadV)
        ) {
            Text(
                text          = "$routeCode · Laguna".uppercase(),
                fontSize      = codeSize,
                fontWeight    = FontWeight.Bold,
                color         = AccentYellow.copy(alpha = 0.7f),
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(2.dp))

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text     = origin.uppercase(),
                    fontSize = mainSize,
                    fontWeight = FontWeight.ExtraBold,
                    color    = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                SignboardArrow()
                Text(
                    text     = destination.uppercase(),
                    fontSize = mainSize,
                    fontWeight = FontWeight.ExtraBold,
                    color    = AccentYellow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            if (viaText.isNotBlank()) {
                Text(
                    text     = "via $viaText",
                    fontSize = viaSize,
                    color    = Color.White.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        SignboardStripe(height = botStripeH, startsRed = false)
    }
}

@Composable
private fun SignboardArrow() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(12.dp).height(1.5.dp).background(AccentYellow))
        Text("▶", fontSize = 6.sp, color = AccentYellow)
    }
}

@Composable
private fun SignboardStripe(height: Dp, startsRed: Boolean) {
    val colors = if (startsRed)
        listOf(PrimaryRed, AccentYellow, PrimaryRed, AccentYellow, PrimaryRed, AccentYellow)
    else
        listOf(AccentYellow, PrimaryRed, AccentYellow, PrimaryRed, AccentYellow, PrimaryRed)
    Row(modifier = Modifier.fillMaxWidth().height(height)) {
        colors.forEach { color ->
            Box(Modifier.weight(1f).fillMaxHeight().background(color))
        }
    }
}
