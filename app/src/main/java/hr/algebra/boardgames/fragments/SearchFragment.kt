package hr.algebra.boardgames.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import hr.algebra.boardgames.viewmodels.FilterArgsViewModel


class SearchFragment : Fragment() {

    private lateinit var mProgressDialog: Dialog
    private lateinit var items: MutableList<Item>
    private lateinit var binding: FragmentSearchBinding

    private val viewModel: FilterArgsViewModel by activityViewModels()

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
        viewModel.filterArgs.observe(viewLifecycleOwner) {
            println(it)
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
    private fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}