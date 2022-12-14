package hr.algebra.boardgames.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val BAD_RANK = 9999999

data class BoardGamesItem(

    @SerializedName("id") var id: String,
    @SerializedName("name") var name: String,
    @SerializedName("image_url") var imageUrl: String,
    @SerializedName("description") var description: String,
    @SerializedName("players") var playerCount: String?,
    @SerializedName("playtime") var playtimeRange: String?,
    @SerializedName("rank") var rank: Int

) : Serializable
