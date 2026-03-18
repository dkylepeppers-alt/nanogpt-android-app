# Copilot Instructions — NanoGPT Android App

## Project Overview

Native Android client for [NanoGPT](https://nano-gpt.com/api) built with **Kotlin + Jetpack Compose**. Chat-first design with a clean expansion path toward image and video generation. Currently in scaffold phase (v0.1.0) — core functionality is being built out.

## Tech Stack

| Area | Technology | Version |
|------|-----------|---------|
| Language | Kotlin | 1.9.25 |
| UI | Jetpack Compose + Material 3 | BOM 2026.03.00 |
| Build | Gradle (Kotlin DSL `.kts`) | 8.13 via wrapper |
| Android Gradle Plugin | AGP | 8.13.2 |
| Java target | JDK | 17 |
| Networking | OkHttp | 4.12.0 |
| Serialization | kotlinx.serialization | 1.10.0 |
| Secure storage | EncryptedSharedPreferences | security-crypto 1.1.0 |
| Compose compiler | Kotlin compiler extension | 1.5.14 |
| Linting | ktlint (Gradle plugin) | 12.3.0 |
| Static analysis | detekt | 1.23.8 |

### Android SDK

- **compileSdk**: 34
- **targetSdk**: 34
- **minSdk**: 26 (Android 8.0)

## Repository Layout

```
├── app/                         # Android app module
│   ├── build.gradle.kts         # App-level Gradle config (Kotlin DSL)
│   ├── proguard-rules.pro       # ProGuard/R8 rules (not enabled yet)
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/kyle/nanogptapp/
│           ├── MainActivity.kt       # App entry point
│           ├── AppBlueprint.kt       # Constants (APP_NAME, BASE_URL)
│           ├── model/                # Data models (ChatModels, AppSettings)
│           ├── data/
│           │   ├── api/              # NanoGPT API client code
│           │   └── settings/         # Settings persistence (encrypted prefs)
│           └── ui/
│               ├── AppRoot.kt        # Main navigation (Chat & Settings tabs)
│               ├── chat/             # Chat screen and notes
│               └── settings/         # Settings screen, notes, and ViewModel
├── config/
│   └── detekt/detekt.yml        # Detekt rules config
├── docs/                        # Architecture and planning documents
│   ├── architecture.md          # Layer architecture and screen specs
│   ├── android-ci-review.md     # CI analysis and recommendations
│   ├── android-mobile-build-options.md # Phone-first build workflow guidance
│   ├── media-roadmap.md         # Future image/video generation plans
│   └── wrapper-bootstrap.md     # Gradle wrapper recovery instructions
├── gradle/
│   └── wrapper/                 # Checked-in Gradle wrapper JAR + properties
├── build.gradle.kts             # Root Gradle config (plugin versions)
├── gradlew                      # POSIX Gradle wrapper script
├── gradlew.bat                  # Windows Gradle wrapper script
├── settings.gradle.kts          # Module inclusion and repository config
├── gradle.properties            # JVM args, AndroidX, Kotlin style
├── nanogpt-api-llm-guide.md     # NanoGPT API integration reference
└── README.md                    # Project overview and roadmap
```

## Building the Project

### Prerequisites

- JDK 17 (Temurin recommended)
- Android SDK with platform 34 installed
- Use the committed Gradle wrapper (`./gradlew`)

### Gradle Wrapper Policy

The Gradle wrapper is committed and is the **only supported build entrypoint** for this repository.

If the wrapper ever needs to be regenerated, use the manual workflow at `.github/workflows/bootstrap-gradle-wrapper.yml`, which defaults to Gradle 8.13.

### Build Commands

```bash
./gradlew assembleDebug
```

### Lint and Static Analysis

Both linters run in CI and must pass before a build is considered healthy.

```bash
# ktlint — Kotlin code style
./gradlew ktlintCheck        # Check only
./gradlew ktlintFormat       # Auto-fix violations

# detekt — static analysis
./gradlew detekt
```

### Running All CI Checks Locally

```bash
./gradlew ktlintCheck detekt assembleDebug
```

## Code Style Rules

### ktlint

Uses the default ktlint ruleset via the Gradle plugin. Key rules enforced:

- **Trailing commas are required** in multi-line parameter lists, argument lists, and collection literals. This is the most common source of CI failures.
- Standard Kotlin formatting (indentation, spacing, imports)
- ktlint also checks `.kts` build scripts, not just source code

### detekt (config at `config/detekt/detekt.yml`)

- **`maxIssues: 0`** — any detekt violation fails the build
- **Max line length**: 140 characters
- **Long method threshold**: 80 lines
- **Long parameter list**: 8 params for functions, 10 for constructors
- **Wildcard imports**: forbidden
- **Magic numbers**: disabled (allowed)
- Formatting rules are active and inherit the 140-char max line length

### General Conventions

- Package: `com.kyle.nanogptapp`
- Application ID: `com.kyle.nanogptapp`
- Files with `Notes` suffix (for example, `ChatScreenNotes.kt`) are planning/blueprint documents stored as Kotlin files — they contain doc comments, not compiled logic
- Use KDoc (`/** */`) for documentation comments
- `BuildConfig.NANOGPT_BASE_URL` provides the API base URL (`https://nano-gpt.com/api`)

## Testing

### Current State

An initial JVM unit test baseline exists under `app/src/test/java/com/kyle/nanogptapp/`:

- `ui/settings/SettingsViewModelTest.kt` — tests for `SettingsViewModel` state transitions using a fake repository
- `ui/chat/ChatScreenTest.kt` — tests for `ChatScreen` helper behavior

The `app/src/androidTest/` directory has not been created yet.

Run unit tests in CI with `./gradlew testDebugUnitTest` (or the shorthand `./gradlew test`).

### Test Frameworks Available

- **Unit tests**: JUnit 4.13.2 → place in `app/src/test/java/com/kyle/nanogptapp/`
- **Instrumented tests**: Espresso 3.7.0 + Compose UI Test → place in `app/src/androidTest/java/com/kyle/nanogptapp/`
- **Test runner**: `androidx.test.runner.AndroidJUnitRunner`

### Running Tests

```bash
./gradlew test
./gradlew connectedCheck    # requires emulator/device
```

## Architecture

The app follows a layered architecture:

1. **UI Layer** — Compose screens and reusable components
2. **ViewModel Layer** — Owns UI state via `StateFlow`, orchestrates calls
3. **Repository Layer** — Business logic (for example, `getModels()`, `sendMessage()`, `streamChat()`)
4. **API Client Layer** — Handles HTTP headers, request bodies, SSE parsing
5. **Local Storage Layer** — `SharedPreferences` for settings, `EncryptedSharedPreferences` for API key

### Key Design Decisions

- **Security**: API key stored in `EncryptedSharedPreferences` with AES256-GCM. Never hardcode or log API keys.
- **Models are dynamic**: Fetch available models from the NanoGPT API at runtime — do not hardcode model lists.
- **Streaming**: NanoGPT returns SSE-style chunks. Accumulate `delta.content` and `delta.reasoning` separately; stop on `[DONE]`.
- **Extensibility**: The app is framed as a broader NanoGPT client (not chat-only). Use feature-based packages (`ui/chat/`, `ui/image/`, `ui/video/`) so image/video generation can be added cleanly later.

### API Integration

- **Base URL**: `https://nano-gpt.com/api` (available as `BuildConfig.NANOGPT_BASE_URL`)
- **Auth**: `Authorization: Bearer <NANOGPT_API_KEY>`
- **Reference**: See `nanogpt-api-llm-guide.md` in the repo root for full API documentation
- **Key endpoints**: `/models` (discovery), `/chat/completions` (chat), plus future image/video endpoints

## CI/CD Pipeline

### GitHub Actions Workflow (`.github/workflows/android.yml`)

Triggers on pushes to `main`/`master` and all pull requests.

**Pipeline steps**:
1. Checkout code
2. Set up JDK 17 (Temurin)
3. Set up Gradle cache
4. Ensure `gradlew` is executable
5. Run `./gradlew ktlintCheck`
6. Run `./gradlew detekt`
7. Build debug APK (`./gradlew assembleDebug`)
8. Upload debug APK as artifact

### Known CI Notes

1. **Wrapper-only builds**: Do not use system Gradle for this repo. Local and CI commands should go through `./gradlew`.
2. **ktlint trailing comma enforcement**: ktlint requires trailing commas in multi-line constructs. This applies to `.kts` build scripts too. If CI fails on `ktlintKotlinScriptCheck`, check build scripts for missing trailing commas.
3. **Bootstrap workflow**: If wrapper files ever need regeneration and local tooling is unavailable, run the `Bootstrap Gradle Wrapper` workflow from the Actions tab.

### Dependabot

Configured for weekly updates:
- Gradle dependencies (max 8 open PRs)
- GitHub Actions (max 4 open PRs)

## Common Pitfalls

1. **Trailing commas**: Always add trailing commas in multi-line parameter/argument lists in both Kotlin source and `.kts` build scripts. ktlint enforces this strictly.
2. **Line length**: Keep lines under 140 characters (both ktlint and detekt enforce this).
3. **Wildcard imports**: Do not use `import foo.bar.*` — detekt will reject it.
4. **Build script changes**: ktlint checks `.kts` files too. After editing build scripts, run `./gradlew ktlintCheck` when local tooling is available.
5. **API key security**: Never hardcode API keys. Use `EncryptedSharedPreferences` for storage. Never log API keys or include them in error messages.
6. **Notes files**: Files ending in `Notes` contain planning documentation as KDoc comments. They are part of the source tree but contain no executable logic.
7. **Wrapper consistency**: If Gradle is upgraded, update wrapper files, workflow defaults, and docs together.
