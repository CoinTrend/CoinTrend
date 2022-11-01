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

import androidx.compose.runtime.*
import com.cointrend.presentation.customcomposables.sharedelements.DelayExitState.*

/**
 * When [visible] becomes false, if transition is running, delay the exit of the content until
 * transition finishes. Note that you may need to call [SharedElementsRootScope.prepareTransition]
 * before [visible] becomes false to start transition immediately.
 */
@Composable
fun SharedElementsRootScope.DelayExit(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    var state by remember { mutableStateOf(Invisible) }

    when (state) {
        Invisible -> {
            if (visible) state = Visible
        }
        Visible -> {
            if (!visible) {
                state = if (isRunningTransition) ExitDelayed else Invisible
            }
        }
        ExitDelayed -> {
            if (!isRunningTransition) state = Invisible
        }
    }

    if (state != Invisible) content()
}

private enum class DelayExitState {
    Invisible, Visible, ExitDelayed
}
