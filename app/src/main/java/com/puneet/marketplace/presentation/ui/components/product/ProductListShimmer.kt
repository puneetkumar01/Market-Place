package com.puneet.marketplace.presentation.ui.components.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.puneet.marketplace.presentation.ui.components.common.ShimmerBox
import com.puneet.marketplace.presentation.ui.components.common.rememberShimmerBrush

@Composable
fun ProductListShimmer(
    modifier: Modifier = Modifier,
    itemCount: Int = 5,
) {
    val brush = rememberShimmerBrush()

    Column(modifier = modifier.fillMaxSize()) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            brush = brush,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp),
                brush = brush,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(4) {
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(96.dp),
                    shape = RoundedCornerShape(16.dp),
                    brush = brush,
                )
            }
        }

        ShimmerBox(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .width(64.dp)
                .height(14.dp),
            brush = brush,
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(itemCount) {
                ProductCardShimmer()
            }
        }
    }
}
