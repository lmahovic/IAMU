package hr.algebra.boardgames.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hr.algebra.boardgames.adapters.SearchFragmentItemPagerAdapter
import hr.algebra.boardgames.databinding.ActivityItemPagerBinding
import hr.algebra.boardgames.framework.fetchItems
import hr.algebra.boardgames.framework.restoreItemsFromPreferences
import hr.algebra.boardgames.model.Item

const val ITEM_POSITION = "hr.algebra.nasa.item_position"
const val IS_FAVORITES = "hr.algebra.nasa.is_favorites"

class ItemPagerActivity : AppCompatActivity() {

    private lateinit var items: MutableList<Item>
    private lateinit var binding: ActivityItemPagerBinding

    private var itemPosition = 0
    private var isFavorites: Boolean = false

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


        itemPosition = intent.getIntExtra(ITEM_POSITION, 0)
        isFavorites = intent.getBooleanExtra(IS_FAVORITES, false)

        items = if (!isFavorites) {
            restoreItemsFromPreferences()
        } else {
            fetchItems()
        }

        binding.viewPager.adapter = SearchFragmentItemPagerAdapter(this, items, isFavorites)
        binding.viewPager.currentItem = itemPosition
    }
}