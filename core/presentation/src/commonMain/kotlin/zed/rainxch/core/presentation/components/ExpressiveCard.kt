package zed.rainxch.core.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpressiveCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    // Long-press without tap leaves the gesture orphaned: the card looks
    // tappable but only responds to a hold. Fail loud so the API contract
    // is obvious at the call site.
    check(onLongClick == null || onClick != null) {
        "ExpressiveCard: onLongClick requires onClick"
    }
    when {
        onClick != null && onLongClick != null -> {
            // ElevatedCard's built-in `onClick` doesn't expose long-press;
            // route both gestures through `combinedClickable` on the modifier
            // instead so callers can attach a hide-menu without sacrificing
            // the tap ripple.
            ElevatedCard(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = onClick,
                            onLongClick = onLongClick,
                        ),
                colors =
                    CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                shape = RoundedCornerShape(32.dp),
                content = { content() },
            )
        }

        onClick != null -> {
            ElevatedCard(
                modifier = modifier.fillMaxWidth(),
                colors =
                    CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                onClick = onClick,
                shape = RoundedCornerShape(32.dp),
                content = { content() },
            )
        }

        else -> {
            ElevatedCard(
                modifier = modifier.fillMaxWidth(),
                colors =
                    CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                shape = RoundedCornerShape(32.dp),
                content = { content() },
            )
        }
    }
}
