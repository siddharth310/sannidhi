package com.smaranam.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smaranam.app.di.AppContainer
import com.smaranam.app.ui.screens.onboarding.AuthScreen
import com.smaranam.app.ui.screens.onboarding.IshtaDevataScreen
import com.smaranam.app.ui.screens.onboarding.OnboardingViewModel
import com.smaranam.app.ui.screens.onboarding.OnboardingViewModelFactory
import com.smaranam.app.ui.screens.onboarding.PreferencesScreen
import com.smaranam.app.ui.screens.onboarding.SplashScreen
import com.smaranam.app.ui.screens.shell.MainShellScreen
import com.smaranam.app.ui.screens.shell.MainShellViewModelFactory

@Composable
fun SmaranamNavHost(
    container: AppContainer,
    navController: NavHostController = rememberNavController(),
) {
    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(container.userSessionRepository),
    )
    val profile by onboardingViewModel.profile.collectAsStateWithLifecycle()
    val mainShellFactory = MainShellViewModelFactory(
        recitationRepository = container.recitationRepository,
    )

    NavHost(
        navController = navController,
        startDestination = OnboardingRoute.Splash,
    ) {
        composable(OnboardingRoute.Splash) {
            SplashScreen(
                resolveDestination = onboardingViewModel::resolveSplashDestination,
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo(OnboardingRoute.Splash) { inclusive = true }
                    }
                },
            )
        }
        composable(OnboardingRoute.Auth) {
            AuthScreen(
                viewModel = onboardingViewModel,
                onAuthenticated = { destination ->
                    navController.navigate(destination) {
                        popUpTo(OnboardingRoute.Auth) { inclusive = true }
                    }
                },
            )
        }
        composable(OnboardingRoute.IshtaDevata) {
            IshtaDevataScreen(
                viewModel = onboardingViewModel,
                onContinue = {
                    navController.navigate(OnboardingRoute.Preferences)
                },
                onSkip = {
                    navController.navigate(OnboardingRoute.Preferences)
                },
            )
        }
        composable(OnboardingRoute.Preferences) {
            PreferencesScreen(
                viewModel = onboardingViewModel,
                onComplete = {
                    navController.navigate(OnboardingRoute.Main) {
                        popUpTo(OnboardingRoute.Splash) { inclusive = true }
                    }
                },
            )
        }
        composable(OnboardingRoute.Main) {
            MainShellScreen(
                viewModel = viewModel(factory = mainShellFactory),
                profile = profile,
                container = container,
                onDismissFirstRunNudge = onboardingViewModel::dismissFirstRunNudge,
                onLogOut = {
                    navController.navigate(OnboardingRoute.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
