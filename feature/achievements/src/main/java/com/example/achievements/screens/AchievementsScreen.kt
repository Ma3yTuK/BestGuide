package com.example.achievements.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import com.example.data.models.AchievementModel
import com.example.data.models.AchievementPartModel
import com.example.data.repositories.AchievementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AchievementsRoute() {
    AchievementScreen()
}

@Composable
fun AchievementScreen() {
    var achievementsCompleted by remember { mutableStateOf<List<AchievementModel>>(emptyList()) }
    var achievementsStarted by remember { mutableStateOf<List<AchievementModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                achievementsCompleted = AchievementRepository.getAchievementsCompleted()
                achievementsStarted = AchievementRepository.getAchievementsStarted()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        AchievementContent(
            achievementsCompleted = achievementsCompleted,
            achievementsStarted = achievementsStarted
        )
    }
}

@Composable
fun AchievementContent(
    achievementsCompleted: List<AchievementModel>,
    achievementsStarted: List<AchievementModel>
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(
                text = "Completed Achievements",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(achievementsCompleted.size) { index ->
            AchievementItem(
                achievement = achievementsCompleted[index]
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Started Achievements",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(achievementsStarted.size) { index ->
            AchievementItem(
                achievement = achievementsStarted[index]
            )
        }
    }
}

@Composable
fun AchievementItem(achievement: AchievementModel) {
    var isExpanded by remember { mutableStateOf(false) }
    var partsCompleted by remember { mutableStateOf<List<AchievementPartModel>>(emptyList()) }
    var partsNotCompleted by remember { mutableStateOf<List<AchievementPartModel>>(emptyList()) }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            withContext(Dispatchers.IO) {
                try {
                    partsCompleted = AchievementRepository.getAchievementPartsCompletedByAchievement(achievement.id!!)
                    partsNotCompleted = AchievementRepository.getAchievementPartsNotCompletedByAchievement(achievement.id!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }) {
        Text(text = achievement.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))

        if (isExpanded) {
            Text("Completed Tasks:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp))
            partsCompleted.forEach { part ->
                Text("- ${part.name}", modifier = Modifier.padding(start = 32.dp))
            }

            Text("Not Completed Tasks:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
            partsNotCompleted.forEach { part ->
                Text("- ${part.name}", modifier = Modifier.padding(start = 32.dp))
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}
