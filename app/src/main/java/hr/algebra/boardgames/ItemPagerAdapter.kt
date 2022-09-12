package hr.algebra.boardgames

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.algebra.boardgames.model.Item
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class ItemPagerAdapter(private val context: Context, private val items: MutableList<Item>) :
    RecyclerView.Adapter<ItemPagerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItem = itemView.findViewById<ImageView>(R.id.ivItemImage)
        val ivRead: ImageView = itemView.findViewById(R.id.ivRead)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvName)
        private val tvExplanation = itemView.findViewById<TextView>(R.id.tvDescription)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvRank)
        fun bind(item: Item) {
            tvTitle.text = item.name
            tvExplanation.text = Html.fromHtml(
                item.description,
                Html.FROM_HTML_MODE_COMPACT,
            )
            tvDate.text = item.rank.toString()
            ivRead.setImageResource(if (item.read) R.drawable.green_flag else R.drawable.red_flag)
            Picasso.get()
                .load(File(item.picturePath))
                .transform(RoundedCornersTransformation(50, 5))
                .error(R.drawable.board_games_about)
                .into(ivItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_pager, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.ivRead.setOnClickListener {
            item.read = !item.read
            val uri = ContentUris.withAppendedId(BOARD_GAMES_PROVIDER_URI, item._id!!)
            val values = ContentValues().apply {
                put(Item::read.name, item.read)
            }
            context.contentResolver.update(
                uri,
                values,
                null,
                null
            )
            notifyItemChanged(position)
        }
        holder.bind(item)
    }

    override fun getItemCount() = items.size
}