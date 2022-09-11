package hr.algebra.boardgames.api

import android.content.ContentValues
import android.content.Context
import hr.algebra.boardgames.BOARD_GAMES_PROVIDER_URI
import hr.algebra.boardgames.BoardGamesReceiver
import hr.algebra.boardgames.DATA_IMPORTED
import hr.algebra.boardgames.framework.sendBroadcast
import hr.algebra.boardgames.framework.setBooleanProperty
import hr.algebra.boardgames.framework.showConnectionErrorMessage
import hr.algebra.boardgames.framework.showStatusCodeErrorMessage
import hr.algebra.boardgames.handler.downloadImageAndStore
import hr.algebra.boardgames.model.ListItem
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

        request.enqueue(object : Callback<List<BoardGamesListItem>> {
            override fun onResponse(
                call: Call<List<BoardGamesListItem>>,
                response: Response<List<BoardGamesListItem>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        populateItems(it)
                    }
                } else {
                    context.showStatusCodeErrorMessage(response)
                }

            }

            override fun onFailure(call: Call<List<BoardGamesListItem>>, t: Throwable) {
                context.showConnectionErrorMessage(t)
            }

        })
    }

    private fun populateItems(boardGamesListItems: List<BoardGamesListItem>) {

        GlobalScope.launch {

            boardGamesListItems.forEach {
                val picturePath = downloadImageAndStore(
                    context,
                    it.imageUrl,
                    it.name.replace(" ", "_")
                )
                val values = ContentValues().apply {
                    put(ListItem::name.name, it.name)
                    put(ListItem::rank.name, it.rank)
                    put(ListItem::picturePath.name, picturePath ?: "")
                    put(ListItem::read.name, false)
                }
                context.contentResolver.insert(BOARD_GAMES_PROVIDER_URI, values)


            }

            context.setBooleanProperty(DATA_IMPORTED, true)
            context.sendBroadcast<BoardGamesReceiver>()
        }
    }

}