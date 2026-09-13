package com.naamalu.app.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naamalu.app.ui.components.NaamaluPrimaryButton
import com.naamalu.app.ui.components.NaamaluTextField
import com.naamalu.app.ui.theme.NaamaluTheme

@Composable
fun AuthScreen(
    viewModel: OnboardingViewModel,
    onAuthenticated: (String) -> Unit,
) {
    val state by viewModel.authState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (state.isSignUp) "Create your account" else "Welcome back",
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Text(
            text = "Phone and password only — calm and simple.",
            style = NaamaluTheme.typography.body,
            color = NaamaluTheme.colors.muted,
        )
        Spacer(modifier = Modifier.height(8.dp))
        NaamaluTextField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = "Phone number",
            keyboardType = KeyboardType.Phone,
        )
        NaamaluTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            isPassword = true,
        )
        state.errorMessage?.let { message ->
            Text(
                text = message,
                style = NaamaluTheme.typography.caption,
                color = NaamaluTheme.colors.error,
            )
        }
        NaamaluPrimaryButton(
            text = if (state.isSignUp) "Sign up" else "Log in",
            enabled = !state.isLoading,
            onClick = { viewModel.submitAuth(onAuthenticated) },
        )
        TextButton(onClick = viewModel::toggleAuthMode) {
            Text(
                text = if (state.isSignUp) {
                    "Already have an account? Log in"
                } else {
                    "New here? Sign up"
                },
                style = NaamaluTheme.typography.label,
                color = NaamaluTheme.colors.textEmphasis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
