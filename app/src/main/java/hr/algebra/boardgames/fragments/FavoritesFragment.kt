package hr.algebra.boardgames.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.boardgames.adapters.FavoriteFragmentRecyclerViewAdapter
import hr.algebra.boardgames.databinding.FragmentFavoritesBinding
import hr.algebra.boardgames.framework.fetchItems
import hr.algebra.boardgames.model.Item
import hr.algebra.boardgames.viewmodels.FavoriteItemsViewModel


class FavoritesFragment : Fragment() {

    private lateinit var rvItems: RecyclerView
    private lateinit var items: MutableList<Item>
    private lateinit var binding: FragmentFavoritesBinding
    private val favoriteItemsViewModel: FavoriteItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        items = requireContext().fetchItems()
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvItems = binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = FavoriteFragmentRecyclerViewAdapter(
                requireContext(),
                favoriteItemsViewModel
            )
        }
    }
}