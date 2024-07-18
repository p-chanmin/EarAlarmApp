package kr.ac.tukorea.android.earalarm.ui.components.dialogs


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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import kr.ac.tukorea.android.earalarm.R
import kr.ac.tukorea.android.earalarm.presentation.main.output.AlarmUiState
import kr.ac.tukorea.android.earalarm.ui.theme.EarAlarmTheme
import kr.ac.tukorea.android.earalarm.ui.theme.Paddings

@Composable
fun SettingDialog(
    alarmUiState: State<AlarmUiState>,
    onAlarmSoundSetting: () -> Unit,
    onChangeAlarmVolume: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = { onConfirm() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = Paddings.xlarge, horizontal = Paddings.extra)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.setting_alarm_text),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Spacer(modifier = Modifier.size(Paddings.xlarge))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAlarmSoundSetting() }
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.setting_alarm_media,
                            if (alarmUiState.value.alarmMedia != null) {
                                alarmUiState.value.alarmMedia!!.name
                            } else {
                                stringResource(id = R.string.setting_default_sound)
                            }
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(vertical = Paddings.medium)
                    )
                }

                Text(
                    text = stringResource(
                        id = R.string.setting_alarm_volume,
                        alarmUiState.value.volume
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = Paddings.large)
                )

                Slider(
                    value = alarmUiState.value.volume.toFloat() / 100,
                    onValueChange = { newVolume ->
                        onChangeAlarmVolume((newVolume * 100).toInt())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        activeTrackColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Paddings.medium),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(id = R.string.setting_complete_text),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.clickable { onConfirm() }
                    )
                }
            }
        }
    }
}


@Preview(locale = "ko")
@Composable
fun PreviewSettingDialog() {
    EarAlarmTheme {
        SettingDialog(
            alarmUiState = remember { mutableStateOf(AlarmUiState()) },
            onAlarmSoundSetting = {},
            onChangeAlarmVolume = {},
            onConfirm = {}
        )
    }
}