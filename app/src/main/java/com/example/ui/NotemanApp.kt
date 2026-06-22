package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.outlined.PushPin
import androidx.navigation.navArgument
import com.example.data.Category
import com.example.data.Note
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import com.example.util.JalaliDateHelper
import com.example.util.Locales
import com.example.util.MarkdownText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun NotemanApp(viewModel: NoteViewModel) {
    val navController = rememberNavController()
    val currentLanguage by viewModel.language.collectAsStateWithLifecycle()
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val darkModePreference by viewModel.darkMode.collectAsStateWithLifecycle()
    
    val useDarkMode = when (darkModePreference) {
        true -> true
        false -> false
        null -> isSystemDark
    }

    MyApplicationTheme(darkTheme = useDarkMode) {
        val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavigationHost(
                    navController = navController,
                    viewModel = viewModel,
                    language = currentLanguage
                )
            }
        }
    }
}

@Composable
fun AppNavigationHost(
    navController: NavHostController,
    viewModel: NoteViewModel,
    language: AppLanguage
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel,
                language = language
            )
        }
        
        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            NoteEditorScreen(
                navController = navController,
                viewModel = viewModel,
                language = language,
                noteId = noteId
            )
        }

        composable(
            route = "detail/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
            NoteDetailScreen(
                navController = navController,
                viewModel = viewModel,
                language = language,
                noteId = noteId
            )
        }

        composable("categories") {
            CategoriesScreen(
                navController = navController,
                viewModel = viewModel,
                language = language
            )
        }

        composable("recents") {
            RecentNotesScreen(
                navController = navController,
                viewModel = viewModel,
                language = language
            )
        }

        composable("settings") {
            SettingsScreen(
                navController = navController,
                viewModel = viewModel,
                language = language
            )
        }
    }
}

