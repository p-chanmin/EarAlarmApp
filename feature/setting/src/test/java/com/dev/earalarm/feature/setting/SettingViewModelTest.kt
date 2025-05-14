package com.dev.earalarm.feature.setting

import app.cash.turbine.test
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.feature.setting.model.SettingUiState
import com.dev.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.File


internal class SettingViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val timerRepository: TimerRepository = mockk(relaxed = true)
    private lateinit var settingViewModel: SettingViewModel

    @Test
    fun `설정값을 확인할 수 있다`() = runTest {

        // Given
        val alarmVolume = 100
        val media = "/files/test.m4a"
        val vibrate = false

        coEvery { timerRepository.alarmVolume } returns flowOf(alarmVolume)
        coEvery { timerRepository.media } returns flowOf(File(media))
        coEvery { timerRepository.vibrate } returns flowOf(vibrate)

        settingViewModel = SettingViewModel(timerRepository)

        // When
        settingViewModel.settingUiState.test {

            // Then
            val uiState = awaitItem()
            assertEquals(
                SettingUiState(
                    volume = alarmVolume,
                    alarmMedia = File(media),
                    vibrate = vibrate
                ),
                uiState
            )
        }
    }

    @Test
    fun `알람 사운드 설정값을 변경할 수 있다`() = runTest {

        // Given
        val media = "/files/test.m4a"
        val flow = MutableStateFlow<File?>(null)

        coEvery { timerRepository.alarmVolume } returns flowOf(80)
        coEvery { timerRepository.media } returns flow
        coEvery { timerRepository.vibrate } returns flowOf(true)

        coEvery { timerRepository.setMediaPath(media) } answers {
            flow.value = File(media)
        }

        settingViewModel = SettingViewModel(timerRepository)

        // When
        settingViewModel.setAlarmSound(media)

        // Then
        settingViewModel.settingUiState.test {
            val uiState = awaitItem()
            assertEquals(File(media), uiState.alarmMedia)
        }
    }

    @Test
    fun `알람 볼륨 설정값을 변경할 수 있다`() = runTest {

        // Given
        val alarmVolume = 40
        val flow = MutableStateFlow(TimerRepository.DEFAULT_VOLUME_SIZE)

        coEvery { timerRepository.alarmVolume } returns flow
        coEvery { timerRepository.media } returns flowOf(null)
        coEvery { timerRepository.vibrate } returns flowOf(true)

        coEvery { timerRepository.setAlarmVolume(alarmVolume) } answers {
            flow.value = alarmVolume
        }

        settingViewModel = SettingViewModel(timerRepository)

        // When
        settingViewModel.setVolume(alarmVolume)

        // Then
        settingViewModel.settingUiState.test {
            val uiState = awaitItem()
            assertEquals(alarmVolume, uiState.volume)
        }
    }

    @Test
    fun `알람 진동 설정값을 변경할 수 있다`() = runTest {

        // Given
        val vibrate = false
        val flow = MutableStateFlow(true)

        coEvery { timerRepository.alarmVolume } returns flowOf(80)
        coEvery { timerRepository.media } returns flowOf(null)
        coEvery { timerRepository.vibrate } returns flow

        coEvery { timerRepository.setVibrate(vibrate) } answers {
            flow.value = vibrate
        }

        settingViewModel = SettingViewModel(timerRepository)

        // When
        settingViewModel.setVibrate(vibrate)

        // Then
        settingViewModel.settingUiState.test {
            val uiState = awaitItem()
            assertEquals(vibrate, uiState.vibrate)
        }
    }
}