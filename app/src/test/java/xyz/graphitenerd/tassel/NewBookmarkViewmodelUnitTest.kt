package xyz.graphitenerd.tassel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import xyz.graphitenerd.tassel.model.NewBookmarkViewModel

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewBookmarkViewModelTest {
    val repository = FakeRepository()
    var viewModel: NewBookmarkViewModel? = null

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun createViewModel() {
        viewModel = NewBookmarkViewModel(
            repository,
            mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun testLoadingBookmarksForEditing() = runTest {
        viewModel?.loadBookmark(5327466503284867000)

        assertEquals(
            viewModel?.bookmarkForm?.state?.value?.address?.value,
            repository.getBookmarkById(5327466503284867000).rawUrl
        )

        assertEquals(
            viewModel?.bookmarkForm?.state?.value?.title?.value,
            repository.getBookmarkById(5327466503284867000).title
        )

        assertEquals(
            viewModel?.bookmarkForm?.state?.value?.folderTree?.value?.folderId,
            repository.getBookmarkById(5327466503284867000).folderId
        )
    }

    @Test
    fun testResettingFormStateafterLoading() = runTest {
        viewModel?.loadBookmark(5327466503284867000)

        assertEquals(
            viewModel?.bookmarkForm?.state?.value?.address?.value,
            repository.getBookmarkById(5327466503284867000).rawUrl
        )

        viewModel?.resetForm()

        assertEquals(
            viewModel?.bookmarkForm?.state?.value?.address?.value,
            null
        )


    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule constructor(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}