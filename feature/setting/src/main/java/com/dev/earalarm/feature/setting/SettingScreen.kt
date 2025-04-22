package com.dev.earalarm.feature.setting

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme

@Composable
internal fun SettingScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (message: String) -> Unit,
    popBackStack: () -> Unit,
) {

//    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()

    SettingContent(
        popBackStack = popBackStack,
    )
}

@Composable
private fun SettingContent(
    popBackStack: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = popBackStack
        ) {
            Text(
                text = "Setting Screen\nnavigate to timer"
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingContentPreview() {
    EarAlarmTheme {
        SettingContent(
            popBackStack = {}
        )
    }
}