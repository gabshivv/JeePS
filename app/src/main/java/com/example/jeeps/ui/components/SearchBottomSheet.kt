package com.example.jeeps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import com.example.jeeps.data.model.Terminal
import com.example.jeeps.ui.theme.*

@Composable
fun SearchBottomSheetContent(
    lang                : String,
    destination         : String,
    onDestinationChange : (String) -> Unit,
    terminals           : List<Terminal>,
    onFindRoutes        : () -> Unit = {},
    onSeeAllTerminals   : () -> Unit = {},
    focusRequester      : FocusRequester = FocusRequester(),
    modifier            : Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 24.dp),
    ) {
        SearchInputCard(
            lang                = lang,
            destination         = destination,
            onDestinationChange = onDestinationChange,
            focusRequester      = focusRequester,
        )
        Spacer(Modifier.height(16.dp))
        FindRoutesButton(
            lang    = lang,
            enabled = destination.isNotBlank(),
            onClick = onFindRoutes,
        )
        Spacer(Modifier.height(20.dp))
        TerminalsSectionHeader(lang = lang, onSeeAll = onSeeAllTerminals)
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding        = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        ) {
            items(terminals) { terminal ->
                TerminalCard(terminal = terminal)
            }
        }
    }
}

@Composable
private fun SearchInputCard(
    lang                : String,
    destination         : String,
    onDestinationChange : (String) -> Unit,
    focusRequester      : FocusRequester,
) {
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline
    val onSurf  = MaterialTheme.colorScheme.onSurface

    Surface(
        modifier       = Modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(16.dp),
        color          = surface,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // FROM
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            ) {
                Box(
                    modifier         = Modifier.size(26.dp).clip(CircleShape).background(BlueTint),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = BlueLight, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text          = if (lang == "EN") "FROM" else "MULA",
                        fontSize      = 9.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = onSurf.copy(alpha = 0.5f),
                        letterSpacing = 0.6.sp,
                    )
                    Text(
                        text       = "Crossing, Calamba",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = onSurf,
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = outline)

            // TO
            Row(
                verticalAlignment = Alignment.Top,
                modifier          = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            ) {
                Box(
                    modifier         = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF0F0)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = PrimaryRed, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text          = if (lang == "EN") "TO" else "PATUNGO",
                        fontSize      = 9.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = onSurf.copy(alpha = 0.5f),
                        letterSpacing = 0.6.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    PlacesSearchBar(
                        value           = destination,
                        onValueChange   = onDestinationChange,
                        onPlaceSelected = { _, displayName ->
                            onDestinationChange(displayName)
                        },
                        focusRequester  = focusRequester,
                        lang            = lang,
                        modifier        = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun FindRoutesButton(lang: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = PrimaryRed,
            contentColor           = Color.White,
            disabledContainerColor = PrimaryRed.copy(alpha = 0.38f),
            disabledContentColor   = Color.White.copy(alpha = 0.6f),
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Icon(Icons.Default.Search, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(
            text       = if (lang == "EN") "Find Routes" else "Hanapin ang Ruta",
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TerminalsSectionHeader(lang: String, onSeeAll: () -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text          = if (lang == "EN") "NEARBY TERMINALS" else "MGA TERMINAL SA MALAPIT",
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            letterSpacing = 0.6.sp,
        )
        TextButton(onClick = onSeeAll, contentPadding = PaddingValues(0.dp)) {
            Text(if (lang == "EN") "See All" else "Tingnan Lahat", fontSize = 11.sp, color = BlueLight)
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint               = BlueLight,
                modifier           = Modifier.size(16.dp),
            )
        }
    }
}