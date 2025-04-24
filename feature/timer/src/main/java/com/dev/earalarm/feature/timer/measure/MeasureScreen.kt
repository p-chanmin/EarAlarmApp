package com.dev.earalarm.feature.timer.measure

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.core.admob.FakeAdMobManager
import com.dev.core.admob.LocalAdMobManager
import com.dev.earalarm.core.designsystem.theme.EarAlarmMaterialTheme
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.earalarm.feature.timer.R
import com.dev.earalarm.feature.timer.component.PrimaryButton
import com.dev.earalarm.feature.timer.model.MeasureUiState

@Composable
internal fun MeasureScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    measureViewModel: MeasureViewModel = hiltViewModel()
) {
    val measureUiState by measureViewModel.measureUiState.collectAsStateWithLifecycle()

    MeasureContent(
        measureUiState = measureUiState,
        paddingValues = paddingValues,
        dismissTimerAlarm = measureViewModel::dismissTimerAlarm,
    )
}

@Composable
private fun MeasureContent(
    measureUiState: MeasureUiState,
    paddingValues: PaddingValues,
    dismissTimerAlarm: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        val adMobManager = LocalAdMobManager.current
        val context = LocalContext.current

        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp
        val screenHeightDp = configuration.screenHeightDp

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                if (screenWidthDp < screenHeightDp) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(screenWidthDp.dp)
                            .height(screenWidthDp.dp)
                            .padding(Paddings.xlarge),
                        progress = { measureUiState.progress },
                        color = if (measureUiState.progress >= 0.9f) {
                            EarAlarmMaterialTheme.colorScheme.warning
                        } else {
                            EarAlarmMaterialTheme.colorScheme.active
                        },
                        trackColor = EarAlarmMaterialTheme.colorScheme.inactive,
                        strokeWidth = 10.dp
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = measureUiState.leftTime,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textPrimary
                        )
                    )

                    Text(
                        modifier = Modifier.padding(vertical = Paddings.medium),
                        text = stringResource(
                            id = R.string.feature_timer_alarm_info_minute,
                            measureUiState.minute
                        ),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        modifier = Modifier.padding(vertical = Paddings.medium),
                        text = stringResource(
                            id = R.string.feature_timer_info_end_time,
                            measureUiState.endTimeString
                        ),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = EarAlarmMaterialTheme.colorScheme.textPrimary
                        )
                    )
                }
            }
            PrimaryButton(
                modifier = Modifier.padding(top = Paddings.large),
                id = R.string.feature_timer_dismiss_alarm,
                onClick = {
                    adMobManager.showInterstitialAlarmAd(context as Activity)
                    dismissTimerAlarm()
                }
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, locale = "ko")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, locale = "ko")
@Composable
private fun MeasureContentPreview() {
    EarAlarmTheme {
        CompositionLocalProvider(
            LocalAdMobManager provides FakeAdMobManager()
        ) {
            MeasureContent(
                measureUiState = MeasureUiState(
                    progress = 0.5f
                ),
                paddingValues = PaddingValues(),
                dismissTimerAlarm = {},
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true, locale = "ko")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, locale = "ko")
@Composable
private fun MeasureContentWarningPreview() {
    EarAlarmTheme {
        CompositionLocalProvider(
            LocalAdMobManager provides FakeAdMobManager()
        ) {
            MeasureContent(
                measureUiState = MeasureUiState(
                    progress = 0.9f
                ),
                paddingValues = PaddingValues(),
                dismissTimerAlarm = {},
            )
        }
    }
}
