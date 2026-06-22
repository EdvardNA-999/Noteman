package com.example.util

enum class AppLanguage(val code: String, val isRtl: Boolean, val label: String) {
    PERSIAN("fa", true, "فارسی"),
    ENGLISH("en", false, "English")
}

object Locales {
    val fa = mapOf(
        "app_name" to "نوت‌من (Noteman)",
        "search_hint" to "جستجو در یادداشت‌ها...",
        "all" to "همه",
        "pinned" to "سنجاق‌شده",
        "recents" to "یادداشت‌های اخیر",
        "categories" to "دسته‌بندی‌ها",
        "settings" to "تنظیمات",
        "add_note" to "یادداشت جدید",
        "edit_note" to "ویرایش یادداشت",
        "save" to "ذخیره یادداشت",
        "delete" to "حذف",
        "cancel" to "لغو",
        "title" to "عنوان یادداشت",
        "content" to "چیزی بنویسید (از مارک‌داون پشتیبانی می‌شود)...",
        "tags" to "برچسب‌ها (جدا شده با کاما، مانند: کار، ایده)",
        "category" to "انتخاب دسته‌بندی",
        "developer" to "طراح و توسعه‌دهنده",
        "about_dev" to "درباره برنامه‌نویس",
        "about_desc" to "نوت‌من (Noteman) یک ابزار مدرن برای ثبت ایده‌ها و یادداشت‌هاست که بر پایه ابزارهای بومی اندروید مثل Jetpack Compose و Room با عشق توسط عماد توسعه داده شده است.",
        "language" to "زبان برنامه",
        "theme" to "پوسته تیره برنامه",
        "light" to "روشن",
        "dark" to "تیره",
        "empty_notes" to "هیچ یادداشتی پیدا نشد! یک یادداشت جدید بنویسید.",
        "none" to "بدون دسته‌بندی",
        "markdown_preview" to "پیش‌نمایش مارک‌داون",
        "total_notes" to "تعداد کل یادداشت‌ها",
        "add_category" to "افزودن دسته‌بندی",
        "category_name" to "نام دسته‌بندی جدید",
        "recent_desc" to "یادداشت‌های اخیراً مشاهده یا ذخیره شده شما در این بخش نشان داده می‌شوند.",
        "manage_categories" to "مدیریت دسته‌بندی‌ها",
        "tag_filter" to "فیلتر برچسب‌ها",
        "about_emad" to "طراح و توسعه‌دهنده: عماد",
        "status" to "وضعیت",
        "priority" to "اولویت",
        "todo" to "برای انجام",
        "in_progress" to "در حال انجام",
        "done" to "انجام شده",
        "low" to "کم",
        "medium" to "متوسط",
        "high" to "زیاد",
        "pin" to "سنجاق کردن",
        "pin_hint" to "سنجاق به صفحه اصلی"
    )

    val en = mapOf(
        "app_name" to "Noteman",
        "search_hint" to "Search notes...",
        "all" to "All",
        "pinned" to "Pinned",
        "recents" to "Recent Notes",
        "categories" to "Categories",
        "settings" to "Settings",
        "add_note" to "New Note",
        "edit_note" to "Edit Note",
        "save" to "Save Note",
        "delete" to "Delete",
        "cancel" to "Cancel",
        "title" to "Note Title",
        "content" to "Write something (Markdown supported)...",
        "tags" to "Tags (comma separated, e.g. work, idea)",
        "category" to "Select Category",
        "developer" to "Designer & Developer",
        "about_dev" to "About Developer",
        "about_desc" to "Noteman is a modern note-taking application built on modern Android standards like Jetpack Compose development & offline-first SQLite database (Room), designed and crafted by Emad.",
        "language" to "App Language",
        "theme" to "Dark Mode Theme",
        "light" to "Light",
        "dark" to "Dark",
        "empty_notes" to "No notes available yet! Create your first note.",
        "none" to "Uncategorized",
        "markdown_preview" to "Markdown Preview",
        "total_notes" to "Total Notes Count",
        "add_category" to "Add Category",
        "category_name" to "New Category Name",
        "recent_desc" to "The list below showcases your 10 most recently viewed or edited notes.",
        "manage_categories" to "Manage Categories",
        "tag_filter" to "Filter by Tags",
        "about_emad" to "Developer: Emad",
        "status" to "Status",
        "priority" to "Priority",
        "todo" to "To Do",
        "in_progress" to "In Progress",
        "done" to "Done",
        "low" to "Low",
        "medium" to "Medium",
        "high" to "High",
        "pin" to "Pin",
        "pin_hint" to "Pin to home screen"
    )

    fun getString(key: String, lang: AppLanguage): String {
        return if (lang == AppLanguage.PERSIAN) {
            fa[key] ?: en[key] ?: key
        } else {
            en[key] ?: fa[key] ?: key
        }
    }
}
