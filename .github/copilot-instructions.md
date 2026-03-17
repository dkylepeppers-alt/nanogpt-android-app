# Copilot Instructions — NanoGPT Android App

## Project Overview

Native Android client for [NanoGPT](https://nano-gpt.com/api) built with **Kotlin + Jetpack Compose**. Chat-first design with a clean expansion path toward image and video generation. Currently in scaffold phase (v0.1.0) — core functionality is being built out.

## Tech Stack

| Area | Technology | Version |
|------|-----------|---------|
| Language | Kotlin | 1.9.24 |
| UI | Jetpack Compose + Material 3 | BOM 2024.09.00 |
| Build | Gradle (Kotlin DSL `.kts`) | 8.7 |
| Android Gradle Plugin | AGP | 8.5.2 |
| Java target | JDK | 17 |
| Networking | OkHttp | 4.12.0 |
| Serialization | kotlinx.serialization | 1.7.2 |
| Secure storage | EncryptedSharedPreferences | security-crypto 1.1.0-alpha06 |
| Compose compiler | Kotlin compiler extension | 1.5.14 |
| Linting | ktlint (Gradle plugin) | 12.1.1 |
| Static analysis | detekt | 1.23.6 |

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
│   ├── media-roadmap.md         # Future image/video generation plans
│   └── wrapper-bootstrap.md     # Gradle wrapper bootstrap instructions
├── build.gradle.kts             # Root Gradle config (plugin versions)
├── settings.gradle.kts          # Module inclusion and repository config
├── gradle.properties            # JVM args, AndroidX, Kotlin style
├── nanogpt-api-llm-guide.md     # NanoGPT API integration reference
└── README.md                    # Project overview and roadmap
```

## Building the Project

### Prerequisites

- JDK 17 (Temurin recommended)
- Android SDK with platform 34 installed
- Gradle 8.7 (system install or wrapper)

### Important: Gradle Wrapper Status

The Gradle wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/*`) may not be committed to the repository. The CI workflow has fallback logic to use a system Gradle install when the wrapper is absent.

**If the wrapper is missing**, CI uses `gradle <task>` instead of `./gradlew <task>`. A manual workflow (`.github/workflows/bootstrap-gradle-wrapper.yml`) exists to generate and commit the wrapper.

### Build Commands

```bash
# If gradlew exists:
./gradlew assembleDebug

# If gradlew is missing (CI fallback):
gradle assembleDebug
```

### Lint and Static Analysis

Both linters run in CI and must pass with zero issues before a build proceeds.

```bash
# ktlint — Kotlin code style
./gradlew ktlintCheck       # Check only
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
- Files with `Notes` suffix (e.g., `ChatScreenNotes.kt`) are planning/blueprint documents stored as Kotlin files — they contain doc comments, not compiled logic
- Use KDoc (`/** */`) for documentation comments
- `BuildConfig.NANOGPT_BASE_URL` provides the API base URL (`https://nano-gpt.com/api`)

## Testing

### Current State

Test dependencies are configured (JUnit 4, Espresso, Compose UI Test) but **no test files exist yet**. The test directories (`app/src/test/`, `app/src/androidTest/`) have not been created.

### Test Frameworks Available

- **Unit tests**: JUnit 4.13.2 → place in `app/src/test/java/com/kyle/nanogptapp/`
- **Instrumented tests**: Espresso 3.6.1 + Compose UI Test → place in `app/src/androidTest/java/com/kyle/nanogptapp/`
- **Test runner**: `androidx.test.runner.AndroidJUnitRunner`

### Running Tests

```bash
./gradlew test              # Unit tests
./gradlew connectedCheck    # Instrumented tests (requires emulator/device)
```

## Architecture

The app follows a layered architecture:

1. **UI Layer** — Compose screens and reusable components
2. **ViewModel Layer** — Owns UI state via `StateFlow`, orchestrates calls
3. **Repository Layer** — Business logic (e.g., `getModels()`, `sendMessage()`, `streamChat()`)
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
3. Set up Gradle 8.7
4. Run `ktlintCheck`
5. Run `detekt`
6. Build debug APK (`assembleDebug`)
7. Upload debug APK as artifact

### Known CI Issues and Workarounds

1. **Missing Gradle wrapper**: The repo may lack committed wrapper files. CI falls back to system Gradle (`gradle` instead of `./gradlew`). If you need to add the wrapper, run the `Bootstrap Gradle Wrapper` workflow manually from the Actions tab, or generate it locally with `gradle wrapper --gradle-version 8.7`.

2. **ktlint trailing comma enforcement**: ktlint requires trailing commas in multi-line constructs. This applies to `.kts` build scripts too. If CI fails on `ktlintKotlinScriptCheck`, check build.gradle.kts for missing trailing commas in multi-line function calls.

3. **Node.js 20 deprecation warnings**: GitHub Actions runners may warn about Node.js 20 deprecation for actions like `actions/checkout@v4`, `actions/setup-java@v4`, and `gradle/actions/setup-gradle@v4`. These are warnings only and do not cause failures.

### Dependabot

Configured for weekly updates:
- Gradle dependencies (max 8 open PRs)
- GitHub Actions (max 4 open PRs)

## Common Pitfalls

1. **Trailing commas**: Always add trailing commas in multi-line parameter/argument lists in both Kotlin source and `.kts` build scripts. ktlint enforces this strictly.

2. **Line length**: Keep lines under 140 characters (both ktlint and detekt enforce this).

3. **Wildcard imports**: Do not use `import foo.bar.*` — detekt will reject it.

4. **Build script changes**: ktlint checks `.kts` files too. After editing `build.gradle.kts`, run `./gradlew ktlintCheck` to verify.

5. **API key security**: Never hardcode API keys. Use `EncryptedSharedPreferences` for storage. Never log API keys or include them in error messages.

6. **Notes files**: Files ending in `Notes` (e.g., `ChatScreenNotes.kt`, `SettingsScreenNotes.kt`) contain planning documentation as KDoc comments. They are part of the source tree but contain no executable logic.

7. **No wrapper committed**: If `gradlew` is missing, use `gradle` directly or run the bootstrap workflow. Do not manually create wrapper files — use `gradle wrapper --gradle-version 8.7`.
