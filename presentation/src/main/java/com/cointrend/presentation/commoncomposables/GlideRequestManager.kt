package com.cointrend.presentation.commoncomposables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.Glide

@Composable
fun rememberGlideRequestManager() = LocalContext.current.let { remember(it) { Glide.with(it) } }