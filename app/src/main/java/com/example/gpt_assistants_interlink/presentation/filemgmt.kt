// This will manage the writing and reading of files on the device
package com.example.gpt_assistants_interlink.presentation

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.FileInputStream
import android.media.MediaPlayer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


@Throws(IOException::class)
fun write_text_to_file(context: Context, fileName: String, text: String, isPublic: Boolean = false) {
    /**
     * Writes the given text to a file.
     *
     * @param context The context of the application/package.
     * @param fileName The name of the file to write to.
     * @param text The text to write to the file.
     * @param isPublic Indicates whether the file should be saved to public or private storage.
     * @throws IOException If an I/O error occurs.
     */

    val fileOutputStream: FileOutputStream

    if (isPublic) {
        // For public storage (requires permission and the file will be accessible by other apps)
        val publicDirectory = context.getExternalFilesDir(null)
        val file = File(publicDirectory, fileName)

        // Make sure the directory exists
        file.parentFile?.mkdirs()

        fileOutputStream = FileOutputStream(file)
    } else {
        // For private storage (doesn't require permission and the file will not be accessible by other apps)
        val file = File(context.filesDir, fileName)
        fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
    }

    fileOutputStream.use { outputStream ->
        outputStream.write(text.toByteArray())
    }
}


@Throws(IOException::class)
fun read_text_from_file(context: Context, fileName: String, isPublic: Boolean = false): String {
    /**
     * Reads text from a file.
     *
     * @param context The context of the application/package.
     * @param fileName The name of the file to read from.
     * @param isPublic Indicates whether the file should be read from public or private storage.
     * @return The text read from the file.
     * @throws IOException If the file is not found or an I/O error occurs.
     */

    val fileInputStream: FileInputStream

    if (isPublic) {
        // For public storage (requires permission if file is not in app-specific directory)
        val publicDirectory = context.getExternalFilesDir(null)
        val file = File(publicDirectory, fileName)
        fileInputStream = FileInputStream(file)
    } else {
        // For private storage (no permission required as the file is internal to the app)
        val file = File(context.filesDir, fileName)
        fileInputStream = context.openFileInput(fileName)
    }

    return fileInputStream.use { inputStream ->
        inputStream.bufferedReader().use { it.readText() }
    }
}

@Throws(IOException::class)
fun append_or_create_file(context: Context, fileName: String, text: String, append: Boolean = true) {
    /**
     * Appends text to the specified file, or creates the file if it doesn't already exist.
     *
     * @param context The context of the application or activity.
     * @param fileName The name of the file to write to.
     * @param text The content to append to the file.
     * @param append Whether to append to the file if it exists, or overwrite.
     * @throws IOException If an I/O error occurs.
     */

    // Open a file output stream in append or overwrite mode.
    context.openFileOutput(fileName, if (append) Context.MODE_APPEND else Context.MODE_PRIVATE).use { outputStream ->
        // Write the text content to the file.
        outputStream.write(text.toByteArray())
        // Ensure the written content is flushed to the file.
        outputStream.flush()
    }
}

fun delete_file(context: Context, fileName: String): Boolean {
    /**
     * Deletes the file with the given file name from the internal storage.
     *
     * @param context The context of the application or activity.
     * @param fileName The name of the file to delete.
     * @return True if the file was successfully deleted, false otherwise.
     */
    val file = File(context.filesDir, fileName)
    return file.delete()
}

fun file_exists(context: Context, fileName: String): Boolean {
    /**
     * Checks if a file exists in the application's internal storage.
     *
     * @param context The context of the application.
     * @param fileName Name of the file for which existence has to be checked.
     * @return True if the file exists, false otherwise.
     */
    val file = File(context.filesDir, fileName)
    return file.exists()
}

fun playAudioFromFile(context: Context, filePath: String) {
    val mediaPlayer = MediaPlayer().apply {
        try {
            setDataSource(filePath)
            prepare() // Prepares the player for playback, synchronously.
            start() // Starts the playback.
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the error here, such as by showing an error message to the user.
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            // Handle the error here.
        }
    }

    // Make sure to release the media player resources when done
    mediaPlayer.setOnCompletionListener {
        it.release()
    }

    mediaPlayer.setOnErrorListener { mp, what, extra ->
        mp.release()
        true // Indicates we handled the error
    }
}

suspend fun playAudioFromRawResource(context: Context, resourceId: Int) = suspendCancellableCoroutine<Unit> { continuation ->
    val mediaPlayer = MediaPlayer.create(context, resourceId)
    mediaPlayer?.start() // Starts the playback.

    // Set a listener to resume the coroutine once the playback is complete
    mediaPlayer?.setOnCompletionListener { mp ->
        mp.release() // Release the resources used by the media player
        continuation.resume(Unit) // Resume the coroutine after playback is complete
    }

    mediaPlayer?.setOnErrorListener { mp, what, extra ->
        mp.release()
        continuation.resume(Unit) // Resume the coroutine even if there is an error
        true // Indicates we handled the error
    }

    // Handle cancellation of the coroutine
    continuation.invokeOnCancellation {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}