package com.dev.earalarm.feature.setting

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
internal fun SettingScreen(
    paddingValues: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    popBackStack: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel()
) {

    val settingUiState by settingViewModel.settingUiState.collectAsStateWithLifecycle()

    SettingContent(
        settingUiState = settingUiState,
        paddingValues = paddingValues,
        popBackStack = popBackStack,
        setVolume = settingViewModel::setVolume,
        setAlarmSound = settingViewModel::setAlarmSound
    )
}

@Composable
private fun SettingContent(
    settingUiState: SettingUiState,
    paddingValues: PaddingValues,
    popBackStack: () -> Unit,
    setVolume: (Int) -> Unit,
    setAlarmSound: (String) -> Unit,
) {
    val context = LocalContext.current

    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                it.toPath(context, settingUiState.alarmMedia)
                    ?.let { path -> setAlarmSound(path) }
            }
        }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                text = stringResource(R.string.setting_alarm_text),
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        filePickerLauncher.launch("audio/*")
                    }
                    .padding(Paddings.large)
                    .padding(horizontal = Paddings.medium)
            ) {
                Text(
                    text = stringResource(id = R.string.setting_alarm_media),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EarAlarmMaterialTheme.colorScheme.textPrimary
                    )
                )
                Text(
                    modifier = Modifier.padding(top = Paddings.large),
                    text = if (settingUiState.alarmMedia != null) {
                        settingUiState.alarmMedia.name
                    } else {
                        stringResource(id = R.string.setting_default_sound)
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EarAlarmMaterialTheme.colorScheme.textPrimary
                    )
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Paddings.large)
                    .padding(horizontal = Paddings.medium)
            ) {
                Text(
                    modifier = Modifier.padding(top = Paddings.large),
                    text = stringResource(
                        id = R.string.setting_alarm_volume,
                        settingUiState.volume
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EarAlarmMaterialTheme.colorScheme.textPrimary
                    )
                )

                Slider(
                    value = settingUiState.volume.toFloat() / 100,
                    onValueChange = { newVolume ->
                        setVolume((newVolume * 100).toInt())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = EarAlarmMaterialTheme.colorScheme.active,
                        activeTrackColor = EarAlarmMaterialTheme.colorScheme.active,
                        inactiveTrackColor = EarAlarmMaterialTheme.colorScheme.inactive
                    )
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingContentPreview() {
    EarAlarmTheme {
        SettingContent(
            settingUiState = SettingUiState(),
            paddingValues = PaddingValues(),
            popBackStack = {},
            setVolume = {},
            setAlarmSound = {}
        )
    }
}