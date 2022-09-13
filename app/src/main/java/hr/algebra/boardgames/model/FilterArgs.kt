package hr.algebra.boardgames.model

data class FilterArgs(
    val orderParam: OrderParam = OrderParam.RANK,
    val namePrefix: String = "",
    val minPlayers: Int? = null,
    val maxPlayers: Int? = null,
    val minPlaytime: Int? = null,
    val maxPlaytime: Int? = null,
)