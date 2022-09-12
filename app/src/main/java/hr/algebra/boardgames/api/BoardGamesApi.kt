package hr.algebra.boardgames.api

import retrofit2.Call
import retrofit2.http.GET

const val API_URL = "https://api.boardgameatlas.com/api/"

//not very safe but - who cares :)
const val API_CLIENT_KEY_PARAMETER_NAME = "client_id"
const val API_CLIENT_KEY_PARAMETER_VALUE = "FeykxfqCLi"

interface BoardGamesApi {
    @GET("search?limit=20&order_by=rank&name=Carcassonne&fuzzy_match=true")
    fun fetchItems(): Call<BoardGamesSearchResponse>
}