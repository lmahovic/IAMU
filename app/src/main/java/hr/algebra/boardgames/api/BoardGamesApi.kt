package hr.algebra.boardgames.api

import retrofit2.Call
import retrofit2.http.GET

const val API_URL = "https://api.nasa.gov/planetary/"
interface BoardGamesApi {
    @GET("apod?api_key=DEMO_KEY&count=10")
    fun fetchItems() : Call<List<BoardGamesItem>>
}