package yordanov.radoslav.flickr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import yordanov.radoslav.flickr.R
import yordanov.radoslav.flickr.databinding.ItemFlickrBinding
import yordanov.radoslav.flickr.model.FlickrUiModel

class FlickrAdapter : PagingDataAdapter<FlickrUiModel, FlickrAdapter.FlickrViewHolder>(DiffCallback) {

    private object DiffCallback : DiffUtil.ItemCallback<FlickrUiModel>() {
        override fun areItemsTheSame(
            oldItem: FlickrUiModel,
            newItem: FlickrUiModel
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: FlickrUiModel,
            newItem: FlickrUiModel
        ) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrViewHolder =
        FlickrViewHolder(
            ItemFlickrBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
        )

    override fun onBindViewHolder(holder: FlickrViewHolder, position: Int) {
        val item = getItem(position) as FlickrUiModel
        holder.bind(item)
    }

    class FlickrViewHolder(private val binding: ItemFlickrBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(flickrUiModel: FlickrUiModel) {
            binding.tvTitle.text = flickrUiModel.title
            Glide.with(binding.root.context).load(flickrUiModel.imageUrl).placeholder(R.drawable.ic_launcher_background).into(binding.ivPhoto)
        }
    }
}