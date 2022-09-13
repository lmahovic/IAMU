package hr.algebra.boardgames.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.algebra.boardgames.model.Item

private const val DB_NAME = "items.db"
private const val DB_VERSION = 1

private const val TABLE = "list_items"

private val CREATE = "create table $TABLE(" +
        "${Item::_id.name} integer primary key autoincrement, " +
        "${Item::apiId.name} text not null unique, " +
        "${Item::name.name} text not null, " +
        "${Item::picturePath.name} text not null, " +
        "${Item::description.name} text not null, " +
        "${Item::playerCount.name} text not null, " +
        "${Item::playtimeRange.name} text not null, " +
        "${Item::rank.name} integer not null, " +
        "${Item::isFavourite.name} integer not null" +
        ")"

private const val DROP = "drop table $TABLE"


class BoardGamesSqlHelper(
    context: Context?,
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), BoardGamesRepository {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int, newVersion: Int
    ) {
        db.execSQL(DROP)
        onCreate(db)
    }

    override fun delete(selection: String?, selectionArgs: Array<String>?) =
        writableDatabase.delete(TABLE, selection, selectionArgs)

    override fun update(
        values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ) = writableDatabase.update(TABLE, values, selection, selectionArgs)

    override fun insert(values: ContentValues?) = writableDatabase.insert(TABLE, null, values)

    override fun query(
        projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor = readableDatabase.query(
        TABLE,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )
}