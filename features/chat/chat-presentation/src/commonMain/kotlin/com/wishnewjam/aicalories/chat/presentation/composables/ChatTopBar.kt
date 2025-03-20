package com.wishnewjam.aicalories.chat.presentation.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.wishnewjam.aicalories.resources.Res
import com.wishnewjam.aicalories.resources.app_name
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    LargeTopAppBar(
        title = {
            Text(
                stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}