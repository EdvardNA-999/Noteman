package com.example.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotesFlow()
    val allCategories: Flow<List<Category>> = noteDao.getAllCategoriesFlow()
    val recentNotes: Flow<List<Note>> = noteDao.getRecentNotesFlow()

    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotesFlow(query)
    fun getNotesByCategory(categoryId: Int): Flow<List<Note>> = noteDao.getNotesByCategoryFlow(categoryId)
    fun getNoteById(id: Int): Flow<Note?> = noteDao.getNoteByIdFlow(id)

    suspend fun getNoteByIdDirect(id: Int): Note? = noteDao.getNoteById(id)
    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun insertCategory(category: Category): Long = noteDao.insertCategory(category)
    suspend fun deleteCategory(category: Category) = noteDao.deleteCategory(category)
}
