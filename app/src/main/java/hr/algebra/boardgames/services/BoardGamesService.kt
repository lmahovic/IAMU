package hr.algebra.boardgames.services

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import hr.algebra.boardgames.api.BoardGamesFetcher
import hr.algebra.boardgames.model.FilterArgs

private const val JOB_ID = 1

class BoardGamesService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {

        BoardGamesFetcher(this).fetchItems(FilterArgs())

    }

    companion object {
        fun enqueue(context: Context, intent: Intent) =
            enqueueWork(context, BoardGamesService::class.java, JOB_ID, intent)
    }


}