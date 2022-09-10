package hr.algebra.boardgames

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hr.algebra.boardgames.framework.startActivity

class BoardGamesReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.startActivity<HostActivity>()
    }
}