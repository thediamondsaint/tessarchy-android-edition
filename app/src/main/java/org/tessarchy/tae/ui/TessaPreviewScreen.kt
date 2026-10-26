package org.tessarchy.tae.ui

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 12.dp)) {
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
            Text(
                stringResource(R.string.tessa_preview_title),
                color = palette.text,
                fontSize = 25.sp,
                fontWeight = FontWeight.Light,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.tessarchy_mark),
                contentDescription = stringResource(R.string.tessarchy_mark_description),
                modifier = Modifier
                    .size(112.dp)
                    .background(palette.tessa, androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                    .padding(17.dp),
            )
            Text(
                stringResource(R.string.tessa_preview_title),
                color = palette.text,
                fontSize = 29.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(top = 24.dp),
            )
            Text(
                stringResource(R.string.tessa_preview_body),
                color = palette.muted,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                stringResource(R.string.tessa_preview_note),
                color = palette.mint,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 19.dp),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onPersonalize, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.open_personalize))
            }
            OutlinedButton(onClick = onApps, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.open_drawer))
            }
        }
    }
}
