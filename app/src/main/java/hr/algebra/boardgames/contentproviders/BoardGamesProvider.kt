package hr.algebra.boardgames.contentproviders

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import hr.algebra.boardgames.dao.BoardGamesRepository
import hr.algebra.boardgames.dao.getBoardGameRepository
import hr.algebra.boardgames.model.Item

private const val AUTHORITY = "hr.algebra.boardgames.api.provider"
private const val PATH = "items"

private const val ITEMS = 10
private const val ITEM_ID = 20

private val URI_MATCHER = with(UriMatcher(UriMatcher.NO_MATCH)) {
    addURI(AUTHORITY, PATH, ITEMS)
    addURI(AUTHORITY, "$PATH/#", ITEM_ID)
    this
}

val BOARD_GAMES_PROVIDER_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")

class BoardGamesProvider : ContentProvider() {

    private lateinit var repository: BoardGamesRepository

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (URI_MATCHER.match(uri)) {
            ITEMS -> return repository.delete(selection, selectionArgs)
            ITEM_ID -> {
                uri.lastPathSegment?.let {
                    return repository.delete("${Item::_id.name}=?", arrayOf(it))
                }
            }
        }
        throw IllegalArgumentException("Wrong uri")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val id = repository.insert(values)
        return ContentUris.withAppendedId(BOARD_GAMES_PROVIDER_URI, id)
    }

    override fun onCreate(): Boolean {
        repository = getBoardGameRepository(context)
        return true
    }


    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor = repository.query(
        projection,
        selection,
        selectionArgs,
        sortOrder
    )

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when (URI_MATCHER.match(uri)) {
            ITEMS -> return repository.update(values, selection, selectionArgs)
            ITEM_ID -> {
                uri.lastPathSegment?.let {
                    return repository.update(values, "${Item::_id.name}=?", arrayOf(it))
                }
            }
        }
        throw IllegalArgumentException("Wrong uri")
    }
}