// Global Bottom Navigation Bar
@Composable
fun NotemanBottomNavigation(
    navController: NavHostController,
    language: AppLanguage
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text(Locales.getString("all", language)) }
        )

        NavigationBarItem(
            selected = currentRoute == "recents",
            onClick = {
                if (currentRoute != "recents") {
                    navController.navigate("recents") {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.History, contentDescription = "Recents") },
            label = { Text(Locales.getString("recents", language)) }
        )

        NavigationBarItem(
            selected = currentRoute == "categories",
            onClick = {
                if (currentRoute != "categories") {
                    navController.navigate("categories") {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
            label = { Text(Locales.getString("categories", language)) }
        )

        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = {
                if (currentRoute != "settings") {
                    navController.navigate("settings") {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text(Locales.getString("settings", language)) }
        )
    }
}


// --- 1. HOME SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: NoteViewModel,
    language: AppLanguage
) {
    val notes by viewModel.filteredNotes.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCatId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    val selectedPriority by viewModel.selectedPriority.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Locales.getString("app_name", language),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NotemanBottomNavigation(navController = navController, language = language)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("editor/-1") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = Locales.getString("add_note", language))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Search Input Row
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(Locales.getString("search_hint", language), style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                singleLine = true,
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Categories horizontal slider
            Text(
                text = Locales.getString("categories", language),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isSelected = selectedCatId == null
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(null) },
                        label = { Text(Locales.getString("all", language)) }
                    )
                }
                
                items(categories) { category ->
                    val isSelected = selectedCatId == category.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(category.id) },
                        label = { Text(category.name) }
                    )
                }
            }

            // Tags horizontal selector (only show if tags exist)
            if (allTags.isNotEmpty()) {
                Text(
                    text = Locales.getString("tag_filter", language),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedTag == null,
                            onClick = { viewModel.selectTag(null) },
                            label = { Text(Locales.getString("all", language)) }
                        )
                    }
                    items(allTags) { tag ->
                        FilterChip(
                            selected = selectedTag == tag,
                            onClick = { viewModel.selectTag(tag) },
                            label = { Text("#$tag") }
                        )
                    }
                }
            }

            // Status filter row
            Text(
                text = Locales.getString("status", language),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { viewModel.selectStatus(null) },
                        label = { Text(Locales.getString("all", language)) }
                    )
                }
                listOf("todo", "in_progress", "done").forEach { s ->
                    item {
                        FilterChip(
                            selected = selectedStatus == s,
                            onClick = { viewModel.selectStatus(s) },
                            label = { Text(Locales.getString(s, language)) }
                        )
                    }
                }
            }

            // Priority filter row
            Text(
                text = Locales.getString("priority", language),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedPriority == null,
                        onClick = { viewModel.selectPriority(null) },
                        label = { Text(Locales.getString("all", language)) }
                    )
                }
                listOf("low", "medium", "high").forEach { p ->
                    item {
                        FilterChip(
                            selected = selectedPriority == p,
                            onClick = { viewModel.selectPriority(p) },
                            label = { Text(Locales.getString(p, language)) }
                        )
                    }
                }
            }

            // Notes representation block
            if (notes.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.StickyNote2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(96.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = Locales.getString("empty_notes", language),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(notes) { note ->
                        NoteCard(
                            note = note,
                            language = language,
                            categories = categories,
                            onClick = {
                                viewModel.markNoteAsViewed(note)
                                navController.navigate("detail/${note.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    language: AppLanguage,
    categories: List<Category>,
    onClick: () -> Unit
) {
    val categoryName = categories.firstOrNull { it.id == note.categoryId }?.name ?: Locales.getString("none", language)
    val jalaliDate = JalaliDateHelper.getJalaliDateFromTimestamp(note.timestamp, language == AppLanguage.PERSIAN)

    val cardColor = if (note.isPinned) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category Tag Pill
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )

                // Status Pill
                val statusBg = when (note.status) {
                    "done" -> Color(0xFFE8F5E9)      // Light Green
                    "in_progress" -> Color(0xFFE3F2FD) // Light Blue
                    else -> Color(0xFFFFF3E0)          // Light Amber ("todo")
                }
                val statusTextCol = when (note.status) {
                    "done" -> Color(0xFF2E7D32)
                    "in_progress" -> Color(0xFF1565C0)
                    else -> Color(0xFFE65100)
                }
                Text(
                    text = Locales.getString(note.status, language),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusTextCol,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(statusBg, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )

                // Priority Pill
                val prioBg = when (note.priority) {
                    "high" -> Color(0xFFFFEBEE)   // Soft Red
                    "medium" -> Color(0xFFFFFDE7) // Soft Yellow
                    else -> Color(0xFFF5F5F5)     // Soft Grey ("low")
                }
                val prioTextCol = when (note.priority) {
                    "high" -> Color(0xFFC62828)
                    "medium" -> Color(0xFFF57F17)
                    else -> Color(0xFF616161)
                }
                Text(
                    text = Locales.getString(note.priority, language),
                    style = MaterialTheme.typography.labelSmall,
                    color = prioTextCol,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(prioBg, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                if (note.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = note.title.ifEmpty { "..." },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Raw Snippet text
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Jalali Shamsi Date String
            Text(
                text = jalaliDate,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


// --- 2. NOTE EDITOR SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    navController: NavHostController,
    viewModel: NoteViewModel,
    language: AppLanguage,
    noteId: Int
) {
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<Int?>(null) }
    var isPinned by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("todo") }
    var priority by remember { mutableStateOf("medium") }
    
    // Simple markdown preview switch
    var isMarkdownPreview by remember { mutableStateOf(false) }
    
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    LaunchedEffect(noteId) {
        if (noteId != -1) {
            val note = viewModel.getNoteById(noteId).first()
            if (note != null) {
                title = note.title
                content = note.content
                tags = note.tags
                categoryId = note.categoryId
                isPinned = note.isPinned
                status = note.status
                priority = note.priority
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (noteId == -1) Locales.getString("add_note", language) else Locales.getString("edit_note", language),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Locales.getString("cancel", language))
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isPinned = !isPinned }
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = {
                            val newNote = Note(
                                id = if (noteId == -1) 0 else noteId,
                                title = title,
                                content = content,
                                timestamp = System.currentTimeMillis(),
                                lastViewedTimestamp = System.currentTimeMillis(),
                                isPinned = isPinned,
                                tags = tags,
                                categoryId = categoryId,
                                status = status,
                                priority = priority
                            )
                            if (noteId == -1) {
                                viewModel.insertNote(newNote) {
                                    coroutineScope.launch {
                                        navController.popBackStack()
                                    }
                                }
                            } else {
                                viewModel.updateNote(newNote)
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = Locales.getString("save", language),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(Locales.getString("title", language)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Category selector dropdown
            var dropdownExpanded by remember { mutableStateOf(false) }
            val currentCategoryName = categories.firstOrNull { it.id == categoryId }?.name ?: Locales.getString("none", language)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                OutlinedButton(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "${Locales.getString("category", language)}: $currentCategoryName")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    DropdownMenuItem(
                        text = { Text(Locales.getString("none", language)) },
                        onClick = {
                            categoryId = null
                            dropdownExpanded = false
                        }
                    )
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                categoryId = cat.id
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Status and Priority Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var statusExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { statusExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${Locales.getString("status", language)}: ${Locales.getString(status, language)}",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        listOf("todo", "in_progress", "done").forEach { s ->
                            DropdownMenuItem(
                                text = { Text(Locales.getString(s, language)) },
                                onClick = {
                                    status = s
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                var priorityExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { priorityExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${Locales.getString("priority", language)}: ${Locales.getString(priority, language)}",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        listOf("low", "medium", "high").forEach { p ->
                            DropdownMenuItem(
                                text = { Text(Locales.getString(p, language)) },
                                onClick = {
                                    priority = p
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Tags input
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text(Locales.getString("tags", language)) },
                singleLine = true,
                placeholder = { Text("e.g. work, general") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Markdown Toggle row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = Locales.getString("markdown_preview", language),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Switch(
                    checked = isMarkdownPreview,
                    onCheckedChange = { isMarkdownPreview = it }
                )
            }

            // Content editor or Markdown preview
            if (isMarkdownPreview) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        item {
                            MarkdownText(
                                text = if (content.isEmpty()) "*Empty Content*" else content,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text(Locales.getString("content", language)) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    maxLines = 100
                )
            }
        }
    }
}


// --- 3. NOTE DETAIL SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    navController: NavHostController,
    viewModel: NoteViewModel,
    language: AppLanguage,
    noteId: Int
) {
    val noteState = viewModel.getNoteById(noteId).collectAsStateWithLifecycle(initialValue = null)
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val note = noteState.value

    if (showDeleteDialog && note != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(Locales.getString("delete", language)) },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNote(note) {
                            showDeleteDialog = false
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text(Locales.getString("delete", language), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(Locales.getString("cancel", language))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Locales.getString("app_name", language)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (note != null) {
                        // Toggle PIN
                        IconButton(
                            onClick = {
                                viewModel.updateNote(note.copy(isPinned = !note.isPinned))
                            }
                        ) {
                            Icon(
                                imageVector = if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                contentDescription = "Toggle Pin",
                                tint = if (note.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Edit Note Button
                        IconButton(onClick = { navController.navigate("editor/${note.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Note")
                        }

                        // Delete Note Button
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (note == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            val categoryName = categories.firstOrNull { it.id == note.categoryId }?.name ?: Locales.getString("none", language)
            val shamsiDate = JalaliDateHelper.getJalaliDateFromTimestamp(note.timestamp, language == AppLanguage.PERSIAN)

            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                item {
                    // Header information
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        // Status Pill
                        val statusBg = when (note.status) {
                            "done" -> Color(0xFFE8F5E9)      // Light Green
                            "in_progress" -> Color(0xFFE3F2FD) // Light Blue
                            else -> Color(0xFFFFF3E0)          // Light Amber ("todo")
                        }
                        val statusTextCol = when (note.status) {
                            "done" -> Color(0xFF2E7D32)
                            "in_progress" -> Color(0xFF1565C0)
                            else -> Color(0xFFE65100)
                        }
                        Text(
                            text = Locales.getString(note.status, language),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusTextCol,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .background(statusBg, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        // Priority Pill
                        val prioBg = when (note.priority) {
                            "high" -> Color(0xFFFFEBEE)   // Soft Red
                            "medium" -> Color(0xFFFFFDE7) // Soft Yellow
                            else -> Color(0xFFF5F5F5)     // Soft Grey ("low")
                        }
                        val prioTextCol = when (note.priority) {
                            "high" -> Color(0xFFC62828)
                            "medium" -> Color(0xFFF57F17)
                            else -> Color(0xFF616161)
                        }
                        Text(
                            text = Locales.getString(note.priority, language),
                            style = MaterialTheme.typography.labelSmall,
                            color = prioTextCol,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .background(prioBg, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = shamsiDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Dynamic parsed markdown note contents
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        MarkdownText(text = note.content, modifier = Modifier.fillMaxWidth())
                    }

                    // Display Tags
                    if (note.tags.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            note.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { tag ->
                                Text(
                                    text = "#$tag",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- 4. CATEGORIES SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavHostController,
    viewModel: NoteViewModel,
    language: AppLanguage
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var newCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Locales.getString("categories", language), fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            NotemanBottomNavigation(navController = navController, language = language)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // New Category Row
            Text(
                text = Locales.getString("add_category", language),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text(Locales.getString("category_name", language)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.weight(1f)
                )
                
                Button(
                    onClick = {
                        if (newCategoryName.trim().isNotEmpty()) {
                            viewModel.insertCategory(newCategoryName.trim())
                            newCategoryName = ""
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

            // Categories Management Stack
            Text(
                text = Locales.getString("manage_categories", language),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteCategory(category) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- 5. RECENT NOTES SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentNotesScreen(
    navController: NavHostController,
    viewModel: NoteViewModel,
    language: AppLanguage
) {
    val recentNotes by viewModel.recentNotes.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Locales.getString("recents", language), fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            NotemanBottomNavigation(navController = navController, language = language)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = Locales.getString("recent_desc", language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (recentNotes.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = Locales.getString("empty_notes", language),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(recentNotes) { note ->
                        val categoryName = categories.firstOrNull { it.id == note.categoryId }?.name ?: Locales.getString("none", language)
                        val shamsiDate = JalaliDateHelper.getJalaliDateFromTimestamp(note.timestamp, language == AppLanguage.PERSIAN)

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.markNoteAsViewed(note)
                                    navController.navigate("detail/${note.id}")
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = categoryName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                    
                                    Text(
                                        text = shamsiDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = note.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- 6. SETTINGS SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: NoteViewModel,
    language: AppLanguage
) {
    val darkModePreference by viewModel.darkMode.collectAsStateWithLifecycle()
    val notes by viewModel.filteredNotes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Locales.getString("settings", language), fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            NotemanBottomNavigation(navController = navController, language = language)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Language Selection Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = Locales.getString("language", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppLanguage.values().forEach { lang ->
                            val isSelected = language == lang
                            ElevatedButton(
                                onClick = { viewModel.setLanguage(lang) },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(lang.label)
                            }
                        }
                    }
                }
            }

            // Theme Mode Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Dark Mode Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = Locales.getString("theme", language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ElevatedButton(
                            onClick = { viewModel.setDarkMode(false) },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = if (darkModePreference == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (darkModePreference == false) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(Locales.getString("light", language))
                        }

                        ElevatedButton(
                            onClick = { viewModel.setDarkMode(true) },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = if (darkModePreference == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (darkModePreference == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(Locales.getString("dark", language))
                        }
                    }
                }
            }

            // About Developer Emad Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Developer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = Locales.getString("about_dev", language),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = Locales.getString("about_desc", language),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = Locales.getString("about_emad", language),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "v1.0",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
