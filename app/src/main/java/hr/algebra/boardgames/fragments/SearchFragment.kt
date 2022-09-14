package hr.algebra.boardgames.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.boardgames.R
import hr.algebra.boardgames.adapters.SearchFragmentRecyclerViewAdapter
import hr.algebra.boardgames.api.BoardGamesFetcher
import hr.algebra.boardgames.databinding.FragmentSearchBinding
import hr.algebra.boardgames.dialogs.SearchFilterDialogFragment
import hr.algebra.boardgames.framework.restoreItemsFromPreferences
import hr.algebra.boardgames.model.Item
import hr.algebra.boardgames.viewmodels.FavoriteItemsViewModel
import hr.algebra.boardgames.viewmodels.FilterArgsViewModel


class SearchFragment : Fragment() {

    private lateinit var rvItems: RecyclerView
    private lateinit var mProgressDialog: Dialog
    private lateinit var items: MutableList<Item>
    private lateinit var binding: FragmentSearchBinding

    private val filterArgsViewModel: FilterArgsViewModel by activityViewModels()
    private val favoriteItemsViewModel: FavoriteItemsViewModel by activityViewModels()

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
        items = requireContext().restoreItemsFromPreferences()
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        showMessageIfListEmpty()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvItems = binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter =
                SearchFragmentRecyclerViewAdapter(requireContext(), items, favoriteItemsViewModel)
        }
        filterArgsViewModel.filterArgs.observe(viewLifecycleOwner) {
            showProgressDialog()
            BoardGamesFetcher(requireContext()).fetchItems(this)
        }

        favoriteItemsViewModel.favoriteItems.observe(viewLifecycleOwner) {
            updateRecyclerViewFavorites()
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

    private fun showProgressDialog() {
        mProgressDialog = Dialog(requireContext())

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_custom_progress)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun updateRecyclerViewFromApi() {
        items = requireContext().restoreItemsFromPreferences()
        val searchFragmentRecyclerViewAdapter = rvItems.adapter as SearchFragmentRecyclerViewAdapter
        searchFragmentRecyclerViewAdapter.updateItemList(items)
        showMessageIfListEmpty()
        hideProgressDialog()
    }

    private fun updateRecyclerViewFavorites() {
        val searchFragmentRecyclerViewAdapter = rvItems.adapter as SearchFragmentRecyclerViewAdapter
        searchFragmentRecyclerViewAdapter.updateItemList(items)
    }

    private fun showMessageIfListEmpty() {
        if (items.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.rvItems.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.rvItems.visibility = View.VISIBLE
        }
    }
}