package com.github.pksokolowski.smogmid.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class ScopesProvider {
    val aqUpdatesDispatcher = Executors.newFixedThreadPool(64).asCoroutineDispatcher()
    val aqUpdatesScope = CoroutineScope(aqUpdatesDispatcher + SupervisorJob())
}