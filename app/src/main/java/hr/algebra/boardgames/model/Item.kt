package hr.algebra.boardgames.model

data class Item(
    var _id: Long?,
    val name: String,
    val description: String,
    val rank: Int,
    val picturePath: String,
    var read: Boolean

)
