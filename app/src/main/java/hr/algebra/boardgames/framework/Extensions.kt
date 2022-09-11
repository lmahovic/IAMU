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
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import hr.algebra.boardgames.BOARD_GAMES_PROVIDER_URI
import hr.algebra.boardgames.model.Item

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
                cursor.getString(cursor.getColumnIndexOrThrow(Item::title.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::explanation.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::picturePath.name)),
                cursor.getString(cursor.getColumnIndexOrThrow(Item::date.name)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Item::read.name)) == 1
            )
        )
    }

    return items
}