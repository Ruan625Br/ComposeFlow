package io.composeflow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.composeflow.ui.jewel.TitleBarContent
import io.composeflow.ui.modifier.hoverIconClickable
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.painter.hints.Size
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls

@Composable
fun DecoratedWindowScope.TitleBarView(
    onComposeFlowLogoClicked: () -> Unit,
    titleBarLeftContent: TitleBarContent,
    titleBarRightContent: TitleBarContent,
) {
    TitleBar(
        Modifier.newFullscreenControls(newControls = true),
        gradientStartColor = Color(0xFFB0C6FF),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            val painterProvider = rememberResourcePainterProvider(
                "ic_composeflow_logo.svg",
                Icons::class.java,
            )
            val painter by painterProvider.getPainter(
                Size(18),
            )
            Spacer(modifier = Modifier.width(88.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.background(
                    color = Color.White,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ).clickable {
                    onComposeFlowLogoClicked()
                }.hoverIconClickable()
            ) {
                Icon(
                    painter = painter, "icon",
                    modifier = Modifier.padding(4.dp)
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                titleBarLeftContent()
            }
            Text(title)
            Row(modifier = Modifier.weight(1f)) {
                titleBarRightContent()
            }
        }
    }
}
