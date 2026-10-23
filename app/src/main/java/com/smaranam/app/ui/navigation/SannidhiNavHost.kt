package com.smaranam.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.smaranam.app.content.ContentRepository
import com.smaranam.app.di.AppContainer
import com.smaranam.app.ui.home.HomeScreen
import com.smaranam.app.ui.calendar.CalendarHomeScreen
import com.smaranam.app.ui.calendar.FindNakshatraScreen
import com.smaranam.app.ui.calendar.NaivedyamStubScreen
import com.smaranam.app.ui.calendar.ObservanceScreen
import com.smaranam.app.ui.puja.GodPageScreen
import com.smaranam.app.ui.puja.GuidedPujaScreen
import com.smaranam.app.ui.puja.PujaHomeScreen
import com.smaranam.app.ui.puja.PujaPageScreen
import com.smaranam.app.ui.stories.DeityStoriesScreen
import com.smaranam.app.ui.stories.StoriesHomeScreen
import com.smaranam.app.ui.stories.StoryPlayerScreen
import com.smaranam.app.ui.sankalpam.CompletedJapamScreen
import com.smaranam.app.ui.sankalpam.SankalpamHomeScreen
import com.smaranam.app.ui.sankalpam.SankalpamPlayerScreen
import com.smaranam.app.ui.sankalpam.StartJapamScreen
import com.smaranam.app.ui.onboarding.OnboardingScreen
import com.smaranam.app.ui.profile.ProfileScreen
import com.smaranam.app.ui.theme.Ivory
import androidx.compose.foundation.background
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable data object HomeRoute
@Serializable data object OnboardingRoute
@Serializable data object ProfileRoute
@Serializable data object PujaRoute
@Serializable data class PujaGodRoute(val deity: String)
@Serializable data class PujaIdRoute(val pujaId: String)
@Serializable data class PujaGuidedRoute(val pujaId: String)
@Serializable data object CalendarRoute
@Serializable data class FindNakshatraRoute(val fromMasam: Boolean = false)
@Serializable data class CalendarObsRoute(val obsId: String)
@Serializable data class NaivedyamRoute(val itemId: String)
@Serializable data object SankalpamRoute
@Serializable data class SankalpamNewRoute(val textId: String = "", val goal: Int = -1, val days: Int = -1)
@Serializable data class SankalpamPlayRoute(val id: String)
@Serializable data class SankalpamDoneRoute(val id: String)
@Serializable data object StoriesRoute
@Serializable data class StoriesGodRoute(val deity: String)
@Serializable data class StoryRoute(val storyId: String)

