package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["title"]),
        Index(value = ["content"]),
        Index(value = ["categoryId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val lastViewedTimestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val tags: String = "", // Comma-separated, e.g. "tag1,tag2"
    val categoryId: Int? = null,
    val status: String = "todo", // "todo", "in_progress", "done"
    val priority: String = "medium" // "low", "medium", "high"
)
