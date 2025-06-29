package io.composeflow.ui.uibuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun PlaceholderScreen(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        ) {
            Spacer(Modifier.size(8.dp))
            PlaceholderBox(
                modifier =
                    Modifier
                        .width(240.dp)
                        .height(48.dp),
            )
            Spacer(Modifier.size(8.dp))
        }
        Spacer(Modifier.size(16.dp))
        PlaceholderBox(modifier = Modifier.fillMaxWidth().height(48.dp))
        Spacer(Modifier.size(16.dp))
        PlaceholderBox(modifier = Modifier.fillMaxWidth().height(48.dp))
        Spacer(Modifier.size(16.dp))
        PlaceholderBox(Modifier.size(120.dp))
        Spacer(Modifier.size(16.dp))
        PlaceholderBox(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 64.dp)
                    .height(48.dp),
        )
        Spacer(Modifier.size(16.dp))
        PlaceholderBox(
            modifier =
                Modifier
                    .width(180.dp)
                    .padding(end = 64.dp)
                    .height(48.dp),
        )
    }
}

@Composable
fun PlaceholderComponent(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PlaceholderBox(modifier = Modifier.fillMaxWidth().height(48.dp))
        Spacer(Modifier.size(16.dp))
        PlaceholderBox(modifier = Modifier.fillMaxWidth().height(48.dp))
        Spacer(Modifier.size(16.dp))
    }
}

@Composable
private fun PlaceholderBox(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .shimmer()
                .background(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp),
                ),
    ) {}
}
