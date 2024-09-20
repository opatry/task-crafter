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

package net.opatry.tasks.data

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import kotlinx.coroutines.flow.Flow
import net.opatry.tasks.data.entity.TaskEntity
import net.opatry.tasks.data.entity.TaskListEntity

@Database(
    entities = [
        TaskListEntity::class,
        TaskEntity::class,
    ],
    version = 1
)
@ConstructedBy(TasksAppDatabaseConstructor::class)
abstract class TasksAppDatabase : RoomDatabase() {
    abstract fun getTaskListDao(): TaskListDao
    abstract fun getTaskDao(): TaskDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object TasksAppDatabaseConstructor : RoomDatabaseConstructor<TasksAppDatabase> {
    override fun initialize(): TasksAppDatabase
}

@Dao
interface TaskListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TaskListEntity): Long

    @Query("SELECT * FROM task_list WHERE local_id = :id")
    suspend fun getById(id: Long): TaskListEntity?

    @Query("SELECT * FROM task_list WHERE remote_id = :remoteId")
    suspend fun getByRemoteId(remoteId: String): TaskListEntity?

    @Query("SELECT count(*) FROM task_list")
    suspend fun count(): Int

    @Query("SELECT * FROM task_list")
    fun getAllAsFlow(): Flow<List<TaskListEntity>>
}

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TaskEntity): Long

    @Query("SELECT * FROM task WHERE remote_id = :remoteId")
    suspend fun getByRemoteId(remoteId: String): TaskEntity?

    @Query("SELECT count(*) FROM task")
    suspend fun count(): Int

    @Query("SELECT * FROM task")
    fun getAllAsFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE parent_list_local_id = :parentId")
    suspend fun getAllByParentId(parentId: Long): List<TaskEntity>

    @Query(
        """SELECT * FROM task
JOIN task_list ON task.parent_list_local_id = task_list.local_id"""
    )
    fun getAllTasksWithParent(): Flow<Map<TaskListEntity, List<TaskEntity>>>
}
