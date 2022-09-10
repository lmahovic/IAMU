package hr.algebra.boardgames.api

import android.content.ContentValues
import android.content.Context
import android.util.Log
import hr.algebra.boardgames.DATA_IMPORTED
import hr.algebra.boardgames.NASA_PROVIDER_URI
import hr.algebra.boardgames.NasaReceiver
import hr.algebra.boardgames.framework.sendBroadcast
import hr.algebra.boardgames.framework.setBooleanProperty
import hr.algebra.boardgames.handler.downloadImageAndStore
import hr.algebra.boardgames.model.Item
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NasaFetcher(private val context: Context) {

    private var nasaApi: NasaApi
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        nasaApi = retrofit.create(NasaApi::class.java)
    }

    fun fetchItems() {
        val request = nasaApi.fetchItems()

        request.enqueue(object: Callback<List<NasaItem>> {
            override fun onResponse(
                call: Call<List<NasaItem>>,
                response: Response<List<NasaItem>>
            ) {
                response.body()?.let {
                    populateItems(it)
                }
            }

            override fun onFailure(call: Call<List<NasaItem>>, t: Throwable) {
                Log.d(javaClass.name, t.message, t)
            }

        })
    }

    private fun populateItems(nasaItems: List<NasaItem>) {

        GlobalScope.launch {

             nasaItems.forEach {
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
                context.contentResolver.insert(NASA_PROVIDER_URI, values);


            }

            context.setBooleanProperty(DATA_IMPORTED, true)
            context.sendBroadcast<NasaReceiver>()
        }
    }

}