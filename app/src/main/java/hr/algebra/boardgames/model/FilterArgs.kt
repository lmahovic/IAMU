package hr.algebra.boardgames.model

import java.io.Serializable

const val FILTER_ARGS_PREFERENCES_KEY = "hr.algebra.boardgames.model.filterargs"

data class FilterArgs(
    val orderParam: OrderParam = OrderParam.RANK,
    val namePrefix: String = "",
    val minPlayers: Int? = null,
    val maxPlayers: Int? = null,
    val minPlaytime: Int? = null,
    val maxPlaytime: Int? = null,
) : Serializable