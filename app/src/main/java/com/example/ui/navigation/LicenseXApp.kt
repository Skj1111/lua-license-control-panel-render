package com.example.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.domain.UserRole
import com.example.ui.components.BrandMark
import com.example.ui.components.CyberPanelBackground
import com.example.ui.screens.AllPanelKeysScreen
import com.example.ui.screens.AnnouncementControlScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CommandCenterScreen
import com.example.ui.screens.PendingReferralScreen
import com.example.ui.screens.SessionLoadingScreen
import com.example.ui.screens.SuperOwnerOverviewScreen
import com.example.ui.screens.SuspendedAccessScreen
import com.example.ui.screens.SystemControlScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.PanelSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VoidBackground
import com.example.ui.viewmodel.AuthGateState
import com.example.ui.viewmodel.LicenseXViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object CommandCenterRoute

@Serializable
object SuperOwnerOverviewRoute

@Serializable
object SystemControlRoute

@Serializable
object AnnouncementControlRoute

@Serializable
object AllPanelKeysRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseXApp(viewModel: LicenseXViewModel = viewModel()) {
    val authState by viewModel.authGateState.collectAsStateWithLifecycle()
    val isAuthBusy by viewModel.isAuthBusy.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = VoidBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = authState) {
                is AuthGateState.Loading -> SessionLoadingScreen()
                is AuthGateState.Unauthenticated -> AuthScreen(
                    isBusy = isAuthBusy,
                    onLogin = { u, p -> viewModel.login(u, p) },
                    onRegister = { u, p, onDone -> viewModel.register(u, p, onDone) }
                )
                is AuthGateState.PendingReferral -> PendingReferralScreen(
                    user = state.user,
                    onRedeem = { code -> viewModel.redeemReferral(code) {} },
                    onSwitchAccount = { viewModel.logout() }
                )
                is AuthGateState.Suspended -> SuspendedAccessScreen(
                    onSwitchAccount = { viewModel.logout() }
                )
                is AuthGateState.Active -> {
                    val user = state.user
                    val role = UserRole.fromWire(user.role)
                    val isSuperOwner = role == UserRole.SUPER_OWNER
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry?.destination?.route.orEmpty()

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val useRail = maxWidth >= 840.dp

                        Row(modifier = Modifier.fillMaxSize()) {
                            if (useRail) {
                                NavigationRail(
                                    containerColor = PanelSurface,
                                    contentColor = TextPrimary,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    Spacer(Modifier.padding(top = 12.dp))
                                    BrandMark()
                                    Spacer(Modifier.padding(top = 20.dp))
                                    NavigationRailItem(
                                        selected = currentRoute.contains("CommandCenterRoute"),
                                        onClick = {
                                            navController.navigate(CommandCenterRoute) {
                                                launchSingleTop = true
                                            }
                                        },
                                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                                        label = { Text("Overview") },
                                        colors = NavigationRailItemDefaults.colors(
                                            selectedIconColor = CyberCyan,
                                            selectedTextColor = CyberCyan,
                                            indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        )
                                    )
                                    if (isSuperOwner) {
                                        NavigationRailItem(
                                            selected = currentRoute.contains("SuperOwnerOverviewRoute"),
                                            onClick = {
                                                navController.navigate(SuperOwnerOverviewRoute) {
                                                    launchSingleTop = true
                                                }
                                            },
                                            icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Super Owner") },
                                            label = { Text("Global") },
                                            colors = NavigationRailItemDefaults.colors(
                                                selectedIconColor = CyberCyan,
                                                selectedTextColor = CyberCyan,
                                                indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            )
                                        )
                                        NavigationRailItem(
                                            selected = currentRoute.contains("AllPanelKeysRoute"),
                                            onClick = {
                                                navController.navigate(AllPanelKeysRoute) {
                                                    launchSingleTop = true
                                                }
                                            },
                                            icon = { Icon(Icons.Default.Key, contentDescription = "All Keys") },
                                            label = { Text("Keys") },
                                            colors = NavigationRailItemDefaults.colors(
                                                selectedIconColor = CyberCyan,
                                                selectedTextColor = CyberCyan,
                                                indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            )
                                        )
                                        NavigationRailItem(
                                            selected = currentRoute.contains("SystemControlRoute"),
                                            onClick = {
                                                navController.navigate(SystemControlRoute) {
                                                    launchSingleTop = true
                                                }
                                            },
                                            icon = { Icon(Icons.Default.Settings, contentDescription = "System Control") },
                                            label = { Text("System") },
                                            colors = NavigationRailItemDefaults.colors(
                                                selectedIconColor = CyberCyan,
                                                selectedTextColor = CyberCyan,
                                                indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            )
                                        )
                                        NavigationRailItem(
                                            selected = currentRoute.contains("AnnouncementControlRoute"),
                                            onClick = {
                                                navController.navigate(AnnouncementControlRoute) {
                                                    launchSingleTop = true
                                                }
                                            },
                                            icon = { Icon(Icons.Default.Campaign, contentDescription = "Announcement") },
                                            label = { Text("Announce") },
                                            colors = NavigationRailItemDefaults.colors(
                                                selectedIconColor = CyberCyan,
                                                selectedTextColor = CyberCyan,
                                                indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            )
                                        )
                                    }
                                    Spacer(Modifier.weight(1f))
                                    IconButton(onClick = { viewModel.logout() }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = stringResource(R.string.cd_sign_out),
                                            tint = CyberCyan
                                        )
                                    }
                                    Spacer(Modifier.padding(bottom = 12.dp))
                                }
                            }

                            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                TopAppBar(
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = PanelSurface.copy(alpha = 0.92f),
                                        titleContentColor = TextPrimary
                                    ),
                                    title = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (!useRail) {
                                                BrandMark()
                                                Spacer(Modifier.width(10.dp))
                                            }
                                            Column {
                                                Text(
                                                    "LICENSE//X",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = CyberCyan,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    "${user.username ?: user.name ?: "OPERATOR"} · ${role.label.uppercase()}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = CyberViolet,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    },
                                    actions = {
                                        IconButton(onClick = { viewModel.logout() }) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.Logout,
                                                contentDescription = stringResource(R.string.cd_sign_out),
                                                tint = CyberCyan
                                            )
                                        }
                                    }
                                )

                                CyberPanelBackground(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                    NavHost(
                                        navController = navController,
                                        startDestination = CommandCenterRoute,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        composable<CommandCenterRoute> {
                                            CommandCenterScreen(
                                                user = user,
                                                viewModel = viewModel,
                                                onNavigateSuperOwner = {
                                                    navController.navigate(SuperOwnerOverviewRoute) { launchSingleTop = true }
                                                },
                                                onNavigateSystemControl = {
                                                    navController.navigate(SystemControlRoute) { launchSingleTop = true }
                                                },
                                                onNavigateAnnouncementControl = {
                                                    navController.navigate(AnnouncementControlRoute) { launchSingleTop = true }
                                                },
                                                onShowMessage = { msg ->
                                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                                }
                                            )
                                        }
                                        composable<SuperOwnerOverviewRoute> {
                                            SuperOwnerOverviewScreen(
                                                user = user,
                                                viewModel = viewModel,
                                                onNavigateBack = { navController.popBackStack() },
                                                onNavigateAllKeys = {
                                                    navController.navigate(AllPanelKeysRoute) { launchSingleTop = true }
                                                }
                                            )
                                        }
                                        composable<AllPanelKeysRoute> {
                                            AllPanelKeysScreen(
                                                user = user,
                                                viewModel = viewModel,
                                                onNavigateBack = { navController.popBackStack() }
                                            )
                                        }
                                        composable<SystemControlRoute> {
                                            SystemControlScreen(
                                                user = user,
                                                viewModel = viewModel,
                                                onNavigateBack = { navController.popBackStack() }
                                            )
                                        }
                                        composable<AnnouncementControlRoute> {
                                            AnnouncementControlScreen(
                                                user = user,
                                                viewModel = viewModel,
                                                onNavigateBack = { navController.popBackStack() }
                                            )
                                        }
                                    }
                                }

                                if (!useRail && isSuperOwner) {
                                    NavigationBar(
                                        containerColor = PanelSurface,
                                        modifier = Modifier.navigationBarsPadding()
                                    ) {
                                        NavigationBarItem(
                                            selected = currentRoute.contains("CommandCenterRoute"),
                                            onClick = {
                                                navController.navigate(CommandCenterRoute) { launchSingleTop = true }
                                            },
                                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                                            label = { Text("Command") },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = CyberCyan,
                                                selectedTextColor = CyberCyan,
                                                indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            )
                                        )
                                        NavigationBarItem(
                                            selected = currentRoute.contains("SuperOwnerOverviewRoute"),
                                            onClick = {
                                                navController.navigate(SuperOwnerOverviewRoute) { launchSingleTop = true }
                                            },
                                            icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Global") },
                                            label = { Text("Global") },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = CyberCyan,
                                                selectedTextColor = CyberCyan,
                                                indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            )
                                        )
                                        NavigationBarItem(
                                            selected = currentRoute.contains("SystemControlRoute"),
                                            onClick = {
                                                navController.navigate(SystemControlRoute) { launchSingleTop = true }
                                            },
                                            icon = { Icon(Icons.Default.Settings, contentDescription = "System") },
                                            label = { Text("System") },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = CyberCyan,
                                                selectedTextColor = CyberCyan,
                                                indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            )
                                        )
                                        NavigationBarItem(
                                            selected = currentRoute.contains("AnnouncementControlRoute"),
                                            onClick = {
                                                navController.navigate(AnnouncementControlRoute) { launchSingleTop = true }
                                            },
                                            icon = { Icon(Icons.Default.Campaign, contentDescription = "Announce") },
                                            label = { Text("Announce") },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = CyberCyan,
                                                selectedTextColor = CyberCyan,
                                                indicatorColor = CyberCyan.copy(alpha = 0.14f),
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
