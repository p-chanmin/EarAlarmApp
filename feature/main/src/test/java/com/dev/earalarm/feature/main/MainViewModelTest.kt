package com.dev.earalarm.feature.main

import app.cash.turbine.test
import com.dev.earalarm.core.data.InAppServiceRepository
import com.dev.firebase.FakeFirebaseManager
import com.dev.firebase.FirebaseManager
import com.dev.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import kotlin.test.assertEquals

internal class MainViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val inAppServiceRepository: InAppServiceRepository = mockk(relaxed = true)
    private val firebaseManager: FirebaseManager = FakeFirebaseManager()
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mockkStatic(ZonedDateTime::class)
        mockkStatic(ZoneId::class)

        every { ZoneId.systemDefault() } returns ZoneId.of("Asia/Seoul")
        every { ZonedDateTime.now() } returns ZonedDateTime.parse("2025-05-01T00:00:00.000000Z")
        Locale.setDefault(Locale.US)
    }

    @Test
    fun `마지막 리뷰 요청 시간과, 유연한 업데이트 거절 시간을 확인할 수 있다`() = runTest {

        // Given
        mainViewModel = MainViewModel(inAppServiceRepository, firebaseManager)

        val lastReviewDate = "2025-05-01T00:00:00.000000Z"
        val rejectFlexibleUpdateDate = "2025-05-01T00:00:00.000000Z"

        coEvery { inAppServiceRepository.lastReviewDate } returns flowOf(lastReviewDate)
        coEvery { inAppServiceRepository.rejectFlexibleUpdateDate } returns flowOf(
            rejectFlexibleUpdateDate
        )

        // When
        mainViewModel.mainUiState.test {

            // Then
            val uiState = awaitItem()
            assertEquals(
                MainUiState(
                    lastReviewDate = ZonedDateTime.parse(lastReviewDate),
                    rejectFlexibleUpdateDate = ZonedDateTime.parse(rejectFlexibleUpdateDate)
                ),
                uiState
            )
        }
    }

    @Test
    fun `마지막 리뷰 요청 시간을 갱신할 수 있다`() = runTest {

        // Given
        mainViewModel = MainViewModel(inAppServiceRepository, firebaseManager)

        val lastReviewDate = "2025-05-01T00:00:00.000000Z"
        val flow = MutableStateFlow<String?>(null)

        coEvery { inAppServiceRepository.lastReviewDate } returns flow
        coEvery { inAppServiceRepository.rejectFlexibleUpdateDate } returns flowOf(null)

        coEvery { inAppServiceRepository.setRejectFlexibleUpdateDate(lastReviewDate) } answers {
            flow.value = lastReviewDate
        }

        // When
        mainViewModel.setLastReviewDate()

        // Then
        mainViewModel.mainUiState.test {
            val uiState = awaitItem()
            assertEquals(
                ZonedDateTime.now(),
                uiState.lastReviewDate
            )
        }
    }

    @Test
    fun `유연한 업데이트 거절시간을 갱신할 수 있다`() = runTest {

        // Given
        mainViewModel = MainViewModel(inAppServiceRepository, firebaseManager)

        val rejectFlexibleUpdateDate = "2025-05-01T00:00:00.000000Z"
        val flow = MutableStateFlow<String?>(null)

        coEvery { inAppServiceRepository.lastReviewDate } returns flowOf(null)
        coEvery { inAppServiceRepository.rejectFlexibleUpdateDate } returns flow

        coEvery { inAppServiceRepository.setRejectFlexibleUpdateDate(rejectFlexibleUpdateDate) } answers {
            flow.value = rejectFlexibleUpdateDate
        }

        // When
        mainViewModel.setRejectFlexibleUpdateDate()

        // Then
        mainViewModel.mainUiState.test {
            val uiState = awaitItem()
            assertEquals(
                ZonedDateTime.now(),
                uiState.rejectFlexibleUpdateDate
            )
        }
    }
}