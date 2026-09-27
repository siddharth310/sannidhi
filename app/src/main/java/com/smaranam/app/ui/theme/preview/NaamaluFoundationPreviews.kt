package com.smaranam.app.ui.theme.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smaranam.app.domain.model.Script
import com.smaranam.app.ui.icons.NaamaluIcons
import com.smaranam.app.ui.landscape.LandscapeLayers
import com.smaranam.app.ui.theme.NaamaluModule
import com.smaranam.app.ui.theme.NaamaluModules
import com.smaranam.app.ui.theme.NaamaluTheme
import com.smaranam.app.ui.theme.Sky
import com.smaranam.app.ui.theme.SmaranamTheme
import com.smaranam.app.ui.theme.notoSerifFor

@Preview(name = "Tokens · light", showBackground = true, widthDp = 360)
@Composable
private fun TokenSheetLight() {
    SmaranamTheme(darkTheme = false) { TokenSheet() }
}

@Preview(name = "Tokens · Rātri", showBackground = true, widthDp = 360)
@Composable
private fun TokenSheetDark() {
    SmaranamTheme(darkTheme = true) { TokenSheet() }
}

@Composable
private fun TokenSheet() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SmaranamTheme.colors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Ivory / ink", style = SmaranamTheme.typography.body, color = SmaranamTheme.colors.textPrimary)
        NaamaluModule.entries.forEach { module ->
            val c = NaamaluModules.forModule(module)
            Text(
                text = module.name,
                style = SmaranamTheme.typography.label,
                color = c.deep,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.tint)
                    .padding(8.dp),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(name = "Icons · light", showBackground = true, widthDp = 360)
@Composable
private fun IconSetLight() {
    SmaranamTheme(darkTheme = false) { IconGrid() }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(name = "Icons · Rātri", showBackground = true, widthDp = 360)
@Composable
private fun IconSetDark() {
    SmaranamTheme(darkTheme = true) { IconGrid() }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconGrid() {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(SmaranamTheme.colors.background)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        listOf(
            NaamaluIcons.Kalasam,
            NaamaluIcons.Talapatra,
            NaamaluIcons.Mala,
            NaamaluIcons.AratiAku,
            NaamaluIcons.Tithi,
            NaamaluIcons.Diyas,
            NaamaluIcons.Lotus,
            NaamaluIcons.Mandapam,
            NaamaluIcons.Basket,
            NaamaluIcons.Rings,
            NaamaluIcons.Scroll,
            NaamaluIcons.HomeNav,
        ).forEach { icon ->
            Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp))
        }
    }
}

@Preview(name = "Sky · Uṣā", showBackground = true, widthDp = 360, heightDp = 200)
@Composable
private fun SkyUsha() {
    SkyPreview(Sky.USHA)
}

@Preview(name = "Sky · Madhyāhna", showBackground = true, widthDp = 360, heightDp = 200)
@Composable
private fun SkyDay() {
    SkyPreview(Sky.MADHYAHNA)
}

@Preview(name = "Sky · Sandhyā", showBackground = true, widthDp = 360, heightDp = 200)
@Composable
private fun SkyDusk() {
    SkyPreview(Sky.SANDHYA)
}

@Preview(name = "Sky · Rātri", showBackground = true, widthDp = 360, heightDp = 200)
@Composable
private fun SkyNight() {
    SkyPreview(Sky.RATRI)
}

@Composable
private fun SkyPreview(sky: Sky) {
    Box(Modifier.fillMaxWidth().height(200.dp)) {
        LandscapeLayers(sky = sky)
    }
}

@Preview(name = "Type · te · light · 1.0", showBackground = true, fontScale = 1f)
@Preview(name = "Type · hi · light · 1.3", showBackground = true, fontScale = 1.3f)
@Composable
private fun TypeSampleTe() {
    TypeSample(Script.TELUGU)
}

@Preview(name = "Type · IAST · Rātri · 1.3", showBackground = true, fontScale = 1.3f)
@Composable
private fun TypeSampleIastDark() {
    SmaranamTheme(darkTheme = true) { TypeSample(Script.IAST) }
}

@Composable
private fun TypeSample(script: Script) {
    SmaranamTheme {
        Column(
            Modifier
                .background(SmaranamTheme.colors.background)
                .padding(20.dp),
        ) {
            Text(
                text = if (script == Script.IAST) "oṁ gaṇeśāya namaḥ" else "శుభోదయం",
                style = NaamaluTheme.typography.greeting.copy(fontFamily = notoSerifFor(script)),
                color = SmaranamTheme.colors.textPrimary,
            )
            Text(
                text = "Meta · 16sp body",
                style = NaamaluTheme.typography.body,
                color = SmaranamTheme.colors.muted,
            )
        }
    }
}
