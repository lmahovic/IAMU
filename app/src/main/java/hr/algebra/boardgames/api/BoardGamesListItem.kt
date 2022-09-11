package hr.algebra.boardgames.api

import com.google.gson.annotations.SerializedName

data class BoardGamesListItem(

    @SerializedName("name") var name: String,
    @SerializedName("image_url") var imageUrl: String,
    @SerializedName("description") var description: String,
    @SerializedName("rank") var rank: Int

)
