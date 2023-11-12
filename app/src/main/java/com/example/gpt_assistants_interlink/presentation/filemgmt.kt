// This will manage the writing and reading of files on the device
package com.example.gpt_assistants_interlink.presentation

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.FileInputStream


@Throws(IOException::class)
fun writeTextToFile(context: Context, fileName: String, text: String, isPublic: Boolean = false) {
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
fun readTextFromFile(context: Context, fileName: String, isPublic: Boolean = false): String {
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