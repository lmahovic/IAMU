package hr.algebra.boardgames.api

import android.content.ContentValues
import android.content.Context
import com.google.gson.Gson
import hr.algebra.boardgames.activities.API_RESPONSE_STRING_KEY
import hr.algebra.boardgames.activities.DATA_IMPORTED
import hr.algebra.boardgames.broadcastreceivers.BoardGamesReceiver
import hr.algebra.boardgames.contentproviders.BOARD_GAMES_PROVIDER_URI
import hr.algebra.boardgames.framework.*
import hr.algebra.boardgames.handler.downloadImageAndStore
import hr.algebra.boardgames.model.Item
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class BoardGamesFetcher(private val context: Context) {


    private var boardGamesApi: BoardGamesApi

    init {

        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getConfiguredClient())
            .build()

        boardGamesApi = retrofit.create(BoardGamesApi::class.java)
    }

    private fun getConfiguredClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            //ADD CLIENT KEY INTERCEPTOR
            .addInterceptor(Interceptor
            {
                it.proceed(
                    it.request().newBuilder()
                        .url(
                            it.request().url.newBuilder()
                                .addQueryParameter(
                                    API_CLIENT_KEY_PARAMETER_NAME,
                                    API_CLIENT_KEY_PARAMETER_VALUE
                                )
                                .build()
                        )
                        .build()
                )

            })
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun fetchItems() {
        val request = boardGamesApi.fetchItems()

        request.enqueue(object : Callback<BoardGamesSearchResponse> {
            override fun onResponse(
                call: Call<BoardGamesSearchResponse>,
                response: Response<BoardGamesSearchResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
//                        populateItems(it.games)
                        val responseJson = Gson().toJson(response.body())
                        context.setStringProperty(API_RESPONSE_STRING_KEY, responseJson)

                        it.games.map { boardGamesItem ->
                            Item(
                                null,
                                boardGamesItem.id,
                                boardGamesItem.name,
                                boardGamesItem.imageUrl,
                                boardGamesItem.description,
                                boardGamesItem.playerCount ?: "n/a",
                                boardGamesItem.playtimeRange ?: "n/a",
                                boardGamesItem.rank,
                                false
                            )
                        }

                        context.sendBroadcast<BoardGamesReceiver>()
                    }
                } else {
                    context.showStatusCodeErrorMessage(response)
                }

            }

            override fun onFailure(call: Call<BoardGamesSearchResponse>, t: Throwable) {
                context.showConnectionErrorMessage(t)
            }
        })
    }

    private fun populateItems(boardGamesItems: List<BoardGamesItem>) {

        GlobalScope.launch {

            boardGamesItems.forEach {
                val picturePath = downloadImageAndStore(
                    context,
                    it.imageUrl,
                    it.name.replace(" ", "_")
                )
                val values = ContentValues().apply {
                    put(Item::apiId.name, it.id)
                    put(Item::name.name, it.name)
                    put(Item::picturePath.name, picturePath ?: "")
                    put(Item::description.name, it.description)
                    put(Item::playerCount.name, it.playerCount ?: "n/a")
                    put(Item::playtimeRange.name, it.playtimeRange ?: "n/a")
                    put(Item::rank.name, it.rank)
                    put(Item::isFavourite.name, false)
                }
                context.contentResolver.insert(BOARD_GAMES_PROVIDER_URI, values)


            }

            context.setBooleanProperty(DATA_IMPORTED, true)
            context.sendBroadcast<BoardGamesReceiver>()
        }
    }

}