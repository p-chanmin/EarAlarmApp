package kr.ac.tukorea.android.earalarm.ui.components.picker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kr.ac.tukorea.android.earalarm.ui.theme.EarAlarmTheme
import kr.ac.tukorea.android.earalarm.ui.theme.Paddings

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    modifier: Modifier = Modifier,
    list: List<T>,
    state: LazyListState,
    itemWidth: Dp,
    itemHeight: Dp,
    unfocusedCount: Int = 1,
    content: @Composable (index: Int) -> Unit
) {
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = state)

    Box(
        modifier = modifier
            .width(itemWidth)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier.height(itemHeight * (unfocusedCount * 2 + 1)),
            flingBehavior = flingBehavior,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(count = Int.MAX_VALUE) { index ->
                val displayIndex = index % list.size
                Box(
                    modifier = Modifier.height(itemHeight), contentAlignment = Alignment.Center
                ) {
                    content(displayIndex)
                }
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .height(itemHeight * unfocusedCount)
                    .fillMaxWidth()
                    .alpha(0.6f)
                    .background(MaterialTheme.colorScheme.background)
            )
            Divider(color = MaterialTheme.colorScheme.primary, thickness = 2.dp)
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
            )
            Divider(color = MaterialTheme.colorScheme.primary, thickness = 2.dp)
            Box(
                modifier = Modifier
                    .height(itemHeight * unfocusedCount)
                    .fillMaxWidth()
                    .alpha(0.6f)
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}

@Composable
fun TimePicker(
    hours: List<Int>,
    minutes: List<Int>,
    hourState: LazyListState,
    minuteState: LazyListState,
    modifier: Modifier = Modifier,
    itemWidth: Dp = 80.dp,
    itemHeight: Dp = 50.dp,
    unfocusedCount: Int = 1,
    onTimeUpdated: (hour: Int, minute: Int) -> Unit
) {

    LaunchedEffect(hourState, minuteState) {
        val hourFlow = snapshotFlow { hourState.firstVisibleItemIndex }
        val minuteFlow = snapshotFlow { minuteState.firstVisibleItemIndex }

        combine(hourFlow, minuteFlow) { hourIndex, minuteIndex ->
            Pair(hourIndex, minuteIndex)
        }.collectLatest { (hourIndex, minuteIndex) ->
            val hour = hours[(hourIndex + unfocusedCount) % hours.size]
            val minute = minutes[(minuteIndex + unfocusedCount) % minutes.size]
            onTimeUpdated(hour, minute)
        }
    }

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WheelPicker(
            list = hours,
            state = hourState,
            itemWidth = itemWidth,
            itemHeight = itemHeight,
            unfocusedCount = unfocusedCount
        ) { index ->
            Text(
                text = "${hours[index]}", style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = ":",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = Paddings.small)
        )
        WheelPicker(
            list = minutes,
            state = minuteState,
            itemWidth = itemWidth,
            itemHeight = itemHeight,
            unfocusedCount = unfocusedCount
        ) { index ->
            Text(
                text = "${minutes[index]}", style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun TimePickerPreview() {
    EarAlarmTheme {

        val hours = (0..99).toList()
        val minutes = (0..59).toList()
        val hourState =
            rememberLazyListState(initialFirstVisibleItemIndex = (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % hours.size) - 1)
        val minuteState =
            rememberLazyListState(initialFirstVisibleItemIndex = (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % minutes.size) - 1)

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimePicker(
                hours,
                minutes,
                hourState,
                minuteState,
            ) { hour, minute ->
                println("observed $hour : $minute")
            }
        }
    }
}