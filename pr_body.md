# 🚀 Tassel Improvement Plan

This PR serves as a tracking hub for implementing comprehensive improvements to the Tassel bookmark manager app. Each improvement will be implemented incrementally through separate commits.

---

## 📋 **CODE REFACTORING & IMPROVEMENTS**

### **1. ViewModel Issues**

#### Fix Multiple Init Blocks in BookmarkViewModel
- **File:** `app/src/main/java/xyz/graphitenerd/tassel/screens/recents/BookmarkViewModel.kt:31`
- **Issue:** Two separate init blocks (lines 50-60 and 130-143) make initialization order unclear
- **Action:** Consolidate into single init block
- **Priority:** 🔴 HIGH

#### Inject Coroutine Dispatcher in BookmarkViewModel
- **File:** `app/src/main/java/xyz/graphitenerd/tassel/screens/recents/BookmarkViewModel.kt:31`
- **Issue:** Using hardcoded `Dispatchers.IO` throughout
- **Action:** Inject CoroutineDispatcher for better testability
- **Priority:** 🔴 HIGH

#### Remove runBlocking from NewBookmarkViewModel
- **File:** `app/src/main/java/xyz/graphitenerd/tassel/screens/create/NewBookmarkViewModel.kt:168`
- **Issue:** `runBlocking` can freeze UI thread
- **Action:** Convert `getFolderChildren` to suspend function using `withContext`
- **Priority:** 🔴 HIGH

### **2. State Management Issues**

#### Fix MutableStateFlow Exposure
- **File:** `BookmarkViewModel.kt:38`
- **Issue:** `deletedBookmarks` exposes MutableStateFlow directly
- **Action:** Use private mutable and public immutable StateFlow
- **Priority:** 🔴 HIGH

```kotlin
// Before
var deletedBookmarks: MutableStateFlow<List<Bookmark>> = MutableStateFlow(emptyList())
    private set

// After
private val _deletedBookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
val deletedBookmarks: StateFlow<List<Bookmark>> = _deletedBookmarks.asStateFlow()
```

### **3. Error Handling**

#### Add Error States to ViewModels
- **Issue:** No error handling in UI states
- **Action:** Implement sealed interface for UI states
- **Priority:** 🔴 HIGH

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val exception: Throwable? = null) : UiState<Nothing>
}
```

### **4. Remove Hard-coded Values**

#### Extract Magic Numbers to Constants
- **File:** `RecentsScreen.kt`
- **Issue:** Multiple magic numbers throughout (96.dp, 384.dp, etc.)
- **Action:** Create `Dimens` object for reusable dimensions
- **Priority:** 🟡 MEDIUM

### **5. Dependency Updates**

#### Update Outdated Dependencies
- **File:** `app/build.gradle`
- **Priority:** 🔴 HIGH

**Current → Recommended:**
- Room: `2.5.0-beta01` → `2.6.1` (stable)
- Kotlin: `1.9.25` → `2.0.0`
- Compose: `1.7.2` → `1.7.5`
- Remove deprecated Accompanist library

---

## 🏗️ **ARCHITECTURE IMPROVEMENTS**

### **1. Remove Navigation Coupling**

#### Remove NavController from RecentsScreen
- **File:** `RecentsScreen.kt:85`
- **Issue:** Violates single responsibility principle
- **Action:** Use only lambda callbacks for navigation
- **Priority:** 🟡 MEDIUM

### **2. Add Result Wrapper**

#### Implement Result Pattern
- **Action:** Add Result sealed class for repository operations
- **Priority:** 🟡 MEDIUM

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
```

### **3. Add Use Cases Layer**

#### Create Domain Layer
- **Action:** Add use cases between ViewModels and Repositories
- **Example:** `SyncBookmarksUseCase`, `SaveBookmarkUseCase`
- **Priority:** 🟡 MEDIUM

---

## 🎨 **UI/UX IMPROVEMENTS**

### **1. Complete Material Design 3 Migration**

#### Migrate from Material 2 to Material 3
- **File:** `RecentsScreen.kt`
- **Issue:** Mixed Material 2 and Material 3 components
- **Action:** Replace all `androidx.compose.material` with `androidx.compose.material3`
- **Priority:** 🔴 HIGH

### **2. Dark Theme Enhancement**

#### Implement Dynamic Theming
- **File:** `ui/theme/Theme.kt`
- **Action:** Add Material3 dynamic theming with system color support
- **Priority:** 🟡 MEDIUM

### **3. Add Loading States**

#### Implement Shimmer/Skeleton Screens
- **Action:** Add loading placeholders for bookmark cards
- **Priority:** 🟡 MEDIUM

### **4. Pull-to-Refresh**

#### Add Pull-to-Refresh Gesture
- **File:** `RecentsScreen.kt`
- **Action:** Implement pull-to-refresh for manual sync
- **Priority:** 🟡 MEDIUM

### **5. Enhanced Empty States**

#### Improve EmptyBookmarkFolder Component
- **Action:** Add illustrations, CTA buttons (Add Bookmark, Import)
- **Priority:** 🟡 MEDIUM

### **6. Accessibility Improvements**

#### Add Semantic Descriptions
- **Action:** Add contentDescription and role semantics to all interactive elements
- **Priority:** 🟡 MEDIUM

### **7. Better Search UX**

