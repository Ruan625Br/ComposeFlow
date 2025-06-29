package io.composeflow.ui.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun PlaceholderContainer(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .padding(16.dp)
                .fillMaxWidth(),
    ) {
        PlaceholderItem(
            height = 48.dp,
            startPadding = 32.dp,
            endPadding = 32.dp,
        )

        Spacer(Modifier.height(48.dp))

        PlaceholderItem()
        Spacer(Modifier.height(16.dp))

        PlaceholderItem()
        Spacer(Modifier.height(16.dp))

        PlaceholderItem(endPadding = 128.dp)
        Spacer(Modifier.height(16.dp))

        PlaceholderItem(endPadding = 128.dp)
        Spacer(Modifier.height(16.dp))

        PlaceholderItem()
        Spacer(Modifier.height(16.dp))

        PlaceholderItem(
            width = 268.dp,
            height = 240.dp,
        )
        Spacer(Modifier.height(16.dp))

        PlaceholderItem(endPadding = 128.dp)
        Spacer(Modifier.height(16.dp))

        PlaceholderItem()
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PlaceholderItem(
    height: Dp = 42.dp,
    width: Dp? = null,
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .padding(start = startPadding, end = endPadding)
                .then(
                    if (width != null) Modifier.width(width) else Modifier.fillMaxWidth(),
                ).height(height)
                .shimmer()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                ),
    )
}
