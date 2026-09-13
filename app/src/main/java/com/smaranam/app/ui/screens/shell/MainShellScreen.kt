package com.smaranam.app.ui.screens.shell

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smaranam.app.di.AppContainer
import com.smaranam.app.domain.model.SeedDeities
import com.smaranam.app.domain.model.UserProfile
import com.smaranam.app.ui.navigation.MainRoute
import com.smaranam.app.ui.navigation.bottomDestinations
import com.smaranam.app.ui.screens.home.HomeScreen
import com.smaranam.app.ui.screens.japa.JapaScreen
import com.smaranam.app.ui.screens.library.DeityDetailScreen
import com.smaranam.app.ui.screens.library.DeityListScreen
import com.smaranam.app.ui.screens.library.LibraryViewModel
import com.smaranam.app.ui.screens.library.LibraryViewModelFactory
import com.smaranam.app.ui.screens.library.NaamavaliDetailScreen
import com.smaranam.app.ui.screens.saadhana.DayDetailScreen
import com.smaranam.app.ui.screens.saadhana.SaadhanaScreen
import com.smaranam.app.ui.screens.saadhana.SaadhanaViewModel
import com.smaranam.app.ui.screens.saadhana.SaadhanaViewModelFactory
import com.smaranam.app.ui.screens.settings.SettingsScreen
import com.smaranam.app.ui.screens.settings.SettingsViewModel
import com.smaranam.app.ui.screens.settings.SettingsViewModelFactory
import com.smaranam.app.ui.theme.SmaranamTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainShellScreen(
    profile: UserProfile,
    container: AppContainer,
    onDismissFirstRunNudge: () -> Unit,
    onLogOut: () -> Unit,
    viewModel: MainShellViewModel = viewModel(
        factory = MainShellViewModelFactory(container.recitationRepository),
    ),
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val summary by viewModel.summary.collectAsStateWithLifecycle()

    val libraryViewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModelFactory(
            contentRepository = container.contentRepository,
            recitationRepository = container.recitationRepository,
            userSessionRepository = container.userSessionRepository,
        ),
    )
    val saadhanaViewModel: SaadhanaViewModel = viewModel(
        factory = SaadhanaViewModelFactory(container.recitationRepository),
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(container.userSessionRepository),
    )

    val libraryDetail by libraryViewModel.detailState.collectAsStateWithLifecycle()

    val showBottomBar = currentRoute in MainRoute.bottomTabs
    val topBarTitle = topBarTitle(currentRoute, libraryDetail.naamavali?.title, navBackStackEntry?.arguments)

    Scaffold(
        containerColor = SmaranamTheme.colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = SmaranamTheme.typography.title,
                        color = SmaranamTheme.colors.textEmphasis,
                    )
                },
                navigationIcon = {
                    if (!showBottomBar && currentRoute != MainRoute.Settings) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SmaranamTheme.colors.textEmphasis,
                            )
                        }
                    } else if (currentRoute == MainRoute.Settings) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SmaranamTheme.colors.textEmphasis,
                            )
                        }
                    }
                },
                actions = {
                    if (showBottomBar) {
                        IconButton(onClick = { navController.navigate(MainRoute.Settings) }) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                tint = SmaranamTheme.colors.textEmphasis,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SmaranamTheme.colors.background,
                ),
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = SmaranamTheme.colors.surface,
                    tonalElevation = 0.dp,
                ) {
                    bottomDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = stringResource(destination.labelRes),
                                    tint = if (currentRoute == destination.route) {
                                        SmaranamTheme.colors.textEmphasis
                                    } else {
                                        SmaranamTheme.colors.muted
                                    },
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(destination.labelRes),
                                    style = SmaranamTheme.typography.label,
                                    color = if (currentRoute == destination.route) {
                                        SmaranamTheme.colors.textEmphasis
                                    } else {
                                        SmaranamTheme.colors.muted
                                    },
                                )
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainRoute.Home,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(MainRoute.Home) {
                HomeScreen(
                    todayCount = summary.todayCount,
                    streakCurrent = summary.streak.current,
                    ishtaDevataName = profile.ishtaDevataName,
                    showFirstRunNudge = profile.showFirstRunNudge,
                    onDismissFirstRunNudge = onDismissFirstRunNudge,
                )
            }
            composable(MainRoute.Library) {
                DeityListScreen(
                    viewModel = libraryViewModel,
                    onDeityClick = { deityId ->
                        navController.navigate(MainRoute.deityDetail(deityId))
                    },
                )
            }
            composable(
                route = MainRoute.DeityDetail,
                arguments = listOf(navArgument("deityId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val deityId = backStackEntry.arguments?.getString("deityId") ?: return@composable
                val deityName = SeedDeities.findById(deityId)?.name ?: "Deity"
                DeityDetailScreen(
                    deityId = deityId,
                    deityName = deityName,
                    viewModel = libraryViewModel,
                    onNaamavaliClick = { naamavaliId ->
                        navController.navigate(MainRoute.naamavaliDetail(naamavaliId))
                    },
                )
            }
            composable(
                route = MainRoute.NaamavaliDetail,
                arguments = listOf(navArgument("naamavaliId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val naamavaliId = backStackEntry.arguments?.getString("naamavaliId") ?: return@composable
                val userId = profile.userId ?: return@composable
                NaamavaliDetailScreen(
                    naamavaliId = naamavaliId,
                    userId = userId,
                    viewModel = libraryViewModel,
                )
            }
            composable(MainRoute.Japa) {
                JapaScreen()
            }
            composable(MainRoute.Saadhana) {
                SaadhanaScreen(
                    viewModel = saadhanaViewModel,
                    onDayClick = { date ->
                        navController.navigate(MainRoute.dayDetail(date.toString()))
                    },
                )
            }
            composable(
                route = MainRoute.DayDetail,
                arguments = listOf(navArgument("date") { type = NavType.StringType }),
            ) { backStackEntry ->
                val dateIso = backStackEntry.arguments?.getString("date") ?: return@composable
                DayDetailScreen(dateIso = dateIso, viewModel = saadhanaViewModel)
            }
            composable(MainRoute.Settings) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onLogOut = onLogOut,
                )
            }
        }
    }
}

private fun topBarTitle(
    route: String?,
    naamavaliTitle: String?,
    arguments: android.os.Bundle?,
): String {
    return when {
        route == MainRoute.Home -> "Mandir"
        route == MainRoute.Library -> "Library"
        route == MainRoute.Japa -> "Japa"
        route == MainRoute.Saadhana -> "Saadhana"
        route == MainRoute.Settings -> "Settings"
        route?.startsWith("library/deity/") == true -> {
            val deityId = arguments?.getString("deityId")
            SeedDeities.findById(deityId.orEmpty())?.name ?: "Deity"
        }
        route?.startsWith("library/naamavali/") == true -> naamavaliTitle ?: "Naamavali"
        route?.startsWith("saadhana/day/") == true -> "Day detail"
        else -> "Smaranam"
    }
}
