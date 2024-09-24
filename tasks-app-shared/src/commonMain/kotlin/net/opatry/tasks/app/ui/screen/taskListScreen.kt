/*
 * Copyright (c) 2024 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.opatry.tasks.app.ui.screen

import CheckCheck
import CircleOff
import LucideIcons
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.opatry.tasks.app.ui.component.EmptyState
import net.opatry.tasks.app.ui.component.TaskListDetail
import net.opatry.tasks.app.ui.component.TaskListsColumn
import net.opatry.tasks.app.ui.model.TaskListUIModel

@Composable
fun TaskListsMasterDetail(taskLists: List<TaskListUIModel>) {
    var currentTaskList by remember { mutableStateOf<TaskListUIModel?>(null) }

    Row(Modifier.fillMaxWidth()) {
        if (taskLists.isEmpty()) {
            EmptyState(
                icon = LucideIcons.CheckCheck,
                title = "No task list",
                description = "Create a new task list to get started",
            )
        } else {
            Box(Modifier.weight(.25f)) {
                TaskListsColumn(
                    taskLists,
                    selectedItem = currentTaskList,
                    onItemClick = { taskList ->
                        currentTaskList = taskList
                    }
                )
            }

            VerticalDivider()
        }

        Box(Modifier.weight(.75f)) {
            currentTaskList?.let { taskList ->
                TaskListDetail(
                    taskLists,
                    taskList,
                    onRenameTaskList = { _, _ -> },
                    onClearTaskListCompletedTasks = {},
                    onDeleteTaskList = {},
                )
            } ?: run {
                EmptyState(
                    icon = LucideIcons.CircleOff,
                    title = "No task list selected",
                    description = "Select a task list to see its tasks",
                )
            }
        }
    }
}