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

import androidx.compose.ui.geometry.Offset

class MaterialArcMotion : KeyframeBasedMotion() {

    override fun getKeyframes(start: Offset, end: Offset): Pair<FloatArray, LongArray> =
        QuadraticBezier.approximate(
            start,
            if (start.y > end.y) Offset(end.x, start.y) else Offset(start.x, end.y),
            end,
            0.5f
        )

}

val MaterialArcMotionFactory: PathMotionFactory = { MaterialArcMotion() }
