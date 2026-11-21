package com.pakoogram.zero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pakoogram.zero.engine.DreamConfig
import com.pakoogram.zero.ui.PakoogramApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DreamConfig.load(this)
        setContent {
            PakoogramApp()
        }
    }
}