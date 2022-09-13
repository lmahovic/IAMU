package hr.algebra.boardgames.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BoardGamesSearchResponse(
    @SerializedName("games") var games: List<BoardGamesItem>,
    @SerializedName("count") var count: Long
) : Serializable