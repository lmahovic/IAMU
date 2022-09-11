package hr.algebra.boardgames.api

import android.content.ContentValues
import android.content.Context
import android.util.Log
import hr.algebra.boardgames.BOARD_GAMES_PROVIDER_URI
import hr.algebra.boardgames.BoardGamesReceiver
import hr.algebra.boardgames.DATA_IMPORTED
import hr.algebra.boardgames.framework.sendBroadcast
import hr.algebra.boardgames.framework.setBooleanProperty
import hr.algebra.boardgames.handler.downloadImageAndStore
import hr.algebra.boardgames.model.Item
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class BoardGamesFetcher(private val context: Context) {

    private var boardGamesApi: BoardGamesApi

    init {

        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getLoggingClient())
            .build()

        boardGamesApi = retrofit.create(BoardGamesApi::class.java)
    }

    private fun getLoggingClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }


    fun fetchItems() {
        val request = boardGamesApi.fetchItems()

        request.enqueue(object : Callback<List<BoardGamesItem>> {
            override fun onResponse(
                call: Call<List<BoardGamesItem>>,
                response: Response<List<BoardGamesItem>>
            ) {
                response.body()?.let {
                    populateItems(it)
                }
            }

            override fun onFailure(call: Call<List<BoardGamesItem>>, t: Throwable) {
                Log.d(javaClass.name, t.message, t)
            }

        })
    }

    private fun populateItems(boardGamesItems: List<BoardGamesItem>) {

        GlobalScope.launch {

            boardGamesItems.forEach {
                val picturePath = downloadImageAndStore(
                    context,
                    it.url,
                    it.title.replace(" ", "_")
                )
                val values = ContentValues().apply {
                    put(Item::title.name, it.title)
                    put(Item::explanation.name, it.explanation)
                    put(Item::picturePath.name, picturePath ?: "")
                    put(Item::date.name, it.date)
                    put(Item::read.name, false)
                }
                context.contentResolver.insert(BOARD_GAMES_PROVIDER_URI, values);


            }

            context.setBooleanProperty(DATA_IMPORTED, true)
            context.sendBroadcast<BoardGamesReceiver>()
        }
    }

}