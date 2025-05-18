package com.example.jetpack_compose_assignment_2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Todo>)

    @Query("SELECT * FROM Todo")
    suspend fun getAllTodo(): List<Todo>
}