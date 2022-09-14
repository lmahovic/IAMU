package hr.algebra.boardgames.api

import android.content.Context
import com.google.gson.Gson
import hr.algebra.boardgames.activities.API_RESPONSE_STRING_KEY
import hr.algebra.boardgames.broadcastreceivers.BoardGamesReceiver
import hr.algebra.boardgames.fragments.SearchFragment
import hr.algebra.boardgames.framework.*
import hr.algebra.boardgames.model.FILTER_ARGS_PREFERENCES_KEY
import hr.algebra.boardgames.model.FilterArgs
import hr.algebra.boardgames.model.Item
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val ORDER_BY_API_PARAMETER = "order_by"
private const val NAME_API_PARAMETER = "name"
private const val MIN_PLAYERS_API_PARAMETER = "gt_min_players"
private const val MAX_PLAYERS_API_PARAMETER = "gt_max_players"
private const val MIN_PLAYTIME_API_PARAMETER = "gt_min_playtime"
private const val MAX_PLAYTIME_API_PARAMETER = "lt_max_playtime"

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

    fun fetchItems(searchFragment: SearchFragment? = null) {

        val filterArgs: FilterArgs
        val filterArgsString = context.getStringProperty(FILTER_ARGS_PREFERENCES_KEY)

        if (filterArgsString.isNullOrEmpty()) {
            filterArgs = FilterArgs()
            val filterArgsSer = Gson().toJson(filterArgs)
            context.setStringProperty(FILTER_ARGS_PREFERENCES_KEY, filterArgsSer)
        } else {
            filterArgs = Gson().fromJson(filterArgsString, FilterArgs::class.java)
        }

        val request = boardGamesApi.fetchItems(createQueryMap(filterArgs))

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
                                null,
                                boardGamesItem.description,
                                boardGamesItem.playerCount ?: "n/a",
                                boardGamesItem.playtimeRange ?: "n/a",
                                boardGamesItem.rank,
                            )
                        }

                        if (searchFragment == null) {
                            context.sendBroadcast<BoardGamesReceiver>()
                        } else {
                            searchFragment.updateRecyclerViewFromApi()
                        }
                    }
                } else {
                    searchFragment?.hideProgressDialog()
                    context.showStatusCodeErrorMessage(response)
                }

            }

            override fun onFailure(call: Call<BoardGamesSearchResponse>, t: Throwable) {
                searchFragment?.hideProgressDialog()
                context.showConnectionErrorMessage(t)
            }
        })
    }

    private fun createQueryMap(filterArgs: FilterArgs): HashMap<String, String> {
        val queryMap = HashMap<String, String>()

        queryMap[ORDER_BY_API_PARAMETER] = filterArgs.orderParam.apiValue
        if (filterArgs.namePrefix.isNotBlank()) {
            queryMap[NAME_API_PARAMETER] = filterArgs.namePrefix
        }
        filterArgs.minPlayers?.let { queryMap[MIN_PLAYERS_API_PARAMETER] = (it - 1).toString() }
        filterArgs.maxPlayers?.let { queryMap[MAX_PLAYERS_API_PARAMETER] = (it - 1).toString() }
        filterArgs.minPlaytime?.let { queryMap[MIN_PLAYTIME_API_PARAMETER] = (it - 1).toString() }
        filterArgs.maxPlaytime?.let { queryMap[MAX_PLAYTIME_API_PARAMETER] = (it + 1).toString() }
        return queryMap
    }
}