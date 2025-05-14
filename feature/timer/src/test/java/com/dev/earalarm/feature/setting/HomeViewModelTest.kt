package com.dev.earalarm.feature.setting

import app.cash.turbine.test
import com.dev.earalarm.core.alarm.EarAlarmManager
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.feature.timer.home.HomeViewModel
import com.dev.earalarm.feature.timer.model.PermissionState
import com.dev.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale
import kotlin.test.assertEquals

internal class HomeViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val timerRepository: TimerRepository = mockk(relaxed = true)
    private val earAlarmManager: EarAlarmManager = mockk(relaxed = true)
    private lateinit var homeViewModel: HomeViewModel

    @Test
    fun `설정된 알람의 볼륨과 미디어 파일을 확인할 수 있다`() = runTest {

        // Given
        val alarmVolume = 100
        val media = "/files/test.m4a"

        coEvery { timerRepository.alarmVolume } returns flowOf(alarmVolume)
        coEvery { timerRepository.media } returns flowOf(File(media))

        homeViewModel = HomeViewModel(timerRepository, earAlarmManager)

        // When
        homeViewModel.homeUiState.test {

            // Then
            val uiState = awaitItem()
            assertEquals(alarmVolume, uiState.volume)
            assertEquals(File(media), uiState.alarmMedia)
        }
    }

    @Test
    fun `알림 권한 상태를 반영할 수 있다`() = runTest {

        // Given
        homeViewModel = HomeViewModel(timerRepository, earAlarmManager)

        homeViewModel.homeUiState.test {

            // When
            homeViewModel.updateNotificationPermissionState(PermissionState.GRANTED, false)

            // Then
            var uiState = awaitItem()
            assertEquals(PermissionState.GRANTED, uiState.notificationPermissionState)
            assertEquals(false, uiState.deniedNotificationDialog)

            // When
            homeViewModel.updateNotificationPermissionState(PermissionState.DENIED, true)

            // Then
            uiState = awaitItem()
            assertEquals(PermissionState.DENIED, uiState.notificationPermissionState)
            assertEquals(true, uiState.deniedNotificationDialog)

            // When
            homeViewModel.updateNotificationPermissionState(PermissionState.DENIED, false)

            // Then
            uiState = awaitItem()
            assertEquals(PermissionState.DENIED, uiState.notificationPermissionState)
            assertEquals(false, uiState.deniedNotificationDialog)
        }
    }

    @Test
    fun `타이머의 상태를 반영할 수 있다`() = runTest {

        // Given
        mockkStatic(ZonedDateTime::class)
        mockkStatic(ZoneId::class)

        val fixedDateTime = ZonedDateTime.parse("2025-05-01T00:00:00.000000Z")
        every { ZoneId.systemDefault() } returns ZoneId.of("Asia/Seoul")
        every { ZonedDateTime.now(ZoneOffset.UTC) } returns fixedDateTime
        Locale.setDefault(Locale.US)
        homeViewModel = HomeViewModel(timerRepository, earAlarmManager)

        // When
        val hour = 2
        val minute = 30
        val expectedEstimatedEndTime = "AM 11:30" // +9 hour, + hour, + minute
        homeViewModel.updateTimerAlarm(hour, minute)

        // Then
        homeViewModel.homeUiState.test {
            val uiState = awaitItem()
            assertEquals(hour, uiState.hour)
            assertEquals(minute, uiState.minute)
            assertEquals(expectedEstimatedEndTime, uiState.estimatedEndTime)
        }
    }

    @Test
    fun `정확한 알림 권한이 없는 상태를 반영할 수 있다`() = runTest {

        // Given
        every { earAlarmManager.checkScheduleExactAlarms() } returns false
        homeViewModel = HomeViewModel(timerRepository, earAlarmManager)

        // When
        homeViewModel.startTimerAlarm()

        // Then
        homeViewModel.homeUiState.test {
            val uiState = awaitItem()
            assertEquals(true, uiState.deniedExactAlarmDialog)
        }
    }

    @Test
    fun `다이얼로그 상태를 초기화 할 수 있다`() = runTest {

        // Given
        every { earAlarmManager.checkScheduleExactAlarms() } returns false
        homeViewModel = HomeViewModel(timerRepository, earAlarmManager)

        homeViewModel.updateNotificationPermissionState(PermissionState.DENIED, true)
        homeViewModel.startTimerAlarm()

        // When
        homeViewModel.dismissDialog()

        // Then
        homeViewModel.homeUiState.test {
            val uiState = awaitItem()
            assertEquals(false, uiState.deniedNotificationDialog)
            assertEquals(false, uiState.deniedExactAlarmDialog)
        }
    }
}