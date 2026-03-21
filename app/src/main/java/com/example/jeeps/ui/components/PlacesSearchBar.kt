package com.example.jeeps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.data.repository.DestinationResult
import com.example.jeeps.ui.theme.*

@Composable
fun PlacesSearchBar(
    value           : String,
    onValueChange   : (String) -> Unit,
    onResultSelected: (DestinationResult) -> Unit,
    localResults    : List<DestinationResult> = emptyList(),
    placeholder     : String? = null,
    focusRequester  : FocusRequester = remember { FocusRequester() },
    lang            : String = "EN",
    modifier        : Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    val showDropdown = isFocused && localResults.isNotEmpty()

    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline

    Column(modifier = modifier) {
        BasicTextField(
            value         = value,
            onValueChange = onValueChange,
            singleLine  = true,
            cursorBrush = SolidColor(PrimaryBlue),
            textStyle   = TextStyle(
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = onSurface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused },
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text     = placeholder ?: (if (lang == "EN") "Where are you going?" else "Saan ka pupunta?"),
                        fontSize = 14.sp,
                        color    = onSurface.copy(alpha = 0.35f),
                    )
                }
                innerTextField()
            },
        )

        if (showDropdown) {
            Spacer(Modifier.height(8.dp))
            Surface(
                shape          = RoundedCornerShape(12.dp),
                color          = surface,
                tonalElevation = 4.dp,
                modifier       = Modifier.fillMaxWidth(),
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(localResults) { result ->
                        PredictionRow(
                            primary   = result.displayName,
                            secondary = if (result.type.name == "BARANGAY") "Barangay" else "Landmark",
                            onSurface = onSurface,
                            onClick   = {
                                onResultSelected(result)
                            },
                        )
                        HorizontalDivider(color = outline, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PredictionRow(
    primary   : String,
    secondary : String,
    onSurface : androidx.compose.ui.graphics.Color,
    onClick   : () -> Unit,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier         = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BlueTint),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.LocationOn,
                contentDescription = null,
                tint               = BlueLight,
                modifier           = Modifier.size(14.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = primary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = onSurface,
            )
            if (secondary.isNotBlank()) {
                Text(
                    text     = secondary,
                    fontSize = 11.sp,
                    color    = onSurface.copy(alpha = 0.5f),
                )
            }
        }
    }
}
