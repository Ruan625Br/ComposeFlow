package io.composeflow.ui.inspector.action

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.connect_with_firebase_to_proceed_go_to_settings
import io.composeflow.model.action.Action
import io.composeflow.model.action.DataFieldUpdateProperty
import io.composeflow.model.action.FirestoreAction
import io.composeflow.model.action.Share
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.findDataTypeOrNull
import io.composeflow.model.project.findFirestoreCollectionOrNull
import io.composeflow.ui.Tooltip
import io.composeflow.ui.inspector.action.datatype.WhereExpressionEditor
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.utils.TreeExpanderInverse
import org.jetbrains.compose.resources.stringResource

@Composable
fun FirestoreContent(
    project: Project,
    actionInEdit: Action?,
    onActionSelected: (Action) -> Unit,
) {
    val firebaseConnected = project.firebaseAppInfoHolder.firebaseAppInfo.firebaseProjectId != null

    var firestoreActionsOpened by remember { mutableStateOf(firebaseConnected) }
    val connectWithFirebaseToProceed =
        stringResource(Res.string.connect_with_firebase_to_proceed_go_to_settings)
    Column(modifier = Modifier.animateContentSize(keyframes { durationMillis = 100 })) {
        if (firebaseConnected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    firestoreActionsOpened = !firestoreActionsOpened
                },
            ) {
                Text(
                    text = "Firebase Firestore",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
                Spacer(Modifier.weight(1f))
                TreeExpanderInverse(
                    expanded = firestoreActionsOpened,
                    onClick = {
                        firestoreActionsOpened = !firestoreActionsOpened
                    },
                )
            }
        } else {
            Tooltip(connectWithFirebaseToProceed) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Firestore",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                            .alpha(0.5f),
                    )
                    Spacer(Modifier.weight(1f))
                    TreeExpanderInverse(
                        expanded = firestoreActionsOpened,
                        onClick = {
                            firestoreActionsOpened = !firestoreActionsOpened
                        },
                        enabled = false
                    )
                }
            }
        }
        if (firestoreActionsOpened) {
            FirestoreAction.entries().forEach { firestoreAction ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .hoverIconClickable()
                        .hoverOverlay()
                        .padding(vertical = 4.dp)
                        .padding(start = 8.dp)
                        .clickable {
                            onActionSelected(
                                firestoreAction
                            )
                        }
                        .selectedActionModifier(
                            actionInEdit = actionInEdit,
                            predicate = {
                                actionInEdit != null &&
                                        actionInEdit is Share &&
                                        actionInEdit.name == firestoreAction.name
                            },
                        ),
                ) {
                    Text(
                        text = firestoreAction.name,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun SaveToFirestoreContentDetail(
    project: Project,
    initialAction: FirestoreAction.SaveToFirestore,
    composeNode: ComposeNode,
    onEditAction: (Action) -> Unit,
) {
    Column {
        val firestoreCollections =
            project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections
        BasicDropdownPropertyEditor(
            items = firestoreCollections,
            onValueChanged = { i, item ->
                val firestoreCollection = project.findFirestoreCollectionOrNull(item.id)
                val dataType =
                    firestoreCollection?.dataTypeId?.let { project.findDataTypeOrNull(it) }

                val updatedAction = initialAction.copy(
                    collectionId = item.id,
                    dataFieldUpdateProperties = dataType?.fields?.map {
                        DataFieldUpdateProperty(
                            dataFieldId = it.id,
                            assignableProperty = it.fieldType.type().defaultValue(),
                        )
                    }?.toMutableList() ?: mutableListOf()
                )
                onEditAction(updatedAction)
            },
            selectedItem = initialAction.collectionId?.let {
                project.findFirestoreCollectionOrNull(
                    it
                )
            },
            label = "Collection"
        )

        val firestoreCollection =
            initialAction.collectionId?.let { project.findFirestoreCollectionOrNull(it) }
        val dataType = firestoreCollection?.dataTypeId?.let { project.findDataTypeOrNull(it) }

        dataType?.let {
            EditUpdatePropertiesForDataType(
                project = project,
                node = composeNode,
                dataFieldUpdateProperties = initialAction.dataFieldUpdateProperties,
                dataType = dataType,
                onDataFieldUpdatePropertiesUpdated = {
                    onEditAction(
                        initialAction.copy(dataFieldUpdateProperties = it.toMutableList())
                    )
                }
            )
        }
    }
}

@Composable
fun UpdateFirestoreDocumentContentDetail(
    project: Project,
    initialAction: FirestoreAction.UpdateDocument,
    composeNode: ComposeNode,
    onEditAction: (Action) -> Unit,
) {
    Column {
        val firestoreCollections =
            project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections
        BasicDropdownPropertyEditor(
            items = firestoreCollections,
            onValueChanged = { i, item ->
                val updatedAction = initialAction.copy(
                    collectionId = item.id,
                )
                onEditAction(updatedAction)
            },
            selectedItem = initialAction.collectionId?.let {
                project.findFirestoreCollectionOrNull(
                    it
                )
            },
            label = "Collection"
        )

        val firestoreCollection =
            initialAction.collectionId?.let { project.findFirestoreCollectionOrNull(it) }
        val dataType = firestoreCollection?.dataTypeId?.let { project.findDataTypeOrNull(it) }

        dataType?.let {
            EditUpdatePropertiesForDataType(
                project = project,
                node = composeNode,
                dataFieldUpdateProperties = initialAction.dataFieldUpdateProperties,
                dataType = dataType,
                onDataFieldUpdatePropertiesUpdated = {
                    onEditAction(
                        initialAction.copy(dataFieldUpdateProperties = it.toMutableList())
                    )
                }
            )

            WhereExpressionEditor(
                project = project,
                composeNode = composeNode,
                firestoreCollection = firestoreCollection,
                dataType = dataType,
                filterExpression = initialAction.filterExpression,
                onFilterExpressionUpdated = {
                    onEditAction(initialAction.copy(filterExpression = it))
                }
            )
        }
    }
}

@Composable
fun DeleteFirestoreDocumentContentDetail(
    project: Project,
    initialAction: FirestoreAction.DeleteDocument,
    composeNode: ComposeNode,
    onEditAction: (Action) -> Unit,
) {
    Column {
        val firestoreCollections =
            project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections
        BasicDropdownPropertyEditor(
            items = firestoreCollections,
            onValueChanged = { i, item ->
                val updatedAction = initialAction.copy(
                    collectionId = item.id,
                )
                onEditAction(updatedAction)
            },
            selectedItem = initialAction.collectionId?.let {
                project.findFirestoreCollectionOrNull(
                    it
                )
            },
            label = "Collection"
        )

        val firestoreCollection =
            initialAction.collectionId?.let { project.findFirestoreCollectionOrNull(it) }
        val dataType = firestoreCollection?.dataTypeId?.let { project.findDataTypeOrNull(it) }
        dataType?.let {
            WhereExpressionEditor(
                project = project,
                composeNode = composeNode,
                firestoreCollection = firestoreCollection,
                dataType = dataType,
                filterExpression = initialAction.filterExpression,
                onFilterExpressionUpdated = {
                    onEditAction(initialAction.copy(filterExpression = it))
                }
            )
        }
    }
}
