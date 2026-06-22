package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = NoteRepository(database.noteDao)
    }

    // Language setting, default is PERSIAN
    private val _language = MutableStateFlow(AppLanguage.PERSIAN)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Dark Mode preference, default to null (System) or Boolean (true/false)
    private val _darkMode = MutableStateFlow<Boolean?>(null)
    val darkMode: StateFlow<Boolean?> = _darkMode.asStateFlow()

    // Search query StateFlow
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Category filter StateFlow
    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    // Tag filter StateFlow
    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    // Status filter StateFlow
    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    // Priority filter StateFlow
    private val _selectedPriority = MutableStateFlow<String?>(null)
    val selectedPriority: StateFlow<String?> = _selectedPriority.asStateFlow()

    // Reactive Categories
    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All tags collected dynamically from notes
    val allTags: StateFlow<List<String>> = repository.allNotes
        .map { notes ->
            notes.flatMap { note ->
                note.tags.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            }.distinct()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Raw Notes
    private val rawNotes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered / Searched notes
    val filteredNotes: StateFlow<List<Note>> = combine(
        rawNotes,
        _searchQuery,
        _selectedCategoryId,
        _selectedTag,
        _selectedStatus,
        _selectedPriority
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val notes = flows[0] as List<Note>
        val query = flows[1] as String
        val catId = flows[2] as Int?
        val tag = flows[3] as String?
        val stat = flows[4] as String?
        val prio = flows[5] as String?

        var list = notes
        if (query.isNotEmpty()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true) ||
                        it.tags.contains(query, ignoreCase = true)
            }
        }
        if (catId != null) {
            list = list.filter { it.categoryId == catId }
        }
        if (tag != null) {
            list = list.filter {
                it.tags.split(",").map { t -> t.trim() }.contains(tag)
            }
        }
        if (stat != null) {
            list = list.filter { it.status == stat }
        }
        if (prio != null) {
            list = list.filter { it.priority == prio }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recent Notes sorted by last accessed / modified
    val recentNotes: StateFlow<List<Note>> = repository.recentNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Preference setters
    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setDarkMode(dark: Boolean?) {
        _darkMode.value = dark
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun selectTag(tag: String?) {
        _selectedTag.value = tag
    }

    fun selectStatus(status: String?) {
        _selectedStatus.value = status
    }

    fun selectPriority(priority: String?) {
        _selectedPriority.value = priority
    }

    // CRUD notes
    fun insertNote(note: Note, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertNote(note)
            onComplete(id)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteNote(note)
            onComplete()
        }
    }

    fun getNoteById(id: Int): Flow<Note?> {
        return repository.getNoteById(id)
    }

    fun markNoteAsViewed(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(lastViewedTimestamp = System.currentTimeMillis()))
        }
    }

    // CRUD categories
    fun insertCategory(name: String) {
        viewModelScope.launch {
            if (name.trim().isNotEmpty()) {
                repository.insertCategory(Category(name = name.trim()))
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}
