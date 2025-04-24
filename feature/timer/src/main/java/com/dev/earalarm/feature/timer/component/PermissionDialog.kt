package com.dev.earalarm.feature.timer.component


import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.dev.earalarm.core.designsystem.theme.EarAlarmMaterialTheme
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.earalarm.feature.timer.R

@Composable
fun PermissionDialog(
    @StringRes titleId: Int? = null,
    @StringRes contentTextId: Int? = null,
    @StringRes confirmButtonTextId: Int? = null,
    @StringRes dismissButtonTextId: Int? = null,
    titleText: String = "",
    contentText: String = "",
    confirmButtonText: String = "",
    dismissButtonText: String = "",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = EarAlarmMaterialTheme.colorScheme.textPrimary
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = Paddings.xlarge, horizontal = Paddings.extra)
            ) {
                Text(
                    text = titleId?.let { stringResource(id = titleId) } ?: titleText,
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(modifier = Modifier.size(Paddings.large))

                Text(
                    text = contentTextId?.let { stringResource(id = contentTextId) } ?: contentText,
                    style = MaterialTheme.typography.labelSmall,
                )

                Spacer(modifier = Modifier.size(Paddings.large))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = dismissButtonTextId?.let { stringResource(id = dismissButtonTextId) }
                            ?: dismissButtonText,
                        color = EarAlarmMaterialTheme.colorScheme.inactive,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .padding(end = Paddings.extra)
                            .clickable { onDismiss() }
                    )

                    Text(
                        text = confirmButtonTextId?.let { stringResource(id = confirmButtonTextId) }
                            ?: confirmButtonText,
                        color = EarAlarmMaterialTheme.colorScheme.active,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.clickable { onConfirm() }
                    )
                }
            }
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PermissionDialogPreview() {
    EarAlarmTheme {
        PermissionDialog(
            titleId = R.string.permission_exact_alarm_title_request,
            contentTextId = R.string.permission_exact_alarm_message_request,
            confirmButtonTextId = R.string.permission_positive,
            dismissButtonTextId = R.string.permission_negative,
            onDismiss = {},
            onConfirm = {}
        )
    }
}