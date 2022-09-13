package hr.algebra.boardgames.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import hr.algebra.boardgames.R
import hr.algebra.boardgames.activities.API_RESPONSE_STRING_KEY
import hr.algebra.boardgames.adapters.ItemsAdapter
import hr.algebra.boardgames.api.BoardGamesSearchResponse
import hr.algebra.boardgames.databinding.FragmentSearchBinding
import hr.algebra.boardgames.dialogs.SearchFilterDialogFragment
import hr.algebra.boardgames.framework.getStringProperty
import hr.algebra.boardgames.model.Item


class SearchFragment : Fragment() {

    private lateinit var items: MutableList<Item>
    private lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        items = requireContext().fetchItems()
        val itemsJsonString = requireContext().getStringProperty(API_RESPONSE_STRING_KEY)
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
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ItemsAdapter(requireContext(), items)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuSearch -> {
                showSearchDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSearchDialog() {
        val dialog = SearchFilterDialogFragment()
        dialog.show(childFragmentManager, "Search filter")
    }
}