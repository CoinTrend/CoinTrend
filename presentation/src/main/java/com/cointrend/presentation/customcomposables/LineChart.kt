// Inspired by https://github.com/philipplackner/StockMarketApp/blob/final/app/src/main/java/com/plcoding/stockmarketapp/presentation/company_info/StockChart.kt

package com.cointrend.presentation.customcomposables

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.models.DataPoint
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import kotlinx.collections.immutable.ImmutableList
import timber.log.Timber

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: ImmutableList<DataPoint>,
    graphColor: Color,
    showDashedLine: Boolean,
    showYLabels: Boolean = false
) {
    Timber.d("LineChart recomposition")

    if (data.isEmpty()) {
        Timber.w("LineChart invoked with empty data list.")
        return
    }

    val spacing = 0f
    val transparentGraphColor = remember(key1 = graphColor) {
        graphColor.copy(alpha = 0.5f)
    }

    val (lowerValue, upperValue) = remember(key1 = data) {
        Pair(
            data.minBy { it.y },
            data.maxBy { it.y }
        )
    }

    val density = LocalDensity.current

    Canvas(modifier = modifier) {

        val spacePerHour = (size.width - spacing) / data.size
        /*
        (0 until data.size - 1 step 2).forEach { i ->
            val info = data[i]
            val hour = info.xLabel
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    hour.toString(),
                    spacing + i * spacePerHour,
                    size.height - 5,
                    textPaint
                )
            }
        }
        val priceStep = (upperValue - lowerValue) / 5f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * size.height / 5f,
                    textPaint
                )
            }
        }

         */
        var lastX = 0f
        var firstY = 0f
        val strokePath = Path().apply {
            val height = size.height
            for (i in data.indices) {
                val info = data[i]
                val nextInfo = data.getOrNull(i + 1) ?: data.last()
                val leftRatio = (info.y - lowerValue.y) / (upperValue.y - lowerValue.y)
                val rightRatio = (nextInfo.y - lowerValue.y) / (upperValue.y - lowerValue.y)

                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (leftRatio * height).toFloat()

                if (i == 0) {
                    firstY = y1
                }

                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * height).toFloat()
                if (i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f
                quadraticBezierTo(
                    x1, y1, lastX, (y1 + y2) / 2f
                )
            }
        }

        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close()
            }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            ),
        )

        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        if (showDashedLine) {
            val dottedPath = Path().apply {
                moveTo(0f, firstY)
                lineTo(lastX, firstY)
            }

            drawPath(
                path = dottedPath,
                color = graphColor.copy(alpha = .8f),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f), 0f)
                )
            )
        }

        if (showYLabels) {
            val textPaint = Paint().apply {
                color = StocksDarkPrimaryText.toArgb()
                textAlign = Paint.Align.RIGHT
                textSize = density.run { 12.dp.toPx() }
                typeface = setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
                alpha = 192
            }

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "MAX ${upperValue.yLabel.orEmpty()}",
                    size.width - 16.dp.toPx(),
                    0 + 8.dp.toPx(),
                    textPaint
                )
                drawText(
                    "MIN ${lowerValue.yLabel.orEmpty()}",
                    size.width - 16.dp.toPx(),
                    size.height - 4.dp.toPx(),
                    textPaint
                )
            }


            /*
            val steps = 4
            val priceStep = (upperValue - lowerValue) / steps.toFloat()
            (0 until steps).forEach { i ->
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        (lowerValue + priceStep * i).toString(),
                        16.dp.toPx(),
                        size.height - spacing - i * size.height / steps.toFloat(),
                        textPaint
                    )
                }
            }

             */
        }

        /*
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                "22,094.00",
                8.dp.toPx(),
                firstY - 4.dp.toPx(),
                textPaint
            )
        }

         */


    }

}