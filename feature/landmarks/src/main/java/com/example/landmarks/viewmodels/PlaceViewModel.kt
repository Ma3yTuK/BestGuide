package com.example.landmarks.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.AchievementPartModel
import com.example.data.repositories.AchievementRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaceViewModel : ViewModel() {
    val fetchedAchievementParts = mutableStateListOf<AchievementPartModel>()
    private var fetchJob: Job? = null

    fun fetchAchievementsPart(placeId: String) {
        fetchJob?.cancel()
        fetchedAchievementParts.clear()
        fetchJob = viewModelScope.launch {
            try {
                fetchedAchievementParts.addAll(AchievementRepository.getAchievementPartsByPlace(placeId))
            } catch (e: Exception) {
                // Do nothing
            }
        }
    }
}