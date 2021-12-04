package yordanov.radoslav.flickr.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyZeroInteractions
import yordanov.radoslav.flickr.model.FlickrViewState
import yordanov.radoslav.flickr.repository.FlickrRepository


@RunWith(MockitoJUnitRunner::class)
class FlickrViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val flickrRepository = mock<FlickrRepository>()
    private val flickrViewStateObserver = mock<Observer<FlickrViewState>>()
    private lateinit var flickrViewModel: FlickrViewModel

    @Before
    fun setUp() {
        flickrViewModel = FlickrViewModel(flickrRepository)
        flickrViewModel.flickrViewState.observeForever(flickrViewStateObserver)
    }

    @Test
    fun `onLoadStateChange() with LoadState Error should post EMPTY state`() {
        // given
        val loadStateError = LoadState.Error(Throwable())

        // when
        flickrViewModel.onLoadStateChange(loadStateError, EMPTY_COUNT)

        // verify
        verify(flickrViewStateObserver).onChanged(FlickrViewState.EMPTY)
    }

    @Test
    fun `onLoadStateChange() with LoadState Loading shouldn't post state`() {
        // when
        flickrViewModel.onLoadStateChange(LoadState.Loading, EMPTY_COUNT)

        // verify
        verifyZeroInteractions(flickrViewStateObserver)
    }

    @Test
    fun `onLoadStateChange() with LoadState NotLoading and no data should post EMPTY state`() {
        // when
        flickrViewModel.onLoadStateChange(LoadState.NotLoading(false), EMPTY_COUNT)

        // verify
        verify(flickrViewStateObserver).onChanged(FlickrViewState.EMPTY)
    }

    @Test
    fun `onLoadStateChange() with LoadState NotLoading and data should post NOT_LOADING state`() {
        // when
        flickrViewModel.onLoadStateChange(LoadState.NotLoading(false), POSITIVE_COUNT)

        // verify
        verify(flickrViewStateObserver).onChanged(FlickrViewState.NOT_LOADING)
    }

    companion object {
        private const val EMPTY_COUNT = 0
        private const val POSITIVE_COUNT = 1
    }
}