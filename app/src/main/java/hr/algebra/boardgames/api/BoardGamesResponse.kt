package hr.algebra.boardgames.api

import com.google.gson.annotations.SerializedName

data class BoardGamesResponse(
    @SerializedName("games") var games: List<BoardGamesListItem>,
    @SerializedName("count") var count: Long
)