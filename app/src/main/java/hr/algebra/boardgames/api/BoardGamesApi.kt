package hr.algebra.boardgames.api

import retrofit2.Call
import retrofit2.http.GET

const val API_URL = "https://api.boardgameatlas.com/api/"

//not very safe but - who cares :)
const val API_CLIENT_KEY_PARAMETER_NAME = "client_id"
const val API_CLIENT_KEY_PARAMETER_VALUE = "FeykxfqCLi"

interface BoardGamesApi {
    @GET("search?limit=10&order_by=rank&fields=name,image_url,rank,description")
    fun fetchItems(): Call<BoardGamesResponse>
}