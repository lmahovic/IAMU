package hr.algebra.boardgames.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hr.algebra.boardgames.adapters.ItemPagerAdapter
import hr.algebra.boardgames.databinding.ActivityItemPagerBinding
import hr.algebra.boardgames.framework.fetchItems
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
        items = fetchItems()
        itemPosition = intent.getIntExtra(ITEM_POSITION, 0)

        binding.viewPager.adapter = ItemPagerAdapter(this, items)
        binding.viewPager.currentItem = itemPosition
    }
}