package yordanov.radoslav.flickr.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import yordanov.radoslav.flickr.network.model.Photos

private const val API_KEY = "3216db85a6cefbef73a5354e676ed612"

interface FlickrService {
    @GET("?method=flickr.photos.search&api_key=$API_KEY&format=json&nojsoncallback=1")
    fun loadImages(@Query("text") query: String, @Query("page") page: Int): Single<PhotosResponse>
}

@JsonClass(generateAdapter = true)
data class PhotosResponse(
    @Json(name = "photos")
    val photos: Photos
)