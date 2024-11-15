package com.ntg.mybudget.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import com.ntg.core.model.ProfileActions
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.features.home.homeScreen
import com.ntg.features.home.navigateToHome
import com.ntg.features.profile.navigateToProfile
import com.ntg.features.profile.profileScreen
import com.ntg.features.setup.SetupViewModel
import com.ntg.features.setup.navigateToCreateAccount
import com.ntg.features.setup.navigateToCurrencies
import com.ntg.features.setup.navigateToSetup
import com.ntg.features.setup.navigateToSource
import com.ntg.features.setup.setupScreen
import com.ntg.login.LoginViewModel
import com.ntg.login.Login_Route
import com.ntg.login.loginScreen
import com.ntg.login.navigateToCode
import com.ntg.login.navigateToCountries
import com.ntg.login.navigateToLogin
import com.ntg.mybudget.ui.BudgetAppState

@Composable
fun BudgetNavHost(
    appState: BudgetAppState,
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = Login_Route,
    sharedViewModel: SharedViewModel,
    finishLogin:(String)-> Unit
) {
    val navController = appState.navController
    val loginViewModel: LoginViewModel = hiltViewModel()
    val setupViewModel: SetupViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        loginScreen(
            loginViewModel,
            sharedViewModel,
            navigateToCountries = navController::navigateToCountries,
            navigateToCode = navController::navigateToCode,
            navigateToSetup = navController::navigateToSetup,
            onBack = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
            finishLogin = finishLogin
        )

        setupScreen(
            sharedViewModel,
            setupViewModel,
            navigateToSource = navController::navigateToSource,
            navigateToAccount = navController::navigateToCreateAccount,
            onBack = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
            navigateToLogin = navController::navigateToLogin,
            navigateToHome = navController::navigateToHome,
            navigateToCurrencies = navController::navigateToCurrencies
        )

        homeScreen(
            sharedViewModel = sharedViewModel,
            navigateToSource = navController::navigateToSource,
            navigateToAccount = navController::navigateToCreateAccount,
            navigateToProfile = navController::navigateToProfile,
            onShowSnackbar = onShowSnackbar
        )

        profileScreen(
            profileActions = { action ->
                when (action) {
                    ProfileActions.CHANGE_INFO -> navController.navigateToHome()
                    ProfileActions.SESSIONS -> navController.navigateToHome()
                    ProfileActions.APP_UI -> navController.navigateToHome()
                    ProfileActions.NOTIFICATIONS -> navController.navigateToHome()
                    ProfileActions.BANK_MESSAGE -> navController.navigateToHome()
                    else -> {}
                }
            }
        )
    }
}
