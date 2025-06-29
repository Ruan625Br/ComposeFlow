package io.composeflow.model.datatype

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ParseDataTypeJsonTextField(
    onJsonParsed: (DataTypeParseResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val jsonParser = JsonParser()
    ParseJsonTextField(
        onParseJson = {
            val parseResult = jsonParser.parseJsonToDataType(it)
            onJsonParsed(parseResult)
            parseResult
        },
        modifier = modifier,
    )
}

@Composable
fun ParseDefaultValuesJsonTextField(
    dataType: DataType,
    onJsonParsed: (DefaultValuesParseResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val jsonParser = JsonParser()
    ParseJsonTextField(
        onParseJson = {
            val parseResult =
                jsonParser.parseJsonToDefaultValues(dataType = dataType, jsonText = it)
            onJsonParsed(parseResult)
            parseResult
        },
        modifier = modifier,
    )
}

@Composable
private fun ParseJsonTextField(
    onParseJson: (String) -> JsonParseResult,
    modifier: Modifier = Modifier,
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                annotatedString =
                    buildAnnotatedString {
                        append("\n".repeat(4))
                    },
            ),
        )
    }
    val lineNumbers = remember { mutableStateListOf<Int>() }
    val coroutineScope = rememberCoroutineScope()
    val verticalScrollState = rememberScrollState()
    var hasFocus by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(true) }
    val borderColor by animateColorAsState(
        targetValue =
            if (hasError) {
                MaterialTheme.colorScheme.error
            } else {
                if (hasFocus) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                }
            },
        animationSpec = tween(durationMillis = 400),
    )

    LaunchedEffect(textFieldValue.text) {
        lineNumbers.clear()
        val lines = textFieldValue.text.lines().size
        for (i in 1..lines) {
            lineNumbers.add(i)
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxHeight(),
    ) {
        Row(
            modifier =
                Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.small,
                    ).border(
                        width = 1.dp,
                        color = borderColor,
                        shape = MaterialTheme.shapes.small,
                    ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(4.dp)
                        .verticalScroll(verticalScrollState),
            ) {
                lineNumbers.forEach { lineNumber ->
                    Text(
                        text = lineNumber.toString(),
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                    )
                }
            }
            val textStyle =
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                )
            Column(
                modifier = Modifier.padding(8.dp).fillMaxSize().verticalScroll(verticalScrollState),
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        val lineCount =
                            textFieldValue.text
                                .substring(0, newValue.selection.end)
                                .count { it == '\n' }
                        coroutineScope.launch {
                            verticalScrollState.scrollTo(lineCount * textStyle.lineHeight.value.toInt())
                        }
                        val parseResult = onParseJson(textFieldValue.text)
                        hasError = !parseResult.isSuccess()
                    },
                    textStyle = textStyle,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .horizontalScroll(rememberScrollState())
                            .onFocusChanged { focusState ->
                                hasFocus = focusState.isFocused
                            },
                )
            }
        }
    }
}
