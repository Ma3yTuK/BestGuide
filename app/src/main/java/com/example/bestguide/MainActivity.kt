package com.example.bestguide

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.staticaccess.StaticPropertyController
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val staticPropertyController = StaticPropertyController(applicationContext)

        runBlocking {
            if (staticPropertyController.readToken() == null) {
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
            } else {
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            }
        }
    }
}