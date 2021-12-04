package yordanov.radoslav.flickr.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import yordanov.radoslav.flickr.model.FlickrUiModel
import yordanov.radoslav.flickr.model.FlickrViewState
import yordanov.radoslav.flickr.network.model.Photo
import yordanov.radoslav.flickr.repository.FlickrRepository
import javax.inject.Inject

@HiltViewModel
class FlickrViewModel @Inject constructor(
    private val flickrRepository: FlickrRepository
) : ViewModel() {

    val flickrImageList: MutableLiveData<PagingData<FlickrUiModel>> = MutableLiveData()
    val flickrViewState: MutableLiveData<FlickrViewState> = MutableLiveData()
    private val disposable = CompositeDisposable()

    @ExperimentalCoroutinesApi
    fun loadImages(query: String) {
        flickrRepository.loadImages(query, viewModelScope)
            .map { it.asUiModel() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { photos -> flickrImageList.value = photos }
            .also { disposable.add(it) }
    }

    fun onLoadStateChange(state: LoadState, itemCount: Int) {
        when (state) {
            is LoadState.NotLoading -> {
                if (itemCount == 0) {
                    flickrViewState.postValue(FlickrViewState.EMPTY)
                } else {
                    flickrViewState.postValue(FlickrViewState.NOT_LOADING)
                }
            }

            is LoadState.Error -> flickrViewState.postValue(FlickrViewState.EMPTY)

            is LoadState.Loading -> Unit
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    private fun PagingData<Photo>.asUiModel(): PagingData<FlickrUiModel> {
        return this.map { photo ->
            val imageUrl = "https://farm${photo.farm}.static.flickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
            FlickrUiModel(photo.id, photo.title, imageUrl)
        }
    }

}