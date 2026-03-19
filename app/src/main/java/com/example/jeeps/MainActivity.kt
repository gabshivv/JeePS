package com.example.jeeps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jeeps.ui.theme.JeePSTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JeePSTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        // This ensures the content doesn't hide behind the Bottom Navigation Bar
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContainerColor = Color.White,
            sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            // Increased peek height slightly to show the start of the terminals
            sheetPeekHeight = 200.dp,
            sheetDragHandle = { BottomSheetDefaults.DragHandle() },
            sheetContent = {
                // Apply innerPadding here to push the sheet content up
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    SearchBottomSheetContent()
                }
            },
            modifier = Modifier.padding(innerPadding)
        ) { bottomSheetPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0038A8))
                    .padding(bottomSheetPadding)
            ) {
                HeaderSection()
                MapSection()
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("JEEPS · PH", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            LanguageToggle()
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Where are you\ngoing?",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFFCC00), modifier = Modifier.size(16.dp))
            Text(" Calamba, Laguna · Location detected", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        }
    }
}

@Composable
fun LanguageToggle() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.2f),
        modifier = Modifier.clickable { /* TODO: Toggle Language */ }
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Text("EN", modifier = Modifier
                .background(Color.White, CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp), color = Color.Black)
            Text("TL", modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp), color = Color.White)
        }
    }
}

@Composable
fun MapSection() {
    // Initializing map at Calamba, Laguna as per detected location
    val calambaLocation = LatLng(14.2046, 121.1560)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(calambaLocation, 14f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // You can add MapProperties here later to hide UI controls if you want it cleaner
        )
    }
}

@Composable
fun SearchBottomSheetContent() {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 8.dp)
        .padding(bottom = 24.dp) // Extra padding for bottom nav clearance
    ) {
        // Input Fields Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF5F5F5)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // FROM Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: Open From Search */ }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "From", tint = Color.Blue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("FROM", fontSize = 10.sp, color = Color.Gray)
                        Text("Crossing, Calamba", fontSize = 16.sp, color = Color.Black)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

                // TO Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: Open To Search */ }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "To", tint = Color.Red, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("TO", fontSize = 10.sp, color = Color.Gray)
                        Text("Where are you going?", fontSize = 16.sp, color = Color.LightGray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Find Routes Button
        Button(
            onClick = { /* TODO: Calculate Routes */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCE1126)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Routes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nearby Terminals Header with 'More Options' interaction
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("NEARBY TERMINALS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

            // Text Button acting as a "More Options" indicator
            TextButton(
                onClick = { /* TODO: Open Terminal List */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("See All", fontSize = 12.sp, color = Color.Blue)
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "See More", tint = Color.Blue, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal scrolling terminals
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp) // Adds a little "bounce" room
        ) {
            items(listOf("Crossing", "San Pedro", "Binan", "Cabuyao")) { terminal ->
                TerminalCard(terminal)
            }
        }
    }
}

@Composable
fun TerminalCard(name: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF0F4FF),
        modifier = Modifier
            .width(160.dp)
            .clickable { /* TODO: Select Terminal */ }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color.Green, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("12 units", fontSize = 12.sp, color = Color.Gray)
            Text("Line A · B · C", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color.White) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, true),
            Triple("Explore", Icons.Default.Search, false),
            Triple("Map", Icons.Default.Place, false),
            Triple("Settings", Icons.Default.Settings, false)
        )
        items.forEach { (label, icon, selected) ->
            NavigationBarItem(
                selected = selected,
                onClick = { /* TODO: Navigate Tabs */ },
                label = { Text(label) },
                icon = { Icon(icon, contentDescription = label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Blue,
                    selectedTextColor = Color.Blue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )
            )
        }
    }
}