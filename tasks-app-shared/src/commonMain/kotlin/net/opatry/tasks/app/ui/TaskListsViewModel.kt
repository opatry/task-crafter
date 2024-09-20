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

package net.opatry.tasks.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import net.opatry.tasks.app.ui.model.TaskListUIModel
import net.opatry.tasks.app.ui.model.TaskUIModel
import net.opatry.tasks.data.TaskRepository
import net.opatry.tasks.data.model.TaskDataModel
import net.opatry.tasks.data.model.TaskListDataModel

private fun TaskListDataModel.asTaskListUIModel(): TaskListUIModel {
    // TODO children
    // TODO date formatter
    return TaskListUIModel(
        id = id,
        title = title,
        lastUpdate = lastUpdate.toString(),
        tasks = tasks.map(TaskDataModel::asTaskUIModel)
    )
}

private fun TaskDataModel.asTaskUIModel(): TaskUIModel {
    // TODO date formatter
    return TaskUIModel(
        title = title,
        dueDate = dueDate?.toString() ?: "",
        isCompleted = false
    )
}

class TaskListsViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _taskLists = MutableStateFlow<List<TaskListUIModel>>(emptyList())

    // TODO UI model
    val taskLists: StateFlow<List<TaskListUIModel>>
        get() = _taskLists

    init {
        // cold flow?
        viewModelScope.launch {
            taskRepository.fetchTaskLists()
            taskRepository.getTaskLists().collect { allLists ->
                _taskLists.value = allLists.map(TaskListDataModel::asTaskListUIModel)
            }
        }
    }

    fun createTaskList(title: String) {
        viewModelScope.launch {
            try {
                taskRepository.createTaskList(title)
            } catch (e: Exception) {
                println("Error creating task list: $e")
                // TODO error handling
            }
        }
    }

    // TODO when "delete task" (or list) is done, manage a "Undo" snackbar
    //  - either apply it remotely and on undo, do another request to restore through API
    //  - or "hide" locally, on undo "un-hide", on dismiss, apply remotely

    fun createTask(taskList: TaskListUIModel, title: String, dueDate: Instant? = null) {
        viewModelScope.launch {
            try {
                taskRepository.createTask(taskList.id, title, dueDate)
            } catch (e: Exception) {
                println("Error creating task: $e")
                // TODO error handling
            }
        }
    }

    fun fetch() {
        viewModelScope.launch {
            refresh()
        }
    }

    private suspend fun refresh() {
        taskRepository.fetchTaskLists()
    }
}
