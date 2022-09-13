package hr.algebra.boardgames.model

data class Item(
    var _id: Long?,
    var apiId: String,
    val name: String,
    val picturePath: String,
    val description: String,
    val playerCount: String,
    val playtimeRange: String,
    val rank: Int,
//    var read: Boolean,
    var isFavourite: Boolean
)
