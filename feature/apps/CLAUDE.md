# Apps Feature

Installed-apps manager. Lists apps installed through GHS, launches them, checks updates. Android-only in nav (bottom-nav hidden on Desktop).

## Structure

```
feature/apps/
├── domain/repository/AppsRepository.kt
├── data/  AppsRepositoryImpl + di
└── presentation/
    ├── AppsViewModel / State / Action / Event / Root
    ├── components/  app item cards, update badges, LinkAppBottomSheet, AdvancedAppSettingsBottomSheet, ApkInspectSheet, import banner
    ├── import/      # ExternalImportRoot/ViewModel — Obtainium import/export + manual-link
    └── starred/     # StarredPickerRoot/ViewModel — APK-shipping subset of user's GitHub stars
```

## Key interface

```kotlin
interface AppsRepository {
    suspend fun getApps(): Flow<List<InstalledApp>>
    suspend fun openApp(installedApp: InstalledApp, onCantLaunchApp: () -> Unit = {})
    suspend fun getLatestRelease(owner: String, repo: String): GithubRelease?
}
```

## Navigation

`GithubStoreGraph.AppsScreen`, `ExternalImportScreen`, `StarredPickerScreen`.

## Notes

- Uses `InstalledAppsRepository` + `SyncInstalledAppsUseCase`. `openApp` via `AppLauncher`. `PackageMonitor` + `Installer` (Android).
- Sort + search: `AppSortRule` enum (UpdatesFirst default, AlphabeticalAZ, RecentlyAdded, RecentlyUpdated). Persisted in DataStore. Inline search filters appName / packageName.
- Per-app actions: Ignore-updates (silence badge), Skip-this-release (per-tag, auto-clear on next release), Advanced filter (regex on asset names + monorepo fallback), Pin variant (token-set + glob fingerprint), Inspect APK (decoded manifest sheet).
- Link sheet (`LinkAppBottomSheet`) sorts installed apps by `InstallerCategory` (`core/domain`): SIDE_STORE (F-Droid / Obtainium / Aurora / Droid-ify / GHS itself) → SIDELOADED → VENDOR_STORE (Samsung / Huawei / Xiaomi / Oppo / vivo / Honor / Amazon) → PLAY_STORE → SYSTEM_UPDATE. `DeviceApp.installerPackageName` (via `PackageManager.getInstallSourceInfo` / pre-30 fallback) + `isUpdatedSystemApp` (via `FLAG_UPDATED_SYSTEM_APP`) feed the classifier. Each row shows a tonal source chip — F-Droid / Obtainium surfaces first; Samsung Authentication Framework and friends drop to the bottom.
- Auto-update on resume: `OnLifecycleResume` fires `autoCheckForUpdatesIfNeeded` (30-min cooldown) — catches drift after external install while GHS background-killed.
- External import (`import/`): Obtainium JSON import/export with pre-import summary buckets (imported / already-tracked / non-GitHub-skipped); manual-link-only path.
- Starred picker (`starred/`): scans signed-in user's GitHub stars, surfaces APK-shipping repos. Resumes mid-scan on rate-limit.
