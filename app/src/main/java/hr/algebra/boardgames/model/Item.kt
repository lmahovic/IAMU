package hr.algebra.boardgames.model

data class Item(
    var _id: Long?,
    val apiId: String,
    val name: String,
    var apiPicturePath: String,
    var localPicturePath: String?,
    val description: String,
    val playerCount: String,
    val playtimeRange: String,
    val rank: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (apiId != other.apiId) return false

        return true
    }

    override fun hashCode(): Int {
        return apiId.hashCode()
    }
}