@Composable
fun SannidhiNavHost(container: AppContainer) {
    val settings by container.settingsStore.settings.collectAsState(initial = null)
    val current = settings
    if (current == null) {
        Box(Modifier.fillMaxSize().background(Ivory))
        return
    }
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    val repo: ContentRepository = container.contentRepository
    LaunchedEffect(current.debugToday) {
        container.practiceRepository.closeExpired(current.today())
    }
    fun home() {
        nav.navigate(HomeRoute) {
            popUpTo(HomeRoute) { inclusive = false }
            launchSingleTop = true
        }
    }
    NavHost(
        navController = nav,
        startDestination = if (current.onboarded) HomeRoute else OnboardingRoute,
    ) {
        composable<HomeRoute> {
            HomeScreen(
                store = container.settingsStore,
                settings = current,
                repository = repo,
                practice = container.practiceRepository,
                onProfile = { nav.navigate(ProfileRoute) },
                onLanguage = { lang -> scope.launch { container.settingsStore.setLang(lang) } },
                onPuja = { nav.navigate(PujaRoute) },
                onCalendar = { nav.navigate(CalendarRoute) },
                onSankalpam = { nav.navigate(SankalpamRoute) },
                onStories = { nav.navigate(StoriesRoute) },
                onOpenPuja = { id -> nav.navigate(PujaIdRoute(id)) },
            )
        }
        composable<OnboardingRoute> {
            OnboardingScreen(
                repository = repo,
                store = container.settingsStore,
                lang = current.lang,
                onFinished = {
                    nav.navigate(HomeRoute) {
                        popUpTo(OnboardingRoute) { inclusive = true }
                    }
                },
            )
        }
        composable<ProfileRoute> {
            ProfileScreen(
                settings = current,
                repository = repo,
                store = container.settingsStore,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onDeleted = {
                    scope.launch {
                        container.clearPersonalData()
                        nav.navigate(OnboardingRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onFindNakshatra = { nav.navigate(FindNakshatraRoute(fromMasam = false)) },
            )
        }
        composable<FindNakshatraRoute> { entry ->
            val route = entry.toRoute<FindNakshatraRoute>()
            FindNakshatraScreen(
                settings = current,
                store = container.settingsStore,
                repository = repo,
                fromMasam = route.fromMasam,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onSaved = { nav.popBackStack() },
            )
        }
        composable<PujaRoute> {
            PujaHomeScreen(
                settings = current,
                repository = repo,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onDeity = { deity -> nav.navigate(PujaGodRoute(deity)) },
                onOpenPuja = { id -> nav.navigate(PujaIdRoute(id)) },
            )
        }
        composable<PujaGodRoute> { entry ->
            val route = entry.toRoute<PujaGodRoute>()
            GodPageScreen(
                deityId = route.deity,
                settings = current,
                repository = repo,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onPuja = { pujaId -> nav.navigate(PujaIdRoute(pujaId)) },
                onStories = { nav.navigate(StoriesGodRoute(route.deity)) },
            )
        }
        composable<PujaIdRoute> { entry ->
            val route = entry.toRoute<PujaIdRoute>()
            PujaPageScreen(
                pujaId = route.pujaId,
                settings = current,
                store = container.settingsStore,
                repository = repo,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onGuided = { nav.navigate(PujaGuidedRoute(route.pujaId)) },
                onProfile = { nav.navigate(ProfileRoute) },
            )
        }
        composable<PujaGuidedRoute> { entry ->
            val route = entry.toRoute<PujaGuidedRoute>()
            GuidedPujaScreen(
                pujaId = route.pujaId,
                settings = current,
                store = container.settingsStore,
                repository = repo,
                onClose = { nav.popBackStack() },
            )
        }
        composable<CalendarRoute> {
            CalendarHomeScreen(
                settings = current,
                store = container.settingsStore,
                repository = repo,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onObservance = { obsId -> nav.navigate(CalendarObsRoute(obsId)) },
                onFindNakshatra = { nav.navigate(FindNakshatraRoute(fromMasam = true)) },
            )
        }
        composable<CalendarObsRoute> { entry ->
            val route = entry.toRoute<CalendarObsRoute>()
            ObservanceScreen(
                obsId = route.obsId,
                settings = current,
                repository = repo,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onPuja = { pujaId -> nav.navigate(PujaIdRoute(pujaId)) },
                onStory = { storyId -> nav.navigate(StoryRoute(storyId)) },
                onNaivedyam = { itemId -> nav.navigate(NaivedyamRoute(itemId)) },
            )
        }
        composable<NaivedyamRoute> { entry ->
            val route = entry.toRoute<NaivedyamRoute>()
            NaivedyamStubScreen(route.itemId, current, repo, { nav.popBackStack() }, { home() })
        }
        composable<SankalpamRoute> {
            SankalpamHomeScreen(
                settings = current,
                store = container.settingsStore,
                repository = repo,
                practice = container.practiceRepository,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onPlay = { nav.navigate(SankalpamPlayRoute(it)) },
                onDone = { nav.navigate(SankalpamDoneRoute(it)) },
                onNew = { nav.navigate(SankalpamNewRoute()) },
            )
        }
        composable<SankalpamNewRoute> { entry ->
            val route = entry.toRoute<SankalpamNewRoute>()
            StartJapamScreen(
                settings = current,
                repository = repo,
                practice = container.practiceRepository,
                prefillTextId = route.textId,
                prefillGoal = route.goal,
                prefillDays = route.days,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onStarted = { id ->
                    nav.navigate(SankalpamPlayRoute(id)) {
                        popUpTo<SankalpamNewRoute> { inclusive = true }
                    }
                },
            )
        }
        composable<SankalpamPlayRoute> { entry ->
            val route = entry.toRoute<SankalpamPlayRoute>()
            SankalpamPlayerScreen(
                id = route.id,
                settings = current,
                repository = repo,
                practice = container.practiceRepository,
                onBack = { nav.popBackStack() },
                onHome = { home() },
            )
        }
        composable<SankalpamDoneRoute> { entry ->
            val route = entry.toRoute<SankalpamDoneRoute>()
            CompletedJapamScreen(
                id = route.id,
                settings = current,
                repository = repo,
                practice = container.practiceRepository,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onStartAgain = { textId, goal, days ->
                    nav.navigate(SankalpamNewRoute(textId, goal, days))
                },
            )
        }
        composable<StoriesRoute> {
            StoriesHomeScreen(
                settings = current,
                repository = repo,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onDeity = { nav.navigate(StoriesGodRoute(it)) },
                onStory = { nav.navigate(StoryRoute(it)) },
            )
        }
        composable<StoriesGodRoute> { entry ->
            val route = entry.toRoute<StoriesGodRoute>()
            DeityStoriesScreen(
                deityId = route.deity,
                settings = current,
                repository = repo,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onStory = { nav.navigate(StoryRoute(it)) },
            )
        }
        composable<StoryRoute> { entry ->
            val route = entry.toRoute<StoryRoute>()
            StoryPlayerScreen(
                storyId = route.storyId,
                settings = current,
                repository = repo,
                onBack = { nav.popBackStack() },
                onHome = { home() },
                onPuja = { nav.navigate(PujaIdRoute(it)) },
                onBackToStories = { deity ->
                    nav.navigate(StoriesGodRoute(deity)) {
                        popUpTo(route) { inclusive = true }
                    }
                },
            )
        }
    }
}
