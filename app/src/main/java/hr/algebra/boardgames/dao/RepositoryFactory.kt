package hr.algebra.boardgames.dao

import android.content.Context

fun getBoardGameRepository(context: Context?) = BoardGamesSqlHelper(context)