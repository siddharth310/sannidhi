package com.smaranam.app.ui.components.mandir

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smaranam.app.domain.model.Script
import com.smaranam.app.ui.layout.naamaluNavigationBarsPadding
import com.smaranam.app.ui.layout.naamaluStatusBarsPadding
import com.smaranam.app.ui.navigation.SmaranamDestination
import com.smaranam.app.ui.theme.SmaranamTheme
import com.smaranam.app.ui.theme.notoSerifFor

@Composable
fun MandirTopBar(
    showBack: Boolean,
    onBack: () -> Unit,
    title: String?,
    script: Script,
    onScriptSelected: (Script) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().naamaluStatusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                if (showBack) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SmaranamTheme.colors.textEmphasis,
                        )
                    }
                }
                Text(
                    text = "నామ",
                    style = SmaranamTheme.typography.greeting.copy(
                        fontFamily = notoSerifFor(Script.TELUGU),
                        fontSize = SmaranamTheme.typography.greeting.fontSize,
                    ),
                    color = SmaranamTheme.colors.primary,
                )
                if (title != null && !showBack) {
                    Text(
                        text = title,
                        style = SmaranamTheme.typography.caption,
                        color = SmaranamTheme.colors.muted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LanguageChip(script = script, onScriptSelected = onScriptSelected)
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, SmaranamTheme.colors.line, CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = SmaranamTheme.colors.textEmphasis,
                    )
                }
            }
        }
        HorizontalDivider(color = SmaranamTheme.colors.line, thickness = 1.dp)
    }
}

@Composable
fun LanguageChip(
    script: Script,
    onScriptSelected: (Script) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = chipLabel(script),
            style = SmaranamTheme.typography.caption,
            color = SmaranamTheme.colors.textPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, SmaranamTheme.colors.gold, RoundedCornerShape(22.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Script.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.menuLabel) },
                    onClick = {
                        expanded = false
                        onScriptSelected(option)
                    },
                )
            }
        }
    }
}

@Composable
fun MandirBottomNav(
    destinations: List<SmaranamDestination>,
    currentRoute: String?,
    onNavigate: (SmaranamDestination) -> Unit,
) {
    Column {
        HorizontalDivider(color = SmaranamTheme.colors.line, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .naamaluNavigationBarsPadding()
                .background(SmaranamTheme.colors.surface)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            destinations.forEach { destination ->
                val selected = currentRoute == destination.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(destination) }
                        .padding(vertical = 8.dp),
                ) {
                    if (selected) {
                        if (SmaranamTheme.colors.isDeepam) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(SmaranamTheme.colors.primary),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .fillMaxWidth(0.35f)
                                    .background(SmaranamTheme.colors.gold),
                            )
                        }
                    } else {
                        Box(modifier = Modifier.height(2.dp))
                    }
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.labelRes),
                        tint = if (selected) SmaranamTheme.colors.primary else SmaranamTheme.colors.muted,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = stringResource(destination.labelRes),
                        style = SmaranamTheme.typography.nav,
                        color = if (selected) SmaranamTheme.colors.primary else SmaranamTheme.colors.muted,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun ContinueRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = SmaranamTheme.colors.gold, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "CONTINUE",
                    style = SmaranamTheme.typography.labelCaps,
                    color = SmaranamTheme.colors.sectionLabel,
                )
                Text(
                    text = title,
                    style = SmaranamTheme.typography.sectionTitle,
                    color = SmaranamTheme.colors.textPrimary,
                )
                Text(
                    text = subtitle,
                    style = SmaranamTheme.typography.caption,
                    color = SmaranamTheme.colors.muted,
                )
            }
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, SmaranamTheme.colors.line, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Continue",
                    tint = SmaranamTheme.colors.primary,
                )
            }
        }
        HorizontalDivider(color = SmaranamTheme.colors.line, thickness = 1.dp)
    }
}

@Composable
fun HairlineListRow(
    label: String?,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                label?.let {
                    Text(
                        text = it.uppercase(),
                        style = SmaranamTheme.typography.labelCaps,
                        color = SmaranamTheme.colors.sectionLabel,
                    )
                }
                Text(
                    text = title,
                    style = SmaranamTheme.typography.sectionTitle,
                    color = SmaranamTheme.colors.textPrimary,
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = SmaranamTheme.typography.caption,
                        color = SmaranamTheme.colors.muted,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SmaranamTheme.colors.muted,
            )
        }
        HorizontalDivider(color = SmaranamTheme.colors.line, thickness = 1.dp)
    }
}

@Composable
fun HubTile(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SmaranamTheme.colors.line, RoundedCornerShape(16.dp))
            .background(SmaranamTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(1.5.dp, SmaranamTheme.colors.primary, RoundedCornerShape(8.dp)),
        )
        Text(
            text = label,
            style = SmaranamTheme.typography.body,
            color = SmaranamTheme.colors.textPrimary,
        )
    }
}

private fun chipLabel(script: Script): String = when (script) {
    Script.TELUGU -> "తె · hi · IAST"
    Script.DEVANAGARI -> "te · हि · IAST"
    Script.TAMIL -> "te · ta · IAST"
    Script.IAST -> "te · hi · IAST"
}

private val Script.menuLabel: String
    get() = when (this) {
        Script.TELUGU -> "Telugu"
        Script.TAMIL -> "Tamil"
        Script.DEVANAGARI -> "Devanagari (Hindi)"
        Script.IAST -> "IAST"
    }
