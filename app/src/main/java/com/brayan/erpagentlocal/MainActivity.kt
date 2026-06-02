package com.brayan.erpagentlocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import com.brayan.erpagentlocal.ui.screens.ChatScreen
import com.brayan.erpagentlocal.ui.theme.ErpColors

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = erpDarkColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ErpColors.Background
                ) {
                    ChatScreen()
                }
            }
        }
    }
}

private fun erpDarkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = ErpColors.Primary,
        onPrimary = ErpColors.TextPrimary,
        background = ErpColors.Background,
        onBackground = ErpColors.TextPrimary,
        surface = ErpColors.Surface,
        onSurface = ErpColors.TextPrimary,
        secondary = ErpColors.Success,
        onSecondary = ErpColors.TextPrimary,
        error = ErpColors.Error,
        onError = ErpColors.TextPrimary
    )
}