package xyz.graphitenerd.tassel.screens.create

data class AddBookmarkUiState(
    val isFetchingMetadata: Boolean = false,
    val isSaving: Boolean = false,
    val urlError: String? = null,
    val saveError: String? = null,
    val bookmarkSaved: Boolean = false
) {
    companion object {
        val Initial = AddBookmarkUiState()
    }
}
