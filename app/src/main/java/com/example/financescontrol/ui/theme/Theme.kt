package com.example.financescontrol.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//  usadas para a finança
val FinanceGreen = Color(0xFF4CAF50)  // positivo
val FinanceRed = Color(0xFFF44336)    // negativo
val FinanceBlue = Color(0xFF2196F3)
val FinanceDarkBlue = Color(0xFF1565C0)
val FinanceLightGray = Color(0xFFF5F5F5)


private val LightFinanceColorScheme = lightColorScheme(
    primary = FinanceBlue,
    onPrimary = Color.White,
    primaryContainer = FinanceDarkBlue,
    onPrimaryContainer = Color.White,

    secondary = FinanceGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF1B5E20),

    tertiary = Color(0xFF7E57C2),
    onTertiary = Color.White,

    background = FinanceLightGray,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,

    error = FinanceRed,
    onError = Color.White
)


private val DarkFinanceColorScheme = darkColorScheme(
    primary = FinanceBlue,
    onPrimary = Color.White,
    primaryContainer = FinanceDarkBlue,
    onPrimaryContainer = Color.White,

    secondary = FinanceGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1B5E20),
    onSecondaryContainer = Color(0xFFA5D6A7),

    tertiary = Color(0xFFB39DDB),
    onTertiary = Color.Black,

    background = Color(0xFF121212),
    onBackground = Color.White,

    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,

    error = Color(0xFFCF6679),
    onError = Color.Black
)


data class ExtendedColors(
    val positive: Color,
    val negative: Color,
    val warning: Color,
    val cardBackground: Color
)


val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        positive = Color.Unspecified,
        negative = Color.Unspecified,
        warning = Color.Unspecified,
        cardBackground = Color.Unspecified
    )
}


private val lightExtendedColors = ExtendedColors(
    positive = FinanceGreen,
    negative = FinanceRed,
    warning = Color(0xFFFFA000),
    cardBackground = Color.White
)


private val darkExtendedColors = ExtendedColors(
    positive = FinanceGreen,
    negative = FinanceRed,
    warning = Color(0xFFFFB74D),
    cardBackground = Color(0xFF2D2D2D)
)

@Composable
fun FinancesControlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkFinanceColorScheme
        else -> LightFinanceColorScheme
    }

    val extendedColors = if (darkTheme) darkExtendedColors else lightExtendedColors

    // barra de status
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FinanceTypography,
            shapes = FinanceShapes,
            content = content
        )
    }
}


object FinanceTheme {
    val extendedColors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}