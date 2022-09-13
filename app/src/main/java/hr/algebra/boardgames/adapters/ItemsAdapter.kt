package hr.algebra.boardgames.adapters

import android.app.AlertDialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.boardgames.R
import hr.algebra.boardgames.activities.ITEM_POSITION
import hr.algebra.boardgames.activities.ItemPagerActivity
import hr.algebra.boardgames.contentproviders.BOARD_GAMES_PROVIDER_URI
import hr.algebra.boardgames.framework.startActivity
import hr.algebra.boardgames.model.Item
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class ItemsAdapter(private val context: Context, private val items: MutableList<Item>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.delete)
                setMessage(context.getString(R.string.sure) + " '${item.name}'?")
                setIcon(R.drawable.delete)
                setCancelable(true)
                setNegativeButton(R.string.cancel, null)
                setPositiveButton("Ok") { _, _ -> deleteItem(position) }
                show()
            }
            true
        }
        holder.itemView.findViewById<ImageView>(R.id.ivFavorite).setOnClickListener {
            item.isFavourite = !item.isFavourite
            val uri = ContentUris.withAppendedId(BOARD_GAMES_PROVIDER_URI, item._id!!)
            val values = ContentValues().apply {
                put(Item::isFavourite.name, item.isFavourite)
            }
            context.contentResolver.update(uri, values, null, null)
            val message = if (item.isFavourite) {
                "Game added to favorite games!"
            } else {
                "Game removed from favorite games!"
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            notifyItemChanged(position)
        }

        holder.itemView.setOnClickListener {
            context.startActivity<ItemPagerActivity>(ITEM_POSITION, position)
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
            tvRank.text = item.rank.toString()
            tvPlayerCount.text = item.playerCount
            tvPlayTime.text = item.playtimeRange
            ivFavorite.setImageResource(
                if (item.isFavourite) {
                    R.drawable.ic_favorite
                } else {
                    R.drawable.ic_favorite_border
                }
            )

            Picasso.get()
                .load(File(item.picturePath))
                .error(R.drawable.board_games_about)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivItemImage)
        }
    }

    private fun deleteItem(position: Int) {
        val item = items[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(BOARD_GAMES_PROVIDER_URI, item._id!!),
            null,
            null
        )
        File(item.picturePath).delete()
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    override fun getItemCount() = items.size
}