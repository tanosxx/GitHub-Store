# Tweaks Feature

Single home for every app-level setting. Update prefs, installer choice (Default / Shizuku / Dhizuku / Root), telemetry, translation, mirror, feedback, hidden + skipped list managers. Absorbs settings half of former `feature/profile/`.

## Structure

```
feature/tweaks/presentation/
├── TweaksViewModel / State / Action / Event / Root, RestartApp
├── components/
│   ├── sections/  Account, Appearance, Installation, Language, Network, Others, Translation, SettingsSection
│   └── ToggleSettingCard, ClearDownloadsDialog, SectionText
├── feedback/      # FeedbackViewModel + sheet
├── hidden/        # HiddenRepositoriesRoot — Tweaks → Updates → Hidden repos
├── skipped/       # SkippedUpdatesRoot — Tweaks → Updates → Skipped updates
├── mirror/        # MirrorPickerRoot — Tweaks → Network → Mirror picker
└── model/         # ProxyScopeFormState, ProxyType
```

## Sub-screen VMs (registered in `composeApp/.../app/di/ViewModelsModule.kt`)

`SkippedUpdatesViewModel` (unskip via `InstalledAppsRepository.setSkippedReleaseTag`), `HiddenRepositoriesViewModel` (unhide / unhide-all via `HiddenReposRepository`), `FeedbackViewModel`, `AutoSuggestMirrorViewModel`, `MirrorPickerViewModel`.

## Navigation

`TweaksScreen`, `SkippedUpdatesScreen`, `HiddenRepositoriesScreen`, `MirrorPickerScreen`.

## Notes

- TweaksViewModel injects: `TweaksRepository`, `ThemesRepository`, `ProxyRepository`, `InstalledAppsRepository`, `ProfileRepository`, `InstallerStatusProvider`, `TelemetryRepository`, `Platform`, `BatteryOptimizationManager` (Android), `GitHubStoreLogger`.
- One-shot coachmark flags in `TweaksRepository` (`apk_inspect_coachmark_shown`, `channel_chip_coachmark_shown`). Once persisted true, never re-shown.
- `include_pre_releases` pref read by `InstallationManagerImpl` to seed new install's `InstalledApp.includePreReleases`. Existing rows keep per-app value.
- `show_all_platforms` pref drives cross-platform asset section in Details.
- Mirror picker gated on user locale (suggested in throttled regions).
- `RestartApp.kt` applies locale change (persist tag, restart MainActivity / DesktopApp).
