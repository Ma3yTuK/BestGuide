package com.example.data.repositories

import com.example.data.Api
import com.example.data.handlers.processError
import com.example.data.models.AchievementModel
import com.example.data.models.AchievementPartModel

object AchievementRepository {
    var getToken: suspend () -> String? = { "" }

    fun initialize(getToken: suspend () -> String?) {
        this.getToken = getToken
    }

    suspend fun getAchievements(longitude: Double, latitude: Double): List<AchievementModel> {
        return processError(Api.achievementService.sendLocationInfo(longitude, latitude, "Bearer ${getToken()}"))
    }

    suspend fun getAchievementPartsByPlace(placeId: String): List<AchievementPartModel> {
        return processError(Api.achievementService.getAchievementPartsByPlace(placeId, "Bearer ${getToken()}"))
    }

    suspend fun getAchievementsCompleted(): List<AchievementModel> {
        return processError(Api.achievementService.getAchievementsCompleted("Bearer ${getToken()}"))
    }

    suspend fun getAchievementsStarted(): List<AchievementModel> {
        return processError(Api.achievementService.getAchievementsNotCompleted("Bearer ${getToken()}"))
    }

    suspend fun getAchievementPartsCompletedByAchievement(achievementId: Long): List<AchievementPartModel> {
        return processError(Api.achievementService.getAchievementPartsCompletedByAchievement(achievementId, "Bearer ${getToken()}"))
    }

    suspend fun getAchievementPartsNotCompletedByAchievement(achievementId: Long): List<AchievementPartModel> {
        return processError(Api.achievementService.getAchievementPartsNotCompletedByAchievement(achievementId, "Bearer ${getToken()}"))
    }
}