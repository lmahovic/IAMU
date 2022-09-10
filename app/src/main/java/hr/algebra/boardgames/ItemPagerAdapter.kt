package hr.algebra.boardgames

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
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
        private val ivItem = itemView.findViewById<ImageView>(R.id.ivItem)
        val ivRead = itemView.findViewById<ImageView>(R.id.ivRead)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvExplanation = itemView.findViewById<TextView>(R.id.tvExplanation)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        fun bind(item: Item) {
            tvTitle.text = item.title
            tvExplanation.text = item.explanation
            tvDate.text = item.date
            ivRead.setImageResource(if (item.read) R.drawable.green_flag else R.drawable.red_flag)
            Picasso.get()
                .load(File(item.picturePath))
                .error(R.drawable.nasa)
                .transform(RoundedCornersTransformation(50, 5))
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