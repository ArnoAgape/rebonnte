package com.openclassrooms.rebonnte.data.service

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Interface defining file-related operations.
 * Implementations handle storage, upload and retrieval logic.
 */
interface Api {

    /** Returns all files ordered by creation date from a specific user. */
    fun getFilesOrderByUser(userId: String): Flow<List<File>>

    /** Uploads a file and returns the list of uploaded URLs. */
    suspend fun sendFile(localUris: List<Uri>, file: File): List<String>

    /** Observes a file by its ID and user ID. */
    fun getFileById(fileId: String, userId: String): Flow<File?>

    /** Uploads a single document to Firebase Storage and returns its URL. */
    suspend fun uploadDocumentToFirebase(uri: Uri): String?
}