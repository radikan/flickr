package yordanov.radoslav.flickr.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import androidx.paging.rxjava3.cachedIn
import androidx.paging.rxjava3.flowable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import yordanov.radoslav.flickr.network.FlickrService
import yordanov.radoslav.flickr.network.PhotosResponse
import yordanov.radoslav.flickr.network.model.Photo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepository @Inject constructor(
    private val flickrService: FlickrService,
) {
    @ExperimentalCoroutinesApi
    fun loadImages(query: String, scope: CoroutineScope): Flowable<PagingData<Photo>> =
        Pager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)) {
            ImagesPagingSource(flickrService, query, PAGE_SIZE)
        }
            .flowable
            .cachedIn(scope)

    companion object {
        private const val PAGE_SIZE = 100
    }
}

private class ImagesPagingSource(
    private val flickrService: FlickrService,
    private val query: String,
    private val pageSize: Int
) : RxPagingSource<Int, Photo>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Photo>> {
        val page = params.key ?: FIRST_PAGE

        return flickrService.loadImages(query, page)
            .map { it.asLoadResult(page) }
            .onErrorReturn { LoadResult.Error(it) }
            .subscribeOn(Schedulers.io())
    }

    private fun PhotosResponse.asLoadResult(page: Int): LoadResult<Int, Photo> =
        LoadResult.Page(
            data = this.photos.photo,
            prevKey = null,
            nextKey = calculateNextPage(page, pageSize, this.photos.total)
        )

    companion object {
        private const val FIRST_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it) }?.nextKey
    }

}

fun calculateNextPage(page: Int, pageSize: Int, numberOfRecords: Int): Int? =
    if (page * pageSize < numberOfRecords) {
        page + 1
    } else {
        null
    }