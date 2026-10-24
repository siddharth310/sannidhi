package app.sannidhi.ui.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.sannidhi.R
import app.sannidhi.content.ContentRepository
import app.sannidhi.settings.KathaPlace
import app.sannidhi.ui.common.ImageOrFallback
import app.sannidhi.ui.common.ModuleKind
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.TintStories

const val KATHA_KEEP_MS = 12L * 60 * 60 * 1000

fun KathaPlace.stillOpen(now: Long = System.currentTimeMillis()): Boolean = now - at < KATHA_KEEP_MS

@Composable
fun ChavithiKathaCard(
    coverId: String?,
    symbolId: String?,
    repository: ContentRepository,
    place: KathaPlace?,
    heading: String,
    onOpen: (part: Int, beat: Int) -> Unit,
) {
    val talk = stringResource(R.string.chavithi_katha_talkback)
    val open = place?.takeIf { it.stillOpen() }
    val part = (open?.part ?: 0) + 1
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(TintStories)
            .clickable { onOpen(open?.part ?: 0, open?.beat ?: 0) }
            .semantics(mergeDescendants = true) { contentDescription = talk }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(heading, color = Maroon, style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ImageOrFallback(
                imageId = coverId,
                fallbackSymbolId = symbolId,
                tint = TintStories,
                repository = repository,
                modifier = Modifier.weight(0.42f).aspectRatio(4f / 3f),
                corner = 16.dp,
                module = ModuleKind.Stories,
            )
            Column(Modifier.weight(0.58f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.chavithi_katha_line), color = Ink, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (open == null) {
                        stringResource(R.string.read_or_listen)
                    } else {
                        stringResource(R.string.continue_katha_part, part, 2)
                    },
                    color = Maroon,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(vertical = 16.dp),
                )
            }
        }
    }
}
