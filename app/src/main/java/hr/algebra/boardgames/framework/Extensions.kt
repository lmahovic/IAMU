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
import hr.algebra.boardgames.BOARD_GAMES_PROVIDER_URI
import hr.algebra.boardgames.model.ListItem
import retrofit2.Response
import java.io.IOException

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

fun Context.getBooleanProperty(key: String) = PreferenceManager.getDefaultSharedPreferences(this)
    .getBoolean(key, false)

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

inline fun <reified T : Activity> Context.startActivity(key: String = "", value: Int = 0) =
    startActivity(Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (key.isNotBlank()) {
            putExtra(key, value)
        }
    })

inline fun <reified T : BroadcastReceiver> Context.sendBroadcast() =
    sendBroadcast(Intent(this, T::class.java))

fun callDelayed(delay: Long, function: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(
        function,
        delay
    )
}

fun Context.fetchItems(): MutableList<ListItem> {
    val listItems = mutableListOf<ListItem>()

    val cursor = contentResolver?.query(
        BOARD_GAMES_PROVIDER_URI,
        null,
        null,
        null,
        null
    )
    while (cursor != null && cursor.moveToNext()) {
        listItems.add(
            ListItem(
                cursor.getLong(cursor.getColumnIndexOrThrow(ListItem::_id.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(ListItem::name.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(ListItem::description.name)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ListItem::rank.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(ListItem::picturePath.name)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ListItem::read.name)) == 1
            )
        )
    }
    cursor?.close()

    return listItems
}

fun Context.showConnectionErrorMessage(error: Throwable) =
    if (error is IOException) {
        Toast.makeText(this, "A connection error occured", Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(this, "Failed to retrieve items ", Toast.LENGTH_LONG).show()
    }

fun Context.showStatusCodeErrorMessage(response: Response<*>) =
    Toast.makeText(this, "Error status code received - ${response.code()} Error message - ${response.message()}", Toast.LENGTH_LONG)
        .show()