package hr.algebra.boardgames

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
import hr.algebra.boardgames.framework.startActivity
import hr.algebra.boardgames.model.ListItem
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class ItemsAdapter(private val context: Context, private val listItems: MutableList<ListItem>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItem = itemView.findViewById<ImageView>(R.id.ivItem)
        private val tvItem = itemView.findViewById<TextView>(R.id.tvItem)
        fun bind(listItem: ListItem) {
            tvItem.text = listItem.title
            Picasso.get()
                .load(File(listItem.picturePath))
                .error(R.drawable.nasa)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.delete)
                setMessage(context.getString(R.string.sure) + " '${item.title}'?")
                setIcon(R.drawable.delete)
                setCancelable(true)
                setNegativeButton(R.string.cancel, null)
                setPositiveButton("Ok") {_, _ -> deleteItem(position)}
                show()
            }
            true
        }
        holder.itemView.setOnClickListener {
            context.startActivity<ItemPagerActivity>(ITEM_POSITION, position)
        }

        holder.bind(item)
    }

    private fun deleteItem(position: Int) {
        val item = listItems[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(BOARD_GAMES_PROVIDER_URI, item._id!!),
            null,
            null
        )
        File(item.picturePath).delete()
        listItems.removeAt(position)
        notifyDataSetChanged() // observable kick
    }

    override fun getItemCount() = listItems.size
}