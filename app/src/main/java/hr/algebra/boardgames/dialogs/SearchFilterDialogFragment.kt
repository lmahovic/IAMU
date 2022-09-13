package hr.algebra.boardgames.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import hr.algebra.boardgames.R
import hr.algebra.boardgames.model.FilterArgs
import hr.algebra.boardgames.model.OrderParam
import hr.algebra.boardgames.viewmodels.FilterArgsViewModel

class SearchFilterDialogFragment : DialogFragment() {

    private val viewModel: FilterArgsViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.search_filter_dialog, null)
            val spSort = view.findViewById<Spinner>(R.id.sort_by_spinner)
            val etNamePrefix = view.findViewById<EditText>(R.id.name_prefix)
            val etMinPlayers = view.findViewById<EditText>(R.id.min_players)
            val etMaxPlayers = view.findViewById<EditText>(R.id.max_players)
            val etMinPlaytime = view.findViewById<EditText>(R.id.min_playtime)
            val etMaxPlaytime = view.findViewById<EditText>(R.id.max_playtime)

            spSort.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                OrderParam.values()
            )

            builder.setView(view)
                .setPositiveButton(
                    R.string.apply
                ) { _, _ ->
                    val filterArgs = FilterArgs(
                        spSort.selectedItem as OrderParam,
                        etNamePrefix.text.toString(),
                        etMinPlayers.text.toString().toIntOrNull(),
                        etMaxPlayers.text.toString().toIntOrNull(),
                        etMinPlaytime.text.toString().toIntOrNull(),
                        etMaxPlaytime.text.toString().toIntOrNull(),
                    )
                    viewModel.updateFilterArgs(filterArgs)
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    dialog!!.cancel()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}