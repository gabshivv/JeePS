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
import com.example.jeeps.data.model.Barangay
import com.example.jeeps.data.model.Route
import com.example.jeeps.data.model.barangayName
import com.example.jeeps.data.repository.DestinationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*

@Composable
fun MapSection(
    modifier: Modifier = Modifier,
    activeRoute: Route? = null,
    origin: DestinationResult? = null,
    destination: DestinationResult? = null,
    barangays: List<Barangay> = emptyList()
) {
    val calambaLocation     = LatLng(14.2137, 121.1620)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(calambaLocation, 13f)
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var showMapTypeMenu by remember { mutableStateOf(false) }

    val routePath = remember(activeRoute) {
        activeRoute?.path?.ifEmpty {
            activeRoute.stops.mapNotNull { segment ->
                segment.barangay?.let { LatLng(it.lat, it.lng) }
            }
        } ?: emptyList()
    }

    // Adjust camera to show the route or selected points
    LaunchedEffect(routePath, origin, destination) {
        if (routePath.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            routePath.forEach { builder.include(it) }
            val bounds = builder.build()
            
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 150)
            )
        } else if (destination != null && origin != null) {
            val builder = LatLngBounds.Builder()
            builder.include(LatLng(origin.lat, origin.lng))
            builder.include(LatLng(destination.lat, destination.lng))
            val bounds = builder.build()
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 150)
            )
        } else if (destination != null) {
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngZoom(LatLng(destination.lat, destination.lng), 14f)
            )
        } else if (origin != null) {
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngZoom(LatLng(origin.lat, origin.lng), 14f)
            )
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
                mapType             = mapType,
                isTrafficEnabled    = true
            ),
            uiSettings          = MapUiSettings(
                zoomControlsEnabled     = false,
                myLocationButtonEnabled = locationPermissionGranted,
                compassEnabled          = true,
                mapToolbarEnabled       = false,
            ),
        ) {
            // Visualize all barangays if no route is active
            if (activeRoute == null) {
                barangays.forEach { brgy ->
                    Marker(
                        state = MarkerState(position = LatLng(brgy.lat, brgy.lng)),
                        title = brgy.name,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                        alpha = 0.6f
                    )
                }
            }

            // Draw Selected Origin Marker
            origin?.let {
                Marker(
                    state = MarkerState(position = LatLng(it.lat, it.lng)),
                    title = it.displayName,
                    snippet = "Start Point",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }

            // Draw Selected Destination Marker
            destination?.let {
                Marker(
                    state = MarkerState(position = LatLng(it.lat, it.lng)),
                    title = it.displayName,
                    snippet = "End Point",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }

            // Draw the Route Path
            if (routePath.isNotEmpty()) {
                Polyline(
                    points = routePath,
                    color = Color(0xFF1976D2),
                    width = 12f,
                    jointType = JointType.ROUND,
                    startCap = RoundCap(),
                    endCap = RoundCap()
                )

                activeRoute?.stops?.forEachIndexed { index, segment ->
                    val isOrigin = index == 0
                    val isDest = index == activeRoute.stops.lastIndex
                    val pos = segment.barangay?.let { LatLng(it.lat, it.lng) }
                    
                    if (pos != null) {
                        Marker(
                            state = MarkerState(position = pos),
                            title = segment.barangayName,
                            snippet = if (isOrigin) "Origin" else if (isDest) "Destination" else "Stop",
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (isOrigin) BitmapDescriptorFactory.HUE_AZURE
                                else if (isDest) BitmapDescriptorFactory.HUE_RED
                                else BitmapDescriptorFactory.HUE_ORANGE
                            )
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
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
