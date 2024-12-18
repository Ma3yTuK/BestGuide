package com.example.bestguide

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.bestguide.navigation.AuthNavigation
import com.example.bestguide.ui.theme.BestGuideTheme
import com.example.staticaccess.StaticPropertyController

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val staticPropertyController = StaticPropertyController(application.applicationContext)
        enableEdgeToEdge()
        setContent {
            BestGuideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthNavigation(
                        staticPropertyController::readToken,
                        staticPropertyController::writeToken,
                        staticPropertyController::deleteToken,
                        goHome = {
                            startActivity(Intent(this@AuthActivity, HomeActivity::class.java))
                            finish()
                                 },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}