#### Add Search Suggestions and History
- **Action:** Implement recent searches with suggestion chips
- **Priority:** 🟢 LOW

---

## ⚡ **PERFORMANCE OPTIMIZATIONS**

### **1. Image Loading Configuration**

#### Configure Memory/Disk Caching
- **Action:** Set up Coil with proper cache limits (25% memory, 50MB disk)
- **Priority:** 🟡 MEDIUM

### **2. Database Indices**

#### Add Search Indices
- **File:** `model/Bookmark.kt`
- **Action:** Add indices for `title`, `desc`, `folderId`, `creation_date`
- **Priority:** 🟡 MEDIUM

---

## 🚀 **NEW FEATURES**

### **1. Tags System**

#### Implement Bookmark Tagging
- **Action:** Add Tag entity, BookmarkTagCrossRef, tag UI
- **Priority:** 🟡 MEDIUM
- **Value:** Better organization beyond folders

### **2. Archive Feature**

#### Add Archive/Unarchive Functionality
- **Action:** Add `isArchived` field to Bookmark model
- **Priority:** 🟡 MEDIUM
- **Value:** Hide bookmarks without deleting

### **3. Import/Export**

#### Browser Bookmark Import
- **Action:** Support Netscape HTML bookmark format
- **Priority:** 🟡 MEDIUM
- **Value:** Easy migration from browsers

#### JSON Export
- **Action:** Export bookmarks to JSON for backup
- **Priority:** 🟡 MEDIUM

### **4. Reading Mode**

#### Extract Article Content
- **Action:** Use existing Crux library for clean reading view
- **Priority:** 🟢 LOW
- **Value:** Ad-free reading experience

### **5. Collections/Smart Folders**

#### Dynamic Folder Rules
- **Action:** Create smart folders with filters (tags, date, domain)
- **Priority:** 🟢 LOW
- **Value:** Automatic organization

### **6. Bookmark Sharing**

#### Native Share Integration
- **Action:** Add share button to bookmark cards
- **Priority:** 🟢 LOW
- **Value:** Easy sharing with others

### **7. Offline Reading Queue**

#### Download for Offline Access
- **Action:** Cache bookmark content locally
- **Priority:** 🟢 LOW
- **Value:** Read without internet

### **8. Bookmark Suggestions**

#### ML-Based Folder Suggestions
- **Action:** Suggest folders based on URL patterns
- **Priority:** 🟢 LOW
- **Value:** Faster organization

### **9. Full-Text Search**

#### Implement FTS4/FTS5
- **Action:** Add Room FTS entity for better search
- **Priority:** 🟢 LOW
- **Value:** Search within page content

---

## 🧪 **TESTING IMPROVEMENTS**

### **1. ViewModel Unit Tests**

#### Add Comprehensive ViewModel Tests
- **Action:** Test state management, error handling, business logic
- **Priority:** 🟡 MEDIUM

### **2. Repository Tests**

#### Test Data Layer
- **Action:** Test sync logic, CRUD operations
- **Priority:** 🟡 MEDIUM

### **3. UI Tests**

#### Add Compose UI Tests
- **Action:** Test user interactions, navigation, selection mode
- **Priority:** 🟢 LOW

---

## 🔒 **SECURITY IMPROVEMENTS**

### **1. URL Validation**

#### Validate URLs Before Saving
- **Action:** Add URL validation with scheme and host checks
- **Priority:** 🔴 HIGH

### **2. Input Sanitization**

#### Sanitize User Input
- **Action:** Trim, limit length, remove dangerous characters
- **Priority:** 🔴 HIGH

---

## 📊 **ANALYTICS & MONITORING**

### **1. Event Tracking**

#### Track Key User Actions
- **Action:** Add analytics for bookmark creation, search, folder operations
- **Priority:** 🟢 LOW

---

## 📝 **IMPLEMENTATION PLAN**

### **Phase 1: Critical Fixes (Week 1)**
- [ ] Fix runBlocking in NewBookmarkViewModel
- [ ] Consolidate init blocks in BookmarkViewModel
- [ ] Inject coroutine dispatchers
- [ ] Add error handling with UiState
- [ ] Fix MutableStateFlow exposure
- [ ] Update Room from beta to stable
- [ ] Add URL validation
- [ ] Input sanitization

### **Phase 2: Material 3 Migration (Week 2)**
- [ ] Migrate all Material 2 components to Material 3
- [ ] Implement dynamic theming
- [ ] Add loading states with shimmer
- [ ] Remove hard-coded dimensions
- [ ] Add pull-to-refresh

### **Phase 3: Architecture Improvements (Week 3)**
- [ ] Remove NavController from composables
- [ ] Implement Result wrapper pattern
- [ ] Add use cases layer
- [ ] Enhanced empty states
- [ ] Accessibility improvements

### **Phase 4: Features (Week 4+)**
- [ ] Tags system
- [ ] Archive feature
- [ ] Import/Export functionality
- [ ] Better search UX
- [ ] Reading mode
- [ ] Other features as prioritized

---

## 🎯 **Success Metrics**

- **Code Quality:** Reduce cyclomatic complexity, improve test coverage
- **Performance:** Faster app startup, smoother scrolling
- **User Experience:** Better accessibility scores, improved usability
- **Stability:** Fewer crashes, better error handling

---

**Let's discuss and prioritize which improvements to tackle first!**

🤖 Generated with [Claude Code](https://claude.com/claude-code)
