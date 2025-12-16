package com.openclassrooms.rebonnte.data.service.medicine

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Firebase implementation of [Api].
 * Handles file uploads to Firebase Storage and metadata persistence in Firestore.
 */
class FirebaseApi @Inject constructor(
    @param:ApplicationContext private val context: Context
) : Api {
    override fun getFilesOrderByUser(userId: String): Flow<List<File>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendFile(
        localUris: List<Uri>,
        file: File
    ): List<String> {
        TODO("Not yet implemented")
    }

    override fun getFileById(fileId: String, userId: String): Flow<File?> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadDocumentToFirebase(uri: Uri): String? {
        TODO("Not yet implemented")
    }

}