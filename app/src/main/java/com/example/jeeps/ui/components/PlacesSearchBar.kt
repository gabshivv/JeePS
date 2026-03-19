package com.example.jeeps.ui.components

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.example.jeeps.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PlacesSearchBar(
    value           : String,
    onValueChange   : (String) -> Unit,
    onPlaceSelected : (placeId: String, displayName: String) -> Unit,
    focusRequester  : FocusRequester = remember { FocusRequester() },
    lang            : String = "EN",
    modifier        : Modifier = Modifier,
) {
    val context  = LocalContext.current
    val scope    = rememberCoroutineScope()

    var predictions  by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var showDropdown by remember { mutableStateOf(false) }
    var isFocused    by remember { mutableStateOf(false) }

    // Lazily initialize PlacesClient — safe to call multiple times
    val placesClient: PlacesClient = remember(context) {
        if (!Places.isInitialized()) {
            // API key is read from the app's manifest meta-data
            // (already wired via MAPS_API_KEY in local.properties + build.gradle)
            Places.initializeWithNewPlacesApiEnabled(context, getApiKey(context))
        }
        Places.createClient(context)
    }

    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    // Fetch predictions whenever the query text changes
    LaunchedEffect(value) {
        if (value.length < 2) {
            predictions  = emptyList()
            showDropdown = false
            return@LaunchedEffect
        }
        scope.launch {
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                // Restrict to Philippines for relevance
                .setCountries("PH")
                // Bias results toward Laguna (Calamba area)
                .setQuery(value)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    predictions  = response.autocompletePredictions
                    showDropdown = predictions.isNotEmpty() && isFocused
                }
                .addOnFailureListener {
                    predictions  = emptyList()
                    showDropdown = false
                }
        }
    }

    Column(modifier = modifier) {
        BasicTextField(
            value         = value,
            onValueChange = { text ->
                onValueChange(text)
                if (text.isBlank()) {
                    predictions  = emptyList()
                    showDropdown = false
                }
            },
            singleLine    = true,
            cursorBrush   = SolidColor(PrimaryBlue),
            textStyle     = TextStyle(
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextDark,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { state ->
                    isFocused    = state.isFocused
                    showDropdown = state.isFocused && predictions.isNotEmpty()
                },
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text     = if (lang == "EN") "Where are you going?" else "Saan ka pupunta?",
                        fontSize = 14.sp,
                        color    = TextHint,
                    )
                }
                innerTextField()
            },
        )

        // Autocomplete dropdown
        if (showDropdown && predictions.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Surface(
                shape          = RoundedCornerShape(12.dp),
                color          = BgCard,
                tonalElevation = 2.dp,
                modifier       = Modifier.fillMaxWidth(),
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                    items(
                        items = predictions,
                        key   = { it.placeId },
                    ) { prediction ->
                        PredictionRow(
                            primary   = prediction.getPrimaryText(null).toString(),
                            secondary = prediction.getSecondaryText(null).toString(),
                            onClick   = {
                                val displayName = prediction.getPrimaryText(null).toString()
                                onValueChange(displayName)
                                onPlaceSelected(prediction.placeId, displayName)
                                predictions  = emptyList()
                                showDropdown = false
                            },
                        )
                        HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

// ─── Single prediction row ────────────────────────────────────────────────────

@Composable
private fun PredictionRow(
    primary   : String,
    secondary : String,
    onClick   : () -> Unit,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
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
                color      = TextDark,
            )
            if (secondary.isNotBlank()) {
                Text(
                    text     = secondary,
                    fontSize = 11.sp,
                    color    = TextMuted,
                )
            }
        }
    }
}

// Helper: read API key from manifest meta-data

private fun getApiKey(context: Context): String {
    val appInfo = context.packageManager
        .getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
    return appInfo.metaData?.getString("com.google.android.geo.API_KEY") ?: ""
}