package io.composeflow.model.useroperation

import io.composeflow.model.project.Project
import io.composeflow.model.project.serialize
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object OperationHistory {
    private const val operationHistoryCapacity = 50

    private val undoStack: ArrayDeque<UndoableOperation> by dequeLimiter(operationHistoryCapacity)
    private val redoStack: ArrayDeque<UndoableOperation> by dequeLimiter(operationHistoryCapacity)

    fun record(
        project: Project,
        userOperation: UserOperation,
    ) {
        undoStack.add(
            UndoableOperation(
                serializedProject = project.serialize(),
                userOperation = userOperation,
            ),
        )
    }

    fun undo(project: Project): UndoableOperation? =
        if (undoStack.isNotEmpty()) {
            val undoTop = undoStack.removeLast()
            redoStack.add(
                UndoableOperation(
                    serializedProject = project.serialize(),
                    userOperation = UserOperation.Undo,
                ),
            )
            undoTop
        } else {
            null
        }

    fun redo(project: Project): UndoableOperation? =
        if (redoStack.isNotEmpty()) {
            val redoTop = redoStack.removeLast()
            undoStack.add(
                UndoableOperation(
                    serializedProject = project.serialize(),
                    userOperation = UserOperation.Redo,
                ),
            )
            redoTop
        } else {
            null
        }
}

fun <E> dequeLimiter(limit: Int): ReadWriteProperty<Any?, ArrayDeque<E>> =
    object : ReadWriteProperty<Any?, ArrayDeque<E>> {
        private var deque: ArrayDeque<E> = ArrayDeque(limit)

        private fun applyLimit() {
            while (deque.size > limit) {
                deque.removeFirst()
            }
        }

        override fun getValue(
            thisRef: Any?,
            property: KProperty<*>,
        ): ArrayDeque<E> {
            applyLimit()
            return deque
        }

        override fun setValue(
            thisRef: Any?,
            property: KProperty<*>,
            value: ArrayDeque<E>,
        ) {
            this.deque = value
            applyLimit()
        }
    }
