package hr.algebra.boardgames.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.boardgames.adapters.ItemsAdapter
import hr.algebra.boardgames.databinding.FragmentFavoritesBinding
import hr.algebra.boardgames.databinding.FragmentSearchBinding
import hr.algebra.boardgames.framework.fetchItems
import hr.algebra.boardgames.model.Item


class SearchFragment : Fragment() {

    private lateinit var items: MutableList<Item>
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        items = requireContext().fetchItems()
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
}