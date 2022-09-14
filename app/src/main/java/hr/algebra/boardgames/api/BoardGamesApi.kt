package hr.algebra.boardgames.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

const val API_URL = "https://api.boardgameatlas.com/api/"

//not very safe but - who cares :)
const val API_CLIENT_KEY_PARAMETER_NAME = "client_id"
const val API_CLIENT_KEY_PARAMETER_VALUE = "FeykxfqCLi"

interface BoardGamesApi {
    @GET("search?limit=10&fuzzy_match=true")
    fun fetchItems(@QueryMap filters: Map<String, String>): Call<BoardGamesSearchResponse>
}