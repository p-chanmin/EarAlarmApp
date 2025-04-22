package com.dev.earalarm.feature.timer

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
internal fun TimerScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (message: String) -> Unit,
    navigateToSetting: () -> Unit,
) {

//    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()

    TimerContent(
        navigateToSetting = navigateToSetting
    )
}

@Composable
private fun TimerContent(
    navigateToSetting: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = navigateToSetting
        ) {
            Text(
                text = "Timer Screen\nnavigate to setting"
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TimerContentPreview() {
    EarAlarmTheme {
        TimerContent(
            navigateToSetting = {}
        )
    }
}