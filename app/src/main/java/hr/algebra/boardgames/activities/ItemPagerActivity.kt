package hr.algebra.boardgames.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import hr.algebra.boardgames.adapters.SearchFragmentItemPagerAdapter
import hr.algebra.boardgames.api.BoardGamesSearchResponse
import hr.algebra.boardgames.databinding.ActivityItemPagerBinding
import hr.algebra.boardgames.framework.getStringProperty
import hr.algebra.boardgames.model.Item

const val ITEM_POSITION = "hr.algebra.nasa.item_position"

class ItemPagerActivity : AppCompatActivity() {

    private lateinit var items: MutableList<Item>
    private lateinit var binding: ActivityItemPagerBinding

    private var itemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPager()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun initPager() {
//        items = fetchItems()
        val itemsJsonString = this.getStringProperty(API_RESPONSE_STRING_KEY)
        val boardGames =
            Gson().fromJson(itemsJsonString, BoardGamesSearchResponse::class.java).games
        items = boardGames.map { boardGamesItem ->
            Item(
                null,
                boardGamesItem.id,
                boardGamesItem.name,
                boardGamesItem.imageUrl,
                boardGamesItem.description,
                boardGamesItem.playerCount ?: "n/a",
                boardGamesItem.playtimeRange ?: "n/a",
                boardGamesItem.rank,
                false
            )
        }.toMutableList()

        itemPosition = intent.getIntExtra(ITEM_POSITION, 0)

        binding.viewPager.adapter = SearchFragmentItemPagerAdapter(this, items)
        binding.viewPager.currentItem = itemPosition
    }
}