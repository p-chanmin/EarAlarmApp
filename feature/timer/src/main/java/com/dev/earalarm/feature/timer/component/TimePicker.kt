package com.dev.earalarm.feature.timer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme

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
            modifier = Modifier.height(itemHeight * (unfocusedCount * 2 + 1) - 2.dp),
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
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)
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

@Preview
@Composable
fun TimePickerPreview() {
    EarAlarmTheme {

        val hours = (0..99).toList()
        val state =
            rememberLazyListState(initialFirstVisibleItemIndex = (Int.MAX_VALUE / 2) - (Int.MAX_VALUE / 2 % hours.size) - 1)

        WheelPicker(
            modifier = Modifier,
            list = hours,
            state = state,
            itemWidth = 80.dp,
            itemHeight = 50.dp,
            unfocusedCount = 1,
        ) { i ->
            Text(
                text = hours[i].toString()
            )
        }
    }
}