package hr.algebra.boardgames.adapters

import android.content.Context
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import hr.algebra.boardgames.R
import hr.algebra.boardgames.api.BAD_RANK
import hr.algebra.boardgames.model.Item
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class SearchFragmentItemPagerAdapter(
    private val context: Context,
    private val items: MutableList<Item>,
    private val isFavorites: Boolean
) :
    RecyclerView.Adapter<SearchFragmentItemPagerAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItem = itemView.findViewById<ImageView>(R.id.ivItemImage)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvName)
        private val tvRank = itemView.findViewById<TextView>(R.id.tvRank)
        private val tvPlayerCount = itemView.findViewById<TextView>(R.id.tvPlayerCount)
        private val tvPlayTime = itemView.findViewById<TextView>(R.id.tvPlayTime)
        private val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)
        fun bind(item: Item) {
            tvTitle.text = item.name
            tvRank.text = if (item.rank != BAD_RANK) {
                item.rank.toString()
            } else {
                "n/a"
            }
            tvPlayerCount.text = item.playerCount
            tvPlayTime.text = item.playtimeRange

            tvDescription.text = Html.fromHtml(
                item.description,
                Html.FROM_HTML_MODE_COMPACT,
            )
//            ivRead.setImageResource(if (item.read) R.drawable.green_flag else R.drawable.red_flag)
            val picasso = Picasso.get()
            val creator: RequestCreator = if (isFavorites) {
                picasso.load(File(item.localPicturePath!!))
            } else {
                picasso.load(Uri.parse(item.apiPicturePath))
            }
            creator.transform(RoundedCornersTransformation(50, 5))
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
//        holder.ivRead.setOnClickListener {
//            item.read = !item.read
//            val uri = ContentUris.withAppendedId(BOARD_GAMES_PROVIDER_URI, item._id!!)
//            val values = ContentValues().apply {
//                put(Item::read.name, item.read)
//            }
//            context.contentResolver.update(
//                uri,
//                values,
//                null,
//                null
//            )
//            notifyItemChanged(position)
//        }
        holder.bind(item)
    }

    override fun getItemCount() = items.size
}