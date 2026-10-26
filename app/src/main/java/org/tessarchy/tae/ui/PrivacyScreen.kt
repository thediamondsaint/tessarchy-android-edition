package org.tessarchy.tae.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tessarchy.tae.R
import org.tessarchy.tae.ui.theme.MetroPalette

@Composable
fun PrivacyScreen(
    palette: MetroPalette,
    hasInventoryConsent: Boolean,
    onBack: () -> Unit,
    onInventoryConsentChanged: (Boolean) -> Unit,
    onRequestInventoryConsent: () -> Unit,
) {
    val context = LocalContext.current
    val policyUrl = stringResource(R.string.privacy_policy_url)
    val backDescription = stringResource(R.string.back_to_home)
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp).semantics { contentDescription = backDescription },
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(palette.mint),
                    modifier = Modifier.size(22.dp),
                )
            }
            Text(stringResource(R.string.privacy_title), color = palette.text, fontSize = 27.sp, fontWeight = FontWeight.Light)
        }

        Text(
            text = stringResource(R.string.privacy_intro),
            color = palette.muted,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 20.dp),
        )

        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(palette.surface).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(stringResource(R.string.app_inventory_title), color = palette.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.app_inventory_privacy_detail), color = palette.muted, fontSize = 12.sp, lineHeight = 18.sp)
            if (hasInventoryConsent) {
                TextButton(onClick = { onInventoryConsentChanged(false) }, modifier = Modifier.align(Alignment.End)) {
                    Text(stringResource(R.string.app_inventory_turn_off), color = palette.mint)
                }
            } else {
                Button(onClick = onRequestInventoryConsent, modifier = Modifier.align(Alignment.End)) {
                    Text(stringResource(R.string.app_inventory_review))
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(16.dp)).background(palette.surface).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.local_data_title), color = palette.text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(stringResource(R.string.local_data_detail), color = palette.muted, fontSize = 12.sp, lineHeight = 18.sp)
        }

        TextButton(
            onClick = {
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, policyUrl.toUri())) }
            },
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text(stringResource(R.string.open_privacy_policy), color = palette.mint)
        }
        Spacer(Modifier.padding(bottom = 24.dp))
    }
}
