# Details Feature

Repository detail screen — owner, stats, releases with download/install, readme (with translation), per-app install/update flow. Most complex feature.

## Structure

```
feature/details/
├── domain/       # DetailsRepository, TranslationRepository; ReleaseCategory, RepoStats, SupportedLanguage, TranslationResult
├── data/         # impls + ReadmeLocalizationHelper, preprocessMarkdown
└── presentation/
    ├── DetailsViewModel.kt / State / Action / Event / Root
    ├── model/    # DownloadStage, InstallLogItem, LogResult, ShowDowngradeWarning, SupportedLanguages, TranslationState
    ├── components/
    │   ├── AppHeader, ReleaseAssetsPicker, VersionPicker, VersionTypePicker, SmartInstallButton, InspectApkButton, ApkInspectSheet, LanguagePicker, TranslationControls, StatItem
    │   └── sections/ About, Header, Logs, Owner, ReportIssue, Stats, WhatsNew, ReleaseChannel
    ├── states/ErrorState
    └── utils/    # MarkdownImageTransformer, MarkdownUtils, SystemArchitecture, LocalTopbarLiquidState, LogResultAsText
```

## Navigation

`GithubStoreGraph.DetailsScreen(repositoryId, owner, repo, isComingFromUpdate)`. By ID or owner+name (deep links use latter; `repositoryId == -1` falls back to owner+name lookup).

## Notes

- Readme localized via `ReadmeLocalizationHelper`; markdown via `multiplatform-markdown-renderer` + `MarkdownImageTransformer`. Translation via `TranslationRepository` + `LanguagePicker`.
- Download stages: `DownloadStage` idle→downloading→installing→done. `SmartInstallButton` adapts to install state. Downgrade warning before installing older version.
- Injects (lots): `DetailsRepository`, `TranslationRepository`, `FavouritesRepository`, `StarredRepository`, `InstalledAppsRepository`, `SeenReposRepository`, `TweaksRepository`, `TelemetryRepository`, `ExternalImportRepository`, `AuthenticationState`, `ProfileRepository`, `Downloader`, `Installer`, `PackageMonitor`, `SystemInstallSerializer`, `BrowserHelper`, `ShareManager`, `Platform`, `SyncInstalledAppsUseCase`, `InstallationManager`, `AttestationVerifier`, `DownloadOrchestrator`, `ApkInspector`, `GitHubStoreLogger`.
- Android installer paths: Default / Shizuku / Dhizuku / Root. Root via raw `su` (`RootServiceManager`). Dhizuku 14+ retries without installer attribution.
- **Multi-OS picker (E15):** `ReleaseAssetsItemsPicker` toggle flips `TweaksRepository.showAllPlatforms`. ON → assets group by `assetPlatformOf` into `PlatformSectionCard`s with "Your device"/"For transfer" chips. Non-current asset → `OnDownloadForTransfer` → `BrowserHelper.openUrl`.
- **Coachmarks:** APK Inspect button pulse + ReleaseChannel chip Popup. One-shot via `TweaksRepository.get*CoachmarkShown`.
- **Self-owned ✓ badge (E20):** `AppHeader` ✓ next to owner login when `state.isCurrentUserOwner`. Reactive via `combine(profileRepo.getUser(), state.repository.owner.login)`.
- **Skip release (E542):** per-app `skippedReleaseTag` on `InstalledApp`. `SmartInstallButton` suppresses CTA; auto-clears on strictly-newer release.
