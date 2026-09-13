package com.naamalu.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.naamalu.app.ui.theme.NaamaluTheme

@Composable
fun NaamaluPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NaamaluTheme.colors.primary,
            contentColor = NaamaluTheme.colors.onPrimary,
            disabledContainerColor = NaamaluTheme.colors.muted.copy(alpha = 0.4f),
            disabledContentColor = NaamaluTheme.colors.onPrimary.copy(alpha = 0.7f),
        ),
    ) {
        Text(text = text, style = NaamaluTheme.typography.label)
    }
}

@Composable
fun NaamaluSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .border(1.dp, NaamaluTheme.colors.hairline, RoundedCornerShape(12.dp)),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = NaamaluTheme.colors.textEmphasis,
        ),
    ) {
        Text(text = text, style = NaamaluTheme.typography.label)
    }
}

@Composable
fun NaamaluTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, style = NaamaluTheme.typography.caption)
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = NaamaluTheme.colors.surface,
            unfocusedContainerColor = NaamaluTheme.colors.surface,
            focusedTextColor = NaamaluTheme.colors.textPrimary,
            unfocusedTextColor = NaamaluTheme.colors.textPrimary,
            focusedLabelColor = NaamaluTheme.colors.muted,
            unfocusedLabelColor = NaamaluTheme.colors.muted,
            cursorColor = NaamaluTheme.colors.textEmphasis,
            focusedIndicatorColor = NaamaluTheme.colors.hairline,
            unfocusedIndicatorColor = NaamaluTheme.colors.muted.copy(alpha = 0.5f),
        ),
    )
}
