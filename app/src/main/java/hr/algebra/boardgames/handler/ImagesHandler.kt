package hr.algebra.boardgames.handler

import android.content.Context
import android.util.Log
import hr.algebra.boardgames.factory.createGetHttpUrlConnection
import java.io.File
import java.net.HttpURLConnection
import java.nio.file.Files
import java.nio.file.Paths

fun downloadImageAndStore(context: Context, url: String, fileName: String): String? {
    //https://apod.nasa.gov/apod/image/2106/NovaCasAndFriends_Ayoub_2230.jpg
    val indexOfFirstQuestionMark = url.indexOfFirst { it == '?' }

    val queryStringsRemoved = if (indexOfFirstQuestionMark == -1) {
        url
    } else {
        url.substring(0, indexOfFirstQuestionMark)
    }

    val urlFileName = Paths.get(queryStringsRemoved).fileName.toString()
    val lastIndexOfDot = urlFileName.lastIndexOf(".")
    val ext = if (lastIndexOfDot == -1) {
        ".jpg"
    } else {
        urlFileName.substring(lastIndexOfDot)
    }
    val file: File = createFile(context, fileName, ext)
    try {

        val con: HttpURLConnection = createGetHttpUrlConnection(url)
        Files.copy(con.inputStream, Paths.get(file.toURI()))
        return file.absolutePath
    } catch (e: Exception) {
        Log.e("DOWNLOAD IMAGE", e.message, e)
    }
    return null
}

fun createFile(context: Context, fileName: String, ext: String): File {
    val dir = context.applicationContext.getExternalFilesDir(null)
    val file = File(dir, File.separator + fileName + ext)
    if (file.exists()) {
        file.delete()
    }
    return file
}
