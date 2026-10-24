package app.sannidhi.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import android.graphics.BitmapFactory
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sannidhi.R
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Lang
import app.sannidhi.content.Sacred
import app.sannidhi.content.TextView
import app.sannidhi.ui.theme.Gold
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.Ivory
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.NotoSerifDevanagariFamily
import app.sannidhi.ui.theme.NotoSerifTeluguFamily
import app.sannidhi.ui.theme.CormorantFamily

@Composable
fun FloatingPillButton(
    onClick: () -> Unit,
    contentDescription: String,
    icon: ImageVector,
) {
    Box(
        Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Ivory)
            .border(1.dp, Gold, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = Maroon)
    }
}

@Composable
fun FloatingPills(onBack: () -> Unit, onHome: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingPillButton(onBack, stringResource(R.string.back), Icons.AutoMirrored.Filled.ArrowBack)
        FloatingPillButton(onHome, stringResource(R.string.home), Icons.Filled.Home)
    }
}

@Composable
fun TopBar(
    title: String,
    onBack: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 64.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "←  ${stringResource(R.string.back)}",
            color = Maroon,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .clickable(onClick = onBack)
                .padding(horizontal = 8.dp)
                .wrapContentHeight(),
        )
        Text(
            text = title,
            color = Ink,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
        )
        Row(
            Modifier
                .heightIn(min = 56.dp)
                .clickable(onClick = onHome)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(Icons.Filled.Home, contentDescription = null, tint = Maroon, modifier = Modifier.size(22.dp))
            Text(stringResource(R.string.home), color = Maroon, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun Modifier.wrapContentHeight() = this.then(Modifier)

@Composable
fun ModuleCard(
    script: String,
    english: String,
    tint: Color,
    kind: ModuleKind,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .heightIn(min = 168.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(tint)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        ModuleGlyph(kind, Modifier.size(64.dp))
        Text(script, color = Ink, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text(english, color = InkSoft, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
    }
}

@Composable
fun ComingUpCard(
    kicker: String,
    title: String,
    subtitle: String,
    action: String,
    showDraft: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageId: String? = null,
    fallbackSymbolId: String? = null,
    repository: ContentRepository? = null,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Maroon)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (repository != null && (imageId != null || fallbackSymbolId != null)) {
            ImageOrFallback(
                imageId = imageId,
                fallbackSymbolId = fallbackSymbolId,
                tint = Ivory,
                repository = repository,
                modifier = Modifier.size(88.dp),
                corner = 16.dp,
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(kicker, color = Gold, style = MaterialTheme.typography.labelLarge)
            Text(title, color = Ivory, style = MaterialTheme.typography.headlineMedium)
            if (subtitle.isNotBlank()) {
                Text(subtitle, color = Ivory.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyLarge)
            }
            if (showDraft) {
                Text(stringResource(R.string.draft), color = Ivory, style = MaterialTheme.typography.labelLarge)
            }
            Text(action, color = Ivory, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun SacredText(
    sacred: Sacred,
    view: TextView,
    lang: Lang,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    color: Color = Ink,
) {
    val family: FontFamily = when (view) {
        TextView.Iast -> CormorantFamily
        TextView.Devanagari -> NotoSerifDevanagariFamily
        TextView.Script -> if (lang == Lang.te) NotoSerifTeluguFamily else NotoSerifDevanagariFamily
    }
    Text(
        text = sacred.pick(view, lang),
        modifier = modifier,
        color = color,
        fontFamily = family,
        fontSize = fontSize,
        lineHeight = fontSize * 1.45f,
    )
}

@Composable
fun ImageOrFallback(
    imageId: String?,
    fallbackSymbolId: String?,
    tint: Color,
    repository: ContentRepository,
    modifier: Modifier = Modifier.size(88.dp),
    corner: Dp = 16.dp,
    containSymbol: Boolean = false,
    symbolFraction: Float = 0.46f,
    missingLabel: String? = null,
) {
    val context = LocalContext.current
    var bitmap by remember(imageId, fallbackSymbolId) { mutableStateOf<android.graphics.Bitmap?>(null) }
    var contained by remember(imageId, fallbackSymbolId, containSymbol) { mutableStateOf(containSymbol) }
    LaunchedEffect(imageId, fallbackSymbolId, containSymbol) {
        val primary = repository.imagePath(imageId)
        val path = primary ?: repository.imagePath(fallbackSymbolId)
        contained = containSymbol || primary == null
        bitmap = path?.let { asset ->
            runCatching { context.assets.open(asset).use { BitmapFactory.decodeStream(it) } }.getOrNull()
        }
    }
    Box(
        modifier
            .clip(RoundedCornerShape(corner))
            .background(tint)
            .border(1.dp, Gold, RoundedCornerShape(corner)),
        contentAlignment = Alignment.Center,
    ) {
        val loaded = bitmap
        if (loaded != null && contained) {
            Image(
                bitmap = loaded.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(symbolFraction).fillMaxHeight(symbolFraction),
                contentScale = ContentScale.Fit,
            )
        } else if (loaded != null) {
            Image(
                bitmap = loaded.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
        } else if (!missingLabel.isNullOrBlank()) {
            Text(
                text = missingLabel,
                color = Maroon,
                fontFamily = CormorantFamily,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(12.dp),
            )
        } else {
            val mark = (fallbackSymbolId ?: imageId).orEmpty().substringAfterLast('.').take(1).uppercase()
            Text(
                text = mark.ifBlank { "•" },
                color = Maroon,
                fontFamily = CormorantFamily,
                fontSize = 32.sp,
            )
        }
    }
}
