package hr.algebra.boardgames.framework

import android.animation.AnimatorInflater
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import hr.algebra.boardgames.activities.API_RESPONSE_STRING_KEY
import hr.algebra.boardgames.activities.IS_FAVORITES
import hr.algebra.boardgames.activities.ITEM_POSITION
import hr.algebra.boardgames.api.BoardGamesSearchResponse
import hr.algebra.boardgames.contentproviders.BOARD_GAMES_PROVIDER_URI
import hr.algebra.boardgames.model.Item
import hr.algebra.boardgames.viewmodels.FavoriteItemsViewModel
import retrofit2.Response
import java.io.IOException

inline fun <reified T : Activity> Context.startActivity(
    itemPosition: Int? = null,
    isFavorites: Boolean = false
) {
    startActivity(Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (itemPosition != null) {
            putExtra(ITEM_POSITION, itemPosition)
            putExtra(IS_FAVORITES, isFavorites)
        }
    })
}

inline fun <reified T : BroadcastReceiver> Context.sendBroadcast() =
    sendBroadcast(Intent(this, T::class.java))

fun callDelayed(delay: Long, function: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(
        function,
        delay
    )
}

fun View.startAnimation(animationId: Int) {
    val animator = AnimatorInflater.loadAnimator(context, animationId)
    animator.apply {
        animator.setTarget(this@startAnimation)
        animator.start()
    }
}

fun Context.setBooleanProperty(key: String, value: Boolean) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putBoolean(key, value)
        .apply()

fun Context.getBooleanProperty(key: String) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(key, false)

fun Context.setStringProperty(key: String, value: String) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putString(key, value)
        .apply()

fun Context.getStringProperty(key: String) = PreferenceManager.getDefaultSharedPreferences(this)
    .getString(key, "")

fun Context.isOnline(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>();
    connectivityManager?.activeNetwork?.let { network ->
        connectivityManager.getNetworkCapabilities(network)?.let { networkCapabilities ->
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
    }
    return false
}

fun Context.fetchItems(): MutableList<Item> {
    val items = mutableListOf<Item>()

    val cursor = contentResolver?.query(
        BOARD_GAMES_PROVIDER_URI,
        null,
        null,
        null,
        null
    )
    while (cursor != null && cursor.moveToNext()) {
        items.add(
            Item(
                cursor.getLong(cursor.getColumnIndexOrThrow(Item::_id.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::apiId.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::name.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::apiPicturePath.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::localPicturePath.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::description.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::playerCount.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::playtimeRange.name)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Item::rank.name)),
            )
        )
    }
    cursor?.close()

    return items
}

fun Context.showConnectionErrorMessage(error: Throwable) =
    if (error is IOException) {
        Toast.makeText(this, "A connection error occured", Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(this, "Failed to retrieve items ", Toast.LENGTH_LONG).show()
    }

fun Context.showStatusCodeErrorMessage(response: Response<*>) =
    Toast.makeText(
        this,
        "Error status code received - ${response.code()} Error message - ${response.message()}",
        Toast.LENGTH_LONG
    )
        .show()

fun FavoriteItemsViewModel.isItemInFavorites(item: Item): Boolean {
    if (!this.favoriteItems.value!!.contains(item)) {
        return false
    }
    val databaseItem = this.favoriteItems.value!!.first { it.apiId == item.apiId }
    item._id = databaseItem._id
    return true
}

fun Context.restoreItemsFromPreferences(): MutableList<Item> {
    val itemsJsonString = this.getStringProperty(API_RESPONSE_STRING_KEY)
    val boardGames =
        Gson().fromJson(itemsJsonString, BoardGamesSearchResponse::class.java).games
    return boardGames.map { boardGamesItem ->
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
    }.toMutableList()
}