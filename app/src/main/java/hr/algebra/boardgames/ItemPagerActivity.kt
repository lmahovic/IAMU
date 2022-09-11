package hr.algebra.boardgames

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hr.algebra.boardgames.databinding.ActivityItemPagerBinding
import hr.algebra.boardgames.framework.fetchItems
import hr.algebra.boardgames.model.ListItem

const val ITEM_POSITION = "hr.algebra.nasa.item_position"

class ItemPagerActivity : AppCompatActivity() {

    private lateinit var listItems: MutableList<ListItem>
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
        listItems = fetchItems()
        itemPosition = intent.getIntExtra(ITEM_POSITION, 0)

        binding.viewPager.adapter = ItemPagerAdapter(this, listItems)
        binding.viewPager.currentItem = itemPosition
    }
}