package com.example.jeeps.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.data.model.Route
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*

@Composable
fun MapSection(
    modifier: Modifier = Modifier,
    activeRoute: Route? = null
) {
    val calambaLocation     = LatLng(14.2046, 121.1560)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(calambaLocation, 12f)
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var showMapTypeMenu by remember { mutableStateOf(false) }

    // Adjust camera if a route is active
    LaunchedEffect(activeRoute) {
        activeRoute?.path?.firstOrNull()?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 12f)
        }
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

    Box(modifier = modifier) {
        GoogleMap(
            modifier            = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
            cameraPositionState = cameraPositionState,
            properties          = MapProperties(
                isMyLocationEnabled = locationPermissionGranted,
                mapType             = mapType
            ),
            uiSettings          = MapUiSettings(
                zoomControlsEnabled     = false,
                myLocationButtonEnabled = locationPermissionGranted,
                compassEnabled          = true,
                mapToolbarEnabled       = false,
            ),
        ) {
            // Draw the Route Path
            activeRoute?.path?.let { points ->
                if (points.isNotEmpty()) {
                    Polyline(
                        points = points,
                        color = Color(0xFF1976D2), // Jeepney Route Blue
                        width = 15f,
                        jointType = JointType.ROUND,
                        startCap = RoundCap(),
                        endCap = RoundCap()
                    )

                    // Origin Marker
                    Marker(
                        state = MarkerState(position = points.first()),
                        title = "Origin: ${activeRoute.originName}",
                    )

                    // Destination Marker
                    Marker(
                        state = MarkerState(position = points.last()),
                        title = "Destination: ${activeRoute.destinationName}",
                    )
                }
            }

            if (activeRoute == null && !locationPermissionGranted) {
                Marker(
                    state = MarkerState(position = calambaLocation),
                    title = "Crossing, Calamba",
                )
            }
        }

        // Map Type Selector Button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { showMapTypeMenu = true },
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(2.dp)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Map Type", modifier = Modifier.size(20.dp))
            }

            DropdownMenu(
                expanded = showMapTypeMenu,
                onDismissRequest = { showMapTypeMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                MapTypeOption("Default", MapType.NORMAL, mapType) { mapType = it; showMapTypeMenu = false }
                MapTypeOption("Satellite", MapType.SATELLITE, mapType) { mapType = it; showMapTypeMenu = false }
                MapTypeOption("Terrain", MapType.TERRAIN, mapType) { mapType = it; showMapTypeMenu = false }
                MapTypeOption("Hybrid", MapType.HYBRID, mapType) { mapType = it; showMapTypeMenu = false }
            }
        }
    }
}

@Composable
private fun MapTypeOption(
    label: String,
    type: MapType,
    selectedType: MapType,
    onClick: (MapType) -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                fontSize = 14.sp,
                color = if (type == selectedType) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (type == selectedType) FontWeight.Bold else null
            )
        },
        onClick = { onClick(type) }
    )
}
