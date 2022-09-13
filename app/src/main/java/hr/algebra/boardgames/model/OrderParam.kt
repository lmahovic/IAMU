package hr.algebra.boardgames.model

enum class OrderParam(val apiValue: String, private val displayName: String) {
    RANK("rank", "Rank"),
    MIN_PLAYTIME("min_playtime", "Min playtime"),
    MAX_PLAYTIME("max_playtime", "Max playtime"),
    MIN_PLAYERS("min_players", "Min players"),
    MAX_PLAYERS("max_players", "Max players"),;

    override fun toString(): String {
        return displayName
    }}