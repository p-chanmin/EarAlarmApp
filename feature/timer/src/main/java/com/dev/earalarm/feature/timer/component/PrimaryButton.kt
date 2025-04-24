package com.dev.earalarm.feature.timer.component

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dev.earalarm.core.designsystem.theme.EarAlarmMaterialTheme
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.earalarm.core.designsystem.theme.Paddings

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    @StringRes id: Int? = null,
    text: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = EarAlarmMaterialTheme.colorScheme.primaryButton,
            contentColor = EarAlarmMaterialTheme.colorScheme.textInverted,
            disabledContainerColor = EarAlarmMaterialTheme.colorScheme.primaryButtonDisabled,
            disabledContentColor = EarAlarmMaterialTheme.colorScheme.textInverted
        ),
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = Paddings.large)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = id?.let { stringResource(id = id) } ?: text,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PrimaryButtonPreview() {
    EarAlarmTheme {
        PrimaryButton(text = "+1 hour") {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PrimaryButtonDisabledPreview() {
    EarAlarmTheme {
        PrimaryButton(
            text = "+1 hour",
            enabled = false
        ) {}
    }
}