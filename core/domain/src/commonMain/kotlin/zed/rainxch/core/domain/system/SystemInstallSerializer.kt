package zed.rainxch.core.domain.system

interface SystemInstallSerializer {
    suspend fun awaitFreeAndMarkPending(
        packageName: String,
        timeoutMs: Long = DEFAULT_TIMEOUT_MS,
    )

    fun markCompleted(packageName: String)

    companion object {
        // Tuned down from 60s after field reports of "stuck at 100%"
        // when a prior install returned DELEGATED_TO_SYSTEM and the
        // broadcast that releases the gate never arrived (user
        // dismissed the system dialog, OEM throttling, Shizuku
        // fallback to default installer with no follow-through).
        // 15s is long enough to cover a normal Shizuku/Dhizuku silent
        // install round-trip and short enough that the queue recovers
        // before the user gives up.
        const val DEFAULT_TIMEOUT_MS: Long = 15_000L
    }
}
