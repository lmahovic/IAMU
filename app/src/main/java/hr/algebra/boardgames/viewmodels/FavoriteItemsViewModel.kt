package hr.algebra.boardgames.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hr.algebra.boardgames.model.Item

class FavoriteItemsViewModel : ViewModel() {
    private val mutableFavoriteItems = MutableLiveData<List<Item>>()

    val favoriteItems: LiveData<List<Item>> get() = mutableFavoriteItems

    fun addToFavoriteItems(item: Item) {
        val listCopy = mutableFavoriteItems.value!!.toMutableList()
        listCopy.add(item)
        mutableFavoriteItems.value = listCopy
    }

    fun removeFromFavoriteItems(item: Item) {
        val listCopy = mutableFavoriteItems.value!!.toMutableList()
        listCopy.remove(item)
        mutableFavoriteItems.value = listCopy
    }

    fun setFavoriteItems(items: List<Item>) {
        mutableFavoriteItems.value = items
    }
}