package yordanov.radoslav.flickr.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photos(
    @Json(name = "page")
    val page: Int,

    @Json(name = "pages")
    val pages: Int,

    @Json(name = "perpage")
    val perpage: Int,

    @Json(name = "total")
    val total: Int,

    @Json(name = "photo")
    val photo: List<Photo>

)