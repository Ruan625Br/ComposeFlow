package io.composeflow.ui.propertyeditor.variable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.search_only_target_type
import io.composeflow.search_only_target_type_description
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.switch.ComposeFlowSwitch
import io.composeflow.ui.textfield.SmallOutlinedTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchStateArea(
    params: SearchStatesParams,
    onSearchParamsUpdated: (SearchStatesParams) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp),
    ) {
        SmallOutlinedTextField(
            value = params.searchText,
            onValueChange = {
                onSearchParamsUpdated(params.copy(searchText = it))
            },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "",
                )
            },
            trailingIcon =
                if (params.searchText.isNotBlank()) {
                    {
                        ComposeFlowIconButton(onClick = {
                            onSearchParamsUpdated(
                                params.copy(searchText = ""),
                            )
                        }) {
                            ComposeFlowIcon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = "Clear search text",
                            )
                        }
                    }
                } else {
                    null
                },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp)
                    .focusRequester(focusRequester),
        )

        Tooltip(stringResource(Res.string.search_only_target_type_description)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .padding(8.dp)
                        .clickable {
                            onSearchParamsUpdated(
                                params.copy(
                                    searchOnlyAcceptableType = !params.searchOnlyAcceptableType,
                                ),
                            )
                        },
            ) {
                ComposeFlowSwitch(
                    checked = params.searchOnlyAcceptableType,
                    onCheckedChange = {
                        onSearchParamsUpdated(params.copy(searchOnlyAcceptableType = it))
                    },
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.search_only_target_type),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
