package org.tessarchy.tae.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.tessarchy.tae.launcher.MetroTheme

data class MetroPalette(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val surface: Color,
    val text: Color,
    val muted: Color,
    val mint: Color,
    val tessarchy: Color,
    val calendar: Color,
    val phone: Color,
    val messages: Color,
    val tessa: Color,
    val photos: Color,
    val music: Color,
    val apps: Color,
    val tileText: Color = Color.White,
    val lightSystemBars: Boolean = false,
) {
    val background: Brush
        get() = Brush.verticalGradient(listOf(backgroundTop, backgroundBottom))

    companion object {
        fun forTheme(theme: MetroTheme): MetroPalette = when (theme) {
            MetroTheme.Midnight -> MetroPalette(
                backgroundTop = Color(0xFF101C2B),
                backgroundBottom = Color(0xFF0B1724),
                surface = Color(0xFF1A3044),
                text = Color(0xFFF2FBFF),
                muted = Color(0xFFA6BFCA),
                mint = Color(0xFF8CE1C9),
                tessarchy = Color(0xFF008272),
                calendar = Color(0xFF6B69D6),
                phone = Color(0xFFD13438),
                messages = Color(0xFF0078D4),
                tessa = Color(0xFFCA5010),
                photos = Color(0xFF5C2D91),
                music = Color(0xFFC239B3),
                apps = Color(0xFF0063B1),
            )

            MetroTheme.Daylight -> MetroPalette(
                backgroundTop = Color(0xFFF4F4F0),
                backgroundBottom = Color(0xFFE6ECEB),
                surface = Color(0xFFFFFFFF),
                text = Color(0xFF14283B),
                muted = Color(0xFF506674),
                mint = Color(0xFF006B64),
                tessarchy = Color(0xFF008272),
                calendar = Color(0xFF625FC2),
                phone = Color(0xFFC52D34),
                messages = Color(0xFF0070C8),
                tessa = Color(0xFFC94A10),
                photos = Color(0xFF572982),
                music = Color(0xFFAC278E),
                apps = Color(0xFF005BAA),
                lightSystemBars = true,
            )

            MetroTheme.Cobalt -> MetroPalette(
                backgroundTop = Color(0xFF071B34),
                backgroundBottom = Color(0xFF051226),
                surface = Color(0xFF112D4A),
                text = Color(0xFFF3F8FF),
                muted = Color(0xFFAFBED1),
                mint = Color(0xFF85E7D1),
                tessarchy = Color(0xFF087F88),
                calendar = Color(0xFF1D5BBF),
                phone = Color(0xFFC7373C),
                messages = Color(0xFF0877CC),
                tessa = Color(0xFFE75C21),
                photos = Color(0xFF65339B),
                music = Color(0xFFB72E9D),
                apps = Color(0xFF1475CE),
            )
        }
    }
}

object TessarchySpacing {
    const val TileGap = 10
    const val PageGutter = 18
    const val TileRadius = 8
}

object TessarchyTypeScale {
    const val Clock = 62
    const val Date = 11
    const val TileTitle = 14
    const val Body = 15
    const val Caption = 10
}
