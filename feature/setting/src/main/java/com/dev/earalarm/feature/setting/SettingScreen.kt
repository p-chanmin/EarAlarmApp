package com.dev.earalarm.feature.setting

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.earalarm.core.designsystem.theme.EarAlarmMaterialTheme
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.earalarm.feature.setting.model.SettingUiState
import com.dev.earalarm.feature.setting.utils.toPath
import com.dev.firebase.LocalFirebaseManager
import com.dev.firebase.model.FA
import com.google.firebase.analytics.logEvent
import java.io.File

@Composable
internal fun SettingScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    popBackStack: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel()
) {
    val settingUiState by settingViewModel.settingUiState.collectAsStateWithLifecycle()
    val firebaseManager = LocalFirebaseManager.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        firebaseManager.screenLogEvent("SettingScreen", configuration.orientation)
        settingViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    SettingContent(
        settingUiState = settingUiState,
        paddingValues = paddingValues,
        popBackStack = popBackStack,
        setVolume = settingViewModel::setVolume,
        setAlarmSound = settingViewModel::setAlarmSound,
        setVibrate = settingViewModel::setVibrate,
    )
}

@Composable
private fun SettingContent(
    settingUiState: SettingUiState,
    paddingValues: PaddingValues,
    popBackStack: () -> Unit,
    setVolume: (Int) -> Unit,
    setAlarmSound: (String) -> Unit,
    setVibrate: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val scrollState: ScrollState = rememberScrollState()
    val firebaseManager = LocalFirebaseManager.current

    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                it.toPath(context, settingUiState.alarmMedia)
                    ?.let { path ->
                        firebaseManager.firebaseAnalytics.logEvent(FA.Event.SETTING_SOUND_CHANGE) {
                            param(FA.Param.Key.MEDIA, FA.Param.Value.CUSTOM)
                        }
                        setAlarmSound(path)
                    }
            }
        }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = popBackStack,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    tint = EarAlarmMaterialTheme.colorScheme.textPrimary
                )
            }
            Text(
                text = stringResource(R.string.feature_setting_alarm_text),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EarAlarmMaterialTheme.colorScheme.textPrimary
                )
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.primary)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AlarmSoundSetting(
                launchFilePicker = { filePickerLauncher.launch("audio/*") },
                alarmMedia = settingUiState.alarmMedia
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            VolumeSetting(
                volume = settingUiState.volume,
                setVolume = setVolume
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            VibrateSetting(
                vibrate = settingUiState.vibrate,
                setVibrate = setVibrate
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            PrivacyPolicy()
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun AlarmSoundSetting(
    launchFilePicker: () -> Unit,
    alarmMedia: File?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { launchFilePicker() }
            .padding(Paddings.large)
            .padding(horizontal = Paddings.medium, vertical = Paddings.medium)
    ) {
        Text(
            text = stringResource(id = R.string.feature_setting_alarm_media),
            style = MaterialTheme.typography.labelSmall.copy(
                color = EarAlarmMaterialTheme.colorScheme.textPrimary
            )
        )
        Text(
            modifier = Modifier.padding(top = Paddings.large),
            text = if (alarmMedia != null) {
                alarmMedia.name
            } else {
                stringResource(id = R.string.feature_setting_default_sound)
            },
            style = MaterialTheme.typography.labelSmall.copy(
                color = EarAlarmMaterialTheme.colorScheme.textPrimary
            )
        )
    }
}

@Composable
private fun VolumeSetting(
    volume: Int,
    setVolume: (Int) -> Unit,
) {
    val firebaseManager = LocalFirebaseManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Paddings.large)
            .padding(horizontal = Paddings.medium)
    ) {
        Text(
            modifier = Modifier.padding(top = Paddings.large),
            text = stringResource(
                id = R.string.feature_setting_alarm_volume,
                volume
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                color = EarAlarmMaterialTheme.colorScheme.textPrimary
            )
        )

        Slider(
            modifier = Modifier.padding(top = Paddings.large),
            value = volume.toFloat() / 100,
            onValueChange = { newVolume ->
                setVolume((newVolume * 100).toInt())
            },
            colors = SliderDefaults.colors(
                thumbColor = EarAlarmMaterialTheme.colorScheme.active,
                activeTrackColor = EarAlarmMaterialTheme.colorScheme.active,
                inactiveTrackColor = EarAlarmMaterialTheme.colorScheme.inactive
            ),
            onValueChangeFinished = {
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.SETTING_VOLUME_CHANGE) {
                    param(FA.Param.Key.VOLUME, volume.toLong())
                }
            },
        )
    }
}

@Composable
private fun VibrateSetting(
    vibrate: Boolean,
    setVibrate: (Boolean) -> Unit,
) {
    val firebaseManager = LocalFirebaseManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { setVibrate(!vibrate) }
            .padding(Paddings.large)
            .padding(horizontal = Paddings.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.feature_setting_vibrate),
            style = MaterialTheme.typography.labelSmall.copy(
                color = EarAlarmMaterialTheme.colorScheme.textPrimary
            )
        )
        Switch(
            checked = vibrate,
            onCheckedChange = {
                firebaseManager.firebaseAnalytics.logEvent(FA.Event.SETTING_VIBRATE_CHANGE) {
                    param(FA.Param.Key.VIBRATE, (!vibrate).toString())
                }
                setVibrate(!vibrate)
            },
            colors = SwitchDefaults.colors().copy(
                uncheckedThumbColor = EarAlarmMaterialTheme.colorScheme.primaryButton,
                uncheckedBorderColor = EarAlarmMaterialTheme.colorScheme.inactive,
                uncheckedTrackColor = EarAlarmMaterialTheme.colorScheme.inactive,
                checkedThumbColor = EarAlarmMaterialTheme.colorScheme.primaryButton,
                checkedBorderColor = EarAlarmMaterialTheme.colorScheme.active,
                checkedTrackColor = EarAlarmMaterialTheme.colorScheme.active,
            )
        )
    }
}

@Composable
private fun PrivacyPolicy() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://sites.google.com/view/oldogz7358-earalarm/%ED%99%88")
                    )
                )
            }
            .padding(Paddings.large)
            .padding(horizontal = Paddings.medium, vertical = Paddings.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.feature_setting_privacy_policy),
            style = MaterialTheme.typography.labelSmall.copy(
                color = EarAlarmMaterialTheme.colorScheme.textPrimary
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingContentPreview() {
    EarAlarmTheme {
        SettingContent(
            settingUiState = SettingUiState(
                vibrate = false
            ),
            paddingValues = PaddingValues(),
            popBackStack = {},
            setVolume = {},
            setAlarmSound = {},
            setVibrate = {}
        )
    }
}