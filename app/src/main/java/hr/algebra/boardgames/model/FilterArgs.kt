package hr.algebra.boardgames.model

data class FilterArgs(
    val orderParam: OrderParam,
    val namePrefix: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val minPlaytime: Int,
    val maxPlaytime: Int,
)