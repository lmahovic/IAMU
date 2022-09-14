package hr.algebra.boardgames.adapters

import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
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
import hr.algebra.boardgames.framework.startActivity
import hr.algebra.boardgames.model.Item
import hr.algebra.boardgames.viewmodels.FavoriteItemsViewModel
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class FavoriteFragmentRecyclerViewAdapter(
    private val context: Context,
    private var favoriteItemsViewModel: FavoriteItemsViewModel
) : RecyclerView.Adapter<FavoriteFragmentRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getViewModelItems()[position]

        holder.itemView.findViewById<ImageView>(R.id.ivFavorite).setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.delete)
                setMessage(context.getString(R.string.sure) + " '${item.name}'?")
                setIcon(R.drawable.delete)
                setCancelable(true)
                setNegativeButton(R.string.cancel, null)
                setPositiveButton("Ok") { _, _ -> deleteItem(position) }
                show()
            }
        }

        holder.itemView.setOnClickListener {
            context.startActivity<ItemPagerActivity>(position, true)
        }

        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            ivFavorite.setImageResource(R.drawable.ic_favorite)

            Picasso.get()
                .load(File(item.localPicturePath!!))
                .error(R.drawable.board_games_about)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivItemImage)
        }
    }

    private fun deleteItem(position: Int) {
        val item = getViewModelItems()[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(BOARD_GAMES_PROVIDER_URI, item._id!!),
            null,
            null
        )
        File(item.localPicturePath!!).delete()
        favoriteItemsViewModel.removeFromFavoriteItems(item)
        item._id = null
        item.localPicturePath = null
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, getViewModelItems().size)
    }

    override fun getItemCount() = getViewModelItems().size

    private fun getViewModelItems(): List<Item> {
        return favoriteItemsViewModel.favoriteItems.value!!
    }
}