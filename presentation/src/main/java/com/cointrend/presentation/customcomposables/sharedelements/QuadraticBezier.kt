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

internal object QuadraticBezier {

    private class PointEntry(
        val t: Float,
        val point: Offset
    ) {
        var next: PointEntry? = null
    }

    private fun calculate(t: Float, p0: Float, p1: Float, p2: Float): Float {
        val oneMinusT = 1 - t
        return oneMinusT * (oneMinusT * p0 + t * p1) + t * (oneMinusT * p1 + t * p2)
    }

    private fun coordinate(t: Float, p0: Offset, p1: Offset, p2: Offset): Offset =
        Offset(
            calculate(t, p0.x, p1.x, p2.x),
            calculate(t, p0.y, p1.y, p2.y)
        )

    fun approximate(
        p0: Offset, p1: Offset, p2: Offset,
        acceptableError: Float
    ): Pair<FloatArray, LongArray> {
        val errorSquared = acceptableError * acceptableError

        val start = PointEntry(0f, coordinate(0f, p0, p1, p2))
        var cur = start
        var next = PointEntry(1f, coordinate(1f, p0, p1, p2))
        start.next = next
        var count = 2
        while (true) {
            var needsSubdivision: Boolean
            do {
                val midT = (cur.t + next.t) / 2
                val midX = (cur.point.x + next.point.x) / 2
                val midY = (cur.point.y + next.point.y) / 2

                val midPoint = coordinate(midT, p0, p1, p2)
                val xError = midPoint.x - midX
                val yError = midPoint.y - midY
                val midErrorSquared = (xError * xError) + (yError * yError)
                needsSubdivision = midErrorSquared > errorSquared

                if (needsSubdivision) {
                    val new = PointEntry(midT, midPoint)
                    cur.next = new
                    new.next = next
                    next = new
                    count++
                }
            } while (needsSubdivision)
            cur = next
            next = cur.next ?: break
        }

        cur = start
        var length = 0f
        var last = Offset.Unspecified
        val result = LongArray(count)
        val lengths = FloatArray(count)
        for (i in result.indices) {
            val point = cur.point
            @Suppress("INVISIBLE_MEMBER")
            result[i] = point.packedValue
            if (i > 0) {
                val distance = (point - last).getDistance()
                length += distance
                lengths[i] = length
            }
            cur = cur.next ?: break
            last = point
        }

        if (length > 0) {
            for (index in lengths.indices) {
                lengths[index] /= length
            }
        }

        return lengths to result
    }

}
