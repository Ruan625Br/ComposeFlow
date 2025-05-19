import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.DesktopMac
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.TabletAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.createDefault
import io.composeflow.AppInitializer
import io.composeflow.common.LocalUseDarkTheme
import io.composeflow.di.initKoin
import io.composeflow.platform.ProvideDeviceSizeDp
import io.composeflow.preview.DotPatternBackground
import moe.tlaster.precompose.preComposeWindow
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    initKoin()
    AppInitializer.onApplicationStart()

    onWasmReady {
        preComposeWindow(title = "App") {
            DotPatternBackground(
                dotRadius = 1.dp,
                dotMargin = 12.dp,
                dotColor = Color.Black,
                modifier = Modifier.fillMaxSize()
                    .background(color = Color(0xFF646258)),
            ) {
                val isSystemInDarkTheme = isSystemInDarkTheme()
                var useDarkTheme by remember { mutableStateOf(isSystemInDarkTheme) }
                CompositionLocalProvider(LocalUseDarkTheme provides useDarkTheme) {
                    Box {
                        var formFactor by remember { mutableStateOf<FormFactor>(FormFactor.Phone()) }
                        var deviceSizeDp by remember { mutableStateOf(IntSize.Zero) }
                        val density = LocalDensity.current

                        DeviceFormFactorCard(
                            onFormFactorChanged = {
                                formFactor = it
                            },
                            currentFormFactor = formFactor,
                            modifier = Modifier.align(Alignment.TopCenter)
                                .padding(top = 24.dp)
                        )

                        Card(
                            modifier = Modifier.align(Alignment.TopCenter)
                                .padding(top = 24.dp)
                                .offset(x = 160.dp)
                                .height(34.dp)
                                .hoverIconClickable(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .clickable {
                                        useDarkTheme = !useDarkTheme
                                    }
                                    .padding(8.dp)
                                    .size(24.dp),
                                imageVector = if (useDarkTheme) {
                                    Icons.Default.ModeNight
                                } else {
                                    Icons.Default.WbSunny
                                },
                                contentDescription = "",
                            )
                        }

                        CompositionLocalProvider(LocalImageLoader provides ImageLoader.createDefault()) {
                            ProvideDeviceSizeDp(deviceSizeDp) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier.align(Alignment.Center)
                                            .padding(horizontal = 48.dp, vertical = 24.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.size(
                                                formFactor.deviceSize.width.dp,
                                                formFactor.deviceSize.height.dp
                                            )
                                                .wrapContentSize()
                                                .clip(RoundedCornerShape(16.dp))
                                                .onGloballyPositioned {
                                                    deviceSizeDp = it.size / density.density.toInt()
                                                }
                                        ) {
                                            App()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed interface FormFactor {
    val deviceSize: IntSize // Device size in device pixels

    data class Phone(
        override val deviceSize: IntSize = IntSize(416, 886),
    ) : FormFactor

    data class Tablet(
        override val deviceSize: IntSize = IntSize(729, 972), // iPad
    ) : FormFactor

    data class Desktop(
        override val deviceSize: IntSize = IntSize(1440, 900), // Desktop
    ) : FormFactor
}


@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.hoverIconClickable(): Modifier = composed {
    var isHovered by remember { mutableStateOf(false) }

    onPointerEvent(PointerEventType.Enter) {
        isHovered = true
    }.onPointerEvent(PointerEventType.Exit) {
        isHovered = false
    }.then(
        if (isHovered) {
            pointerHoverIcon(PointerIcon.Hand)
        } else {
            this
        },
    )
}

@Composable
private fun DeviceFormFactorCard(
    onFormFactorChanged: (FormFactor) -> Unit,
    currentFormFactor: FormFactor,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(34.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            fun formFactorSelectedModifier(selected: Boolean): Modifier {
                return if (selected) {
                    Modifier
                } else {
                    Modifier.alpha(0.3f)
                }
            }
            Icon(
                imageVector = Icons.Outlined.Smartphone,
                contentDescription = null,
                modifier = Modifier.padding(6.dp).size(26.dp)
                    .hoverIconClickable()
                    .clickable {
                        onFormFactorChanged(FormFactor.Phone())
                    }
                    .then(formFactorSelectedModifier(currentFormFactor is FormFactor.Phone))
            )

            Icon(
                imageVector = Icons.Outlined.TabletAndroid,
                contentDescription = null,
                modifier = Modifier.padding(6.dp).size(26.dp)
                    .hoverIconClickable()
                    .clickable {
                        onFormFactorChanged(FormFactor.Tablet())
                    }
                    .then(formFactorSelectedModifier(currentFormFactor is FormFactor.Tablet))
            )

            Icon(
                imageVector = Icons.Outlined.DesktopMac,
                contentDescription = null,
                modifier = Modifier.padding(8.dp).size(26.dp)
                    .hoverIconClickable()
                    .clickable {
                        onFormFactorChanged(FormFactor.Desktop())
                    }
                    .then(formFactorSelectedModifier(currentFormFactor is FormFactor.Desktop))
            )
        }
    }
}