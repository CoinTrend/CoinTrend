/*
This file is taken from https://github.com/mxalbert1996/compose-shared-elements
which has been released under the following license:

MIT License
Copyright (c) 2021 Albert Chang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.cointrend.presentation.customcomposables.sharedelements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun ElementContainer(
    modifier: Modifier,
    relaxMaxSize: Boolean = false,
    content: @Composable () -> Unit
) {
    Layout(content, modifier) { measurables, constraints ->
        if (measurables.size > 1) {
            throw IllegalStateException("SharedElement can have only one direct measurable child!")
        }
        val placeable = measurables.firstOrNull()?.measure(
            Constraints(
                minWidth = 0,
                minHeight = 0,
                maxWidth = if (relaxMaxSize) Constraints.Infinity else constraints.maxWidth,
                maxHeight = if (relaxMaxSize) Constraints.Infinity else constraints.maxHeight
            )
        )
        val width = min(max(constraints.minWidth, placeable?.width ?: 0), constraints.maxWidth)
        val height = min(max(constraints.minHeight, placeable?.height ?: 0), constraints.maxHeight)
        layout(width, height) {
            placeable?.place(0, 0)
        }
    }
}
