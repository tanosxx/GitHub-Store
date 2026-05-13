# Auth Feature

GitHub OAuth via **device flow** — user visits a URL + enters code shown in app. No browser redirect. Works on Android + Desktop.

## Structure

```
feature/auth/
├── domain/repository/AuthenticationRepository.kt
├── data/  AuthenticationRepositoryImpl, network/GitHubAuthApi, di
└── presentation/
    ├── AuthenticationViewModel / State / Action / Event / Root
    └── components/
```

## Key interface

```kotlin
interface AuthenticationRepository {
    val accessTokenFlow: Flow<String?>
    suspend fun startDeviceFlow(): GithubDeviceStart
    suspend fun awaitDeviceToken(start: GithubDeviceStart): GithubDeviceTokenSuccess
}
```

## Navigation

`GithubStoreGraph.AuthenticationScreen`.

## Notes

- Token stored via `TokenStore` (DataStore-backed). `accessTokenFlow` observed app-wide.
- `GITHUB_CLIENT_ID` in `local.properties` for builds.
- **Backend-proxied primary path:** `/v1/auth/device/start` + `/poll` on GHS backend so users on networks throttling `github.com` (China, corporate filters) can still log in. Each session picks one `AuthPath` (`Backend`|`Direct`), persists in `SavedStateHandle`. Only escalates `Backend → Direct` on infra errors (timeout/5xx). HTTP 4xx + GitHub negative 200-bodies (`authorization_pending`, `slow_down`, `access_denied`, `expired_token`, `bad_verification_code`) are real answers — never cause fallback.
- Backend rate limits hard: 10 starts/hr, 200 polls/hr per IP. Don't add retry loops on top of Ktor's `HttpRequestRetry(maxRetries = 2)`.
- Backend responses carry `X-Request-ID` — `GitHubAuthApi` embeds it in error messages via `asRequestIdTag()` (maps to backend logs).
- Both paths share same OAuth App — client `GITHUB_CLIENT_ID` must match backend's `GITHUB_OAUTH_CLIENT_ID`. Backend endpoints in `core/data/network/BackendEndpoints.kt`.
