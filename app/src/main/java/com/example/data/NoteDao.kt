package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    suspend fun getAllNotesRaw(): List<Note>

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, timestamp DESC")
    fun getAllNotesFlow(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY isPinned DESC, timestamp DESC")
    fun searchNotesFlow(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE categoryId = :categoryId ORDER BY isPinned DESC, timestamp DESC")
    fun getNotesByCategoryFlow(categoryId: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY lastViewedTimestamp DESC LIMIT 10")
    fun getRecentNotesFlow(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteByIdFlow(id: Int): Flow<Note?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    // Category DAO operations
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategoriesFlow(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?
}
