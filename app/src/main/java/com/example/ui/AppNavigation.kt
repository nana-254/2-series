package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.MainViewModel

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen { showSplash = false }
        return
    }

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            Column {
                MStripeLine(modifier = Modifier.fillMaxWidth(), height = 2.dp)
                NavigationBar(
                    containerColor = Color(0xF208090C),
                    tonalElevation = 0.dp,
                    modifier = Modifier.border(0.5.dp, Color(0x20FFFFFF))
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp) },
                        selected = currentRoute == "home",
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color(0xFF888E99),
                            unselectedTextColor = Color(0xFF888E99)
                        ),
                        onClick = { navController.navigate("home") { popUpTo(navController.graph.startDestinationId); launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Build, contentDescription = "Configure") },
                        label = { Text("Mods", fontSize = 11.sp) },
                        selected = currentRoute == "configure",
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color(0xFF888E99),
                            unselectedTextColor = Color(0xFF888E99)
                        ),
                        onClick = { navController.navigate("configure") { popUpTo(navController.graph.startDestinationId); launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Compare") },
                        label = { Text("Specs", fontSize = 11.sp) },
                        selected = currentRoute == "compare",
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color(0xFF888E99),
                            unselectedTextColor = Color(0xFF888E99)
                        ),
                        onClick = { navController.navigate("compare") { popUpTo(navController.graph.startDestinationId); launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Calculate, contentDescription = "TCO") },
                        label = { Text("KRA TCO", fontSize = 11.sp) },
                        selected = currentRoute == "tco",
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color(0xFF888E99),
                            unselectedTextColor = Color(0xFF888E99)
                        ),
                        onClick = { navController.navigate("tco") { popUpTo(navController.graph.startDestinationId); launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.ViewInAr, contentDescription = "3D Viewer") },
                        label = { Text("3D Twin", fontSize = 11.sp) },
                        selected = currentRoute == "viewer",
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color(0xFF888E99),
                            unselectedTextColor = Color(0xFF888E99)
                        ),
                        onClick = { navController.navigate("viewer") { popUpTo(navController.graph.startDestinationId); launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", fontSize = 11.sp) },
                        selected = currentRoute == "settings",
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color(0xFF888E99),
                            unselectedTextColor = Color(0xFF888E99)
                        ),
                        onClick = { navController.navigate("settings") { popUpTo(navController.graph.startDestinationId); launchSingleTop = true } }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToConfigure = { navController.navigate("configure") },
                    onNavigateToTco = { navController.navigate("tco") }
                )
            }
            composable("configure") { ConfigureScreen(viewModel) }
            composable("compare") { ComparisonScreen(viewModel) }
            composable("viewer") { ModelViewerScreen(viewModel) }
            composable("tco") { TCOCalculatorScreen(viewModel) }
            composable("settings") { SettingsScreen(viewModel) }
        }
    }
}
