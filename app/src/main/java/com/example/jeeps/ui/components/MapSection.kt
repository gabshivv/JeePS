package com.example.jeeps.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapSection(modifier: Modifier = Modifier) {
    val calambaLocation = LatLng(14.2046, 121.1560)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(calambaLocation, 14f)
    }

    var locationPermissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                ?: permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                        ?: false
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
    ) {
        GoogleMap(
            modifier            = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties          = MapProperties(
                isMyLocationEnabled = locationPermissionGranted,
            ),
            uiSettings          = MapUiSettings(
                zoomControlsEnabled     = true,
                myLocationButtonEnabled = locationPermissionGranted,
                compassEnabled          = true,
                mapToolbarEnabled       = true,
            ),
        ) {
            if (!locationPermissionGranted) {
                Marker(
                    state = MarkerState(position = calambaLocation),
                    title = "Crossing, Calamba",
                )
            }
        }

        // Placeholder label — disappears once API key is added
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text       = "Map loading — API key pending",
                fontSize   = 10.sp,
                color      = Color.White,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}