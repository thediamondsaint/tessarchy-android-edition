package org.tessarchy.tae.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tessarchy.tae.R
import org.tessarchy.tae.ui.theme.MetroPalette

@Composable
fun TessaPreviewScreen(
    palette: MetroPalette,
    onBack: () -> Unit,
    onPersonalize: () -> Unit,
    onApps: () -> Unit,
) {
    val backDescription = stringResource(R.string.back_to_home)
    val cardShape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp).semantics { contentDescription = backDescription },
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(palette.mint),
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(5.dp))
            Text("TESSARCHY / TESSA", color = palette.text, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(Modifier.weight(1f))
            Box(Modifier.clip(RoundedCornerShape(99.dp)).background(palette.mint.copy(alpha = 0.12f)).padding(horizontal = 9.dp, vertical = 6.dp)) {
                Text("CONCEPT", color = palette.mint, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(190.dp).clip(cardShape)
                .background(Brush.linearGradient(listOf(Color(0xFF15354A), Color(0xFF17223F), Color(0xFF26345A))))
                .border(1.dp, Color.White.copy(alpha = 0.16f), cardShape),
        ) {
            Box(Modifier.align(Alignment.CenterEnd).padding(end = 32.dp).size(116.dp).clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFFB8FFF0), Color(0xFF47C7CF), Color(0xFF855FF6).copy(alpha = 0.35f), Color.Transparent)))
            )
            Box(Modifier.align(Alignment.CenterEnd).padding(end = 68.dp).size(44.dp).clip(CircleShape)
                .background(Color.White.copy(alpha = 0.17f)), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.tessarchy_mark), null, modifier = Modifier.size(29.dp))
            }
            Column(Modifier.align(Alignment.CenterStart).padding(start = 20.dp)) {
                Text("YOUR TESSARCHY AGENT", color = palette.mint, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                Text("Tessa", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Light, letterSpacing = (-1).sp)
                Text("A presence across your phone.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            }
            Text("01 / FUTURE INTERFACE", color = Color.White.copy(alpha = 0.55f), fontSize = 7.sp, letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 13.dp))
        }

        Text("One assistant.\nYour whole Tessarchy.", color = palette.text, fontSize = 28.sp, lineHeight = 31.sp,
            fontWeight = FontWeight.Light, letterSpacing = (-0.8).sp, modifier = Modifier.padding(top = 22.dp))
        Text("Choose the agent that feels right to you.", color = palette.muted, fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp))

        Text("PROVIDER OPTIONS / NOT CONNECTED", color = palette.muted, fontSize = 8.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, modifier = Modifier.padding(top = 20.dp, bottom = 9.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("ChatGPT", "Claude", "Grok", "Other").forEach { provider ->
                Box(Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .background(palette.surface.copy(alpha = 0.78f))
                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                    .padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                    Text(provider, color = palette.text, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 19.dp).clip(RoundedCornerShape(18.dp))
                .background(palette.surface.copy(alpha = 0.70f)).padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Text("A GLIMPSE OF THE INTENDED FLOW", color = palette.mint, fontSize = 8.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
            Box(Modifier.align(Alignment.End).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.09f)).padding(horizontal = 12.dp, vertical = 9.dp)) {
                Text("Make the home screen feel more like me.", color = palette.text, fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(Modifier.size(24.dp).clip(CircleShape).background(Brush.radialGradient(listOf(palette.mint, palette.calendar))), contentAlignment = Alignment.Center) {
                    Image(painterResource(R.drawable.tessarchy_mark), null, modifier = Modifier.size(15.dp))
                }
                Text("Tessa could reshape tiles, change settings, and help you reach apps from one conversation.",
                    color = palette.text.copy(alpha = 0.94f), fontSize = 10.sp, lineHeight = 15.sp,
                    modifier = Modifier.weight(1f))
            }
        }
        Text(stringResource(R.string.tessa_preview_note), color = palette.muted, fontSize = 10.sp, lineHeight = 15.sp,
            modifier = Modifier.padding(top = 10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 20.dp)) {
            Button(onClick = onPersonalize, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.open_personalize), fontSize = 11.sp)
            }
            OutlinedButton(onClick = onApps, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.open_drawer), fontSize = 11.sp)
            }
        }
    }
}
