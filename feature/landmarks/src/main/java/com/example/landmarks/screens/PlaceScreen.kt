package com.example.landmarks.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.data.models.AchievementPartModel
import com.example.landmarks.viewmodels.PlaceViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

@Composable
fun PlaceRoute(
    placeId: String,
    placesClient: PlacesClient,
    modifier: Modifier = Modifier,
    viewModel: PlaceViewModel = viewModel {
        PlaceViewModel()
    },
) {
    PlaceScreen(
        viewModel,
        placeId,
        placesClient,
        modifier
    )
}

@Composable
fun PlaceScreen(
    viewModel: PlaceViewModel,
    placeId: String,
    placesClient: PlacesClient,
    modifier: Modifier = Modifier
) {
    var placeDetails by remember { mutableStateOf<Place?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(placeId) {
        val request = FetchPlaceRequest.newInstance(
            placeId,
            listOf(
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES
            )
        )
        placesClient
            .fetchPlace(request)
            .addOnSuccessListener { response ->
                placeDetails = response.place
            }
            .addOnFailureListener {
                it.printStackTrace()
                println(it.cause)
                println(it.message)
                errorMessage = it.message
            }

        try {
            viewModel.fetchAchievementsPart(placeId)
        } catch (e: Exception) {
            errorMessage = e.message
            e.printStackTrace()
        }
        isLoading = false
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else if (errorMessage != null) {
        Text(
            text = errorMessage!!,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.error
        )
    } else if (placeDetails != null) {
        val place = placeDetails!!
        val photoMetadata = place.photoMetadatas?.firstOrNull()

        var imageUrl by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(photoMetadata) {
            if (photoMetadata != null) {
                val photoRequest = FetchPhotoRequest.builder(photoMetadata).build()

                placesClient
                    .fetchPhoto(photoRequest)
                    .addOnSuccessListener { response ->
                        imageUrl = response.bitmap
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        println(it.cause)
                        println(it.message)
                    }
            }
        }

        PlaceContent(
            place,
            imageUrl,
            fetchedAchievementParts = viewModel.fetchedAchievementParts,
            modifier
        )
    }
}

@Composable
fun PlaceContent(
    place: Place,
    imageUrl: Bitmap?,
    fetchedAchievementParts: Collection<AchievementPartModel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        imageUrl?.let { url ->
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = "${place.displayName} image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Text(
            text = place.displayName ?: "Unknown Place",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        place.rating?.let { rating ->
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(rating.toInt()) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Yellow
                    )
                }
                Text(
                    text = "(${rating.toInt()})",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Text(
            text = place.formattedAddress ?: "No address available",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (fetchedAchievementParts.isNotEmpty()) {
            Text(
                text = "Achievements:",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            fetchedAchievementParts.forEach { part ->
                Text(
                    text = "- ${part.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        } else {
            Text(
                text = "No achievements available for this place.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
