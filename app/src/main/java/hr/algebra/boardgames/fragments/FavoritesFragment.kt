package hr.algebra.boardgames.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import hr.algebra.boardgames.activities.API_RESPONSE_STRING_KEY
import hr.algebra.boardgames.adapters.ItemsAdapter
import hr.algebra.boardgames.api.BoardGamesSearchResponse
import hr.algebra.boardgames.databinding.FragmentFavoritesBinding
import hr.algebra.boardgames.framework.fetchItems
import hr.algebra.boardgames.framework.getStringProperty
import hr.algebra.boardgames.model.Item


class FavoritesFragment : Fragment() {

    private lateinit var items: MutableList<Item>
    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ItemsAdapter(
                requireContext(),
                items.filter { it.isFavourite }.toMutableList()
            )
        }
    }
}