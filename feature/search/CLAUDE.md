# Search Feature

Repo search with platform / language / sort filters. Paginated results.

## Structure

```
feature/search/
├── domain/   # SearchRepository; SearchPlatform (All/Android/Macos/Windows/Linux), ProgrammingLanguage, SortBy
├── data/     # SearchRepositoryImpl + dto + mappers + di
└── presentation/
    ├── SearchViewModel / State / Action / Event / Root
    └── components/  filter chips, ClipboardLinkBanner, etc.
```

## Key interface

```kotlin
interface SearchRepository {
    fun searchRepositories(
        query: String,
        searchPlatform: SearchPlatform,
        language: ProgrammingLanguage,
        sortBy: SortBy,
        page: Int,
    ): Flow<PaginatedDiscoveryRepositories>
}
```

## Navigation

`GithubStoreGraph.SearchScreen`.

## Notes

- Platform filter → GitHub topic search (e.g. `android` topic). Language filter → `language:` qualifier. Debounce/throttle on queries.
- Injects: `SearchRepository`, `InstalledAppsRepository`, `SyncInstalledAppsUseCase`, `FavouritesRepository`, `StarredRepository`, `SeenReposRepository`, `HiddenReposRepository`, `TweaksRepository`, `ProfileRepository`, `TelemetryRepository`, `SearchHistoryRepository`, `ShareManager`, `ClipboardHelper`, `Platform`, `GitHubStoreLogger`.
- `computeVisibleRepos` filters `state.repositories` at render time by `hiddenRepoIds` AND (when `isHideSeenEnabled`) `seenRepoIds`. Unhide restores without re-fetch.
- Empty-grid-after-Hide-seen banner offers one-tap reset (issue #574) → `OnDisableHideSeenForResults`.
- Long-press card → shared `RepositoryActionsBottomSheet`.
- `DiscoveryRepositoryUi.isCurrentUserOwner` flipped by `observeCurrentUser` (E20).
- Clipboard auto-detect surfaces GitHub URLs from clipboard as a dismissible banner.
