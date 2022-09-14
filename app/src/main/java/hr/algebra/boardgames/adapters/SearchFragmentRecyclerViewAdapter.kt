package hr.algebra.boardgames.adapters

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.boardgames.R
import hr.algebra.boardgames.activities.ItemPagerActivity
import hr.algebra.boardgames.api.BAD_RANK
import hr.algebra.boardgames.contentproviders.BOARD_GAMES_PROVIDER_URI
import hr.algebra.boardgames.framework.isItemInFavorites
import hr.algebra.boardgames.framework.startActivity
import hr.algebra.boardgames.handler.downloadImageAndStore
import hr.algebra.boardgames.model.Item
import hr.algebra.boardgames.viewmodels.FavoriteItemsViewModel
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class SearchFragmentRecyclerViewAdapter(
    private val context: Context,
    private var items: MutableList<Item>,
    private val favoriteItemsViewModel: FavoriteItemsViewModel,
) : RecyclerView.Adapter<SearchFragmentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.findViewById<ImageView>(R.id.ivFavorite).setOnClickListener {
            if (favoriteItemsViewModel.isItemInFavorites(item)) {
                deleteItem(position)
                favoriteItemsViewModel.removeFromFavoriteItems(item)
            } else {
                addItem(item)
                favoriteItemsViewModel.addToFavoriteItems(item)
            }
            notifyItemChanged(position)
        }

        holder.itemView.setOnClickListener {
            context.startActivity<ItemPagerActivity>(position)
        }

        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItemImage = itemView.findViewById<ImageView>(R.id.ivItemImage)
        private val tvItemName = itemView.findViewById<TextView>(R.id.tvItemName)
        private val tvRank = itemView.findViewById<TextView>(R.id.tvRank)
        private val tvPlayerCount = itemView.findViewById<TextView>(R.id.tvPlayerCount)
        private val tvPlayTime = itemView.findViewById<TextView>(R.id.tvPlayTime)
        private val ivFavorite = itemView.findViewById<ImageView>(R.id.ivFavorite)
        fun bind(item: Item) {
            tvItemName.text = item.name
            tvRank.text = if (item.rank != BAD_RANK) {
                item.rank.toString()
            } else {
                "n/a"
            }
            tvPlayerCount.text = item.playerCount
            tvPlayTime.text = item.playtimeRange
            ivFavorite.setImageResource(
                if (favoriteItemsViewModel.isItemInFavorites(item)) {
                    R.drawable.ic_favorite
                } else {
                    R.drawable.ic_favorite_border
                }
            )

            Picasso.get()
                .load(Uri.parse(item.apiPicturePath))
                .placeholder(R.drawable.loading_icon)
                .error(R.drawable.board_games_about)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivItemImage)
        }
    }

    override fun getItemCount() = items.size

    private fun deleteItem(position: Int) {
        val item = items[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(BOARD_GAMES_PROVIDER_URI, item._id!!),
            null,
            null
        )
        File(item.localPicturePath!!).delete()
        favoriteItemsViewModel.removeFromFavoriteItems(item)
        item._id = null
        item.localPicturePath = null
    }

    private fun addItem(item: Item) {

        GlobalScope.launch {
            item.let {
                val picturePath = downloadImageAndStore(
                    context,
                    it.apiPicturePath,
                    it.name.replace(" ", "_")
                )
                picturePath?.let {
                    item.localPicturePath = picturePath
                }
                val values = ContentValues().apply {
                    put(Item::apiId.name, it.apiId)
                    put(Item::name.name, it.name)
                    put(Item::apiPicturePath.name, it.apiPicturePath)
                    put(Item::localPicturePath.name, picturePath ?: "")
                    put(Item::description.name, it.description)
                    put(Item::playerCount.name, it.playerCount)
                    put(Item::playtimeRange.name, it.playtimeRange)
                    put(Item::rank.name, it.rank)
                }
                val itemUrl = context.contentResolver.insert(BOARD_GAMES_PROVIDER_URI, values)
                if (itemUrl != null) {
                    item._id = ContentUris.parseId(itemUrl)
                }
            }
        }
    }

    fun updateItemList(newItems: MutableList<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}