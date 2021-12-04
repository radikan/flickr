package yordanov.radoslav.flickr.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import yordanov.radoslav.flickr.R
import yordanov.radoslav.flickr.adapters.FlickrAdapter
import yordanov.radoslav.flickr.databinding.ActivityFlickrBinding
import yordanov.radoslav.flickr.model.FlickrViewState
import yordanov.radoslav.flickr.viewmodel.FlickrViewModel
import java.util.*

@AndroidEntryPoint
class FlickrActivity : AppCompatActivity() {
    private val flickrViewModel: FlickrViewModel by viewModels()
    private lateinit var flickrAdapter: FlickrAdapter
    private lateinit var dataBinding: ActivityFlickrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_flickr)

        initRecyclerView()
        observeData()
    }

    @ExperimentalCoroutinesApi
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_flickr, menu)

        getSearchView(menu)?.apply {

            (getSystemService(Context.SEARCH_SERVICE) as? SearchManager)?.let {
                setSearchableInfo(it.getSearchableInfo(componentName))
            }

            setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String): Boolean {
                        flickrViewModel.loadImages(query)
                        return false
                    }

                    override fun onQueryTextChange(query: String): Boolean {
                        return false
                    }
                }
            )
        }

        return true
    }

    private fun getSearchView(menu: Menu): SearchView? =
        (menu.findItem(R.id.action_search)?.actionView as? SearchView)

    private fun initRecyclerView() {
        flickrAdapter = FlickrAdapter()
        dataBinding.rvFlickr.apply {
            layoutManager = GridLayoutManager(this@FlickrActivity, 3)
            adapter = flickrAdapter
        }
        flickrAdapter.addLoadStateListener {
            flickrViewModel.onLoadStateChange(it.refresh, flickrAdapter.itemCount)
        }
    }

    private fun observeData() {
        flickrViewModel.flickrImageList.observe(this, { items -> flickrAdapter.submitData(lifecycle, items) })
        flickrViewModel.flickrViewState.observe(this, { viewState ->
            when (viewState) {
                FlickrViewState.EMPTY -> handleEmptyState()
                FlickrViewState.NOT_LOADING -> handleNotLoadingState()
                else -> handleEmptyState()
            }
        })
    }

    private fun handleEmptyState() {
        dataBinding.apply {
            rvFlickr.visibility = View.INVISIBLE
            tvEmptyState.text = getString(R.string.no_results)
            tvEmptyState.visibility = View.VISIBLE
        }
    }

    private fun handleNotLoadingState() {
        dataBinding.apply {
            tvEmptyState.visibility = View.INVISIBLE
            rvFlickr.visibility = View.VISIBLE
        }
    }
}