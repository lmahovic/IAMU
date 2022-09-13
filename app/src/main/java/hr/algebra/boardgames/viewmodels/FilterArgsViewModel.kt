package hr.algebra.boardgames.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hr.algebra.boardgames.model.FilterArgs

class FilterArgsViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<FilterArgs>()
    val filterArgs: LiveData<FilterArgs> get() = mutableSelectedItem

    fun updateFilterArgs(filterArgs: FilterArgs) {
        mutableSelectedItem.value = filterArgs
    }
}