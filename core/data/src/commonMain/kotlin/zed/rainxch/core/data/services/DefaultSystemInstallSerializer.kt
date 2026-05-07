package zed.rainxch.core.data.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import zed.rainxch.core.domain.system.SystemInstallSerializer

class DefaultSystemInstallSerializer : SystemInstallSerializer {
    private val pending = MutableStateFlow<String?>(null)

    override suspend fun awaitFreeAndMarkPending(
        packageName: String,
        timeoutMs: Long,
    ) {
        val acquired =
            withTimeoutOrNull(timeoutMs) {
                while (!pending.compareAndSet(null, packageName)) {
                    pending.first { it == null }
                }
                Unit
            }
        if (acquired == null) {
            Logger.w {
                "SystemInstallSerializer: timed out waiting for ${pending.value} to clear; force-claiming for $packageName"
            }
            pending.value = packageName
        }
    }

    override fun markCompleted(packageName: String) {
        // Clear unconditionally rather than compareAndSet against the
        // marked package: any package install/uninstall completion means
        // the system installer activity is no longer holding our prior
        // install hostage, so the next gated install can proceed. Using
        // strict compareAndSet would leave the slot locked when a broadcast
        // fires for a different package than the one we marked (e.g.
        // `markPending` was called with an empty string because the APK
        // info extractor failed to surface a packageName).
        pending.value = null
    }
}
