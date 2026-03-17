# Android CI review

## Bottom line

The repo is close to having useful CI, but the cleanest path for an Android-only user to get builds working soon is:

1. **Generate and commit the Gradle wrapper from Android Studio** (`gradlew`, `gradlew.bat`, `gradle/wrapper/*`)
2. **Make GitHub Actions use the wrapper only**
3. **Keep CI narrow at first**: `ktlintCheck`, `detekt`, `assembleDebug`

That is the shortest route to a repeatable Android build without depending on whatever `gradle` binary happens to exist on a runner.

## Current repo state

### Good
- Android app scaffold exists and looks structurally normal.
- Root plugins are declared cleanly.
- App module applies:
  - Android application plugin
  - Kotlin Android
  - Kotlin serialization
  - ktlint plugin
  - detekt plugin
- CI workflow already exists.
- Dependabot is set up for Gradle and GitHub Actions.
- Detekt config exists and is intentionally strict (`maxIssues: 0`).

### Main blocker
- **No Gradle wrapper is committed.**

For an Android project, that is the biggest practical problem. It hurts both:
- **local onboarding** for a typical Android Studio user
- **CI reproducibility** on GitHub Actions

The current workflow tries to fall back to a system Gradle install (`gradle ...`) if `./gradlew` is missing. That can work sometimes, but it is not the clean Android path.

## Risks / likely failure points

### 1) Missing wrapper
**Risk:** highest

Why it matters:
- Android Studio and GitHub Actions both work best when the wrapper defines the exact Gradle version.
- Without it, builds depend on CI setup details instead of repo state.
- It makes future Android Gradle Plugin updates harder to reason about.

### 2) CI currently supports two execution modes
The workflow does this pattern:
- use `./gradlew` if present
- else use `gradle`

**Risk:** medium

That adds ambiguity instead of reducing it. For Android repos, wrapper-only is cleaner and easier to debug.

### 3) Android SDK assumptions on runner
**Risk:** medium

`assembleDebug` needs Android tooling/platform packages compatible with:
- compileSdk 34
- AGP 8.5.2
- Java 17

GitHub-hosted runners often have enough preinstalled for this to work, but relying on ambient setup is still less explicit than ideal.

### 4) Static analysis may fail before build succeeds
**Risk:** low to medium

`detekt` is strict (`maxIssues: 0`). That is fine long-term, but on a new scaffold it can make CI red immediately if style/debt slips in. Same story for ktlint. Not a bad choice — just worth expecting.

## Dependabot review
Dependabot config is fine and reasonable:
- weekly Gradle updates
- weekly GitHub Actions updates
- grouped PRs

No urgent issue there. I would keep it.

## GitHub Actions review
Current workflow intent is good:
- checkout
- JDK 17
- Gradle setup
- ktlint
- detekt
- debug build
- upload APK

What I would change:
- **Require wrapper usage once wrapper is committed**
- Stop branching between `./gradlew` and `gradle`
- Optionally add an Android SDK setup step only if the runner proves flaky

## Cleanest path for an Android-only user

If the goal is **"get CI building soon with minimal churn"**, I would do exactly this:

### Minimal next actions
1. Open the project in **Android Studio**.
2. Let Android Studio generate/update the **Gradle wrapper** for the project.
3. Commit these files:
   - `gradlew`
   - `gradlew.bat`
   - `gradle/wrapper/gradle-wrapper.jar`
   - `gradle/wrapper/gradle-wrapper.properties`
4. Update CI to run only:
   - `./gradlew ktlintCheck detekt assembleDebug`
5. Push and see what fails first.
   - If lint/detekt fail, fix code/config.
   - If Android SDK is missing on runner, then add explicit Android SDK setup.

## Recommended CI shape after wrapper is added

Conceptually, the workflow should be:
- checkout
- setup Java 17
- setup Gradle cache
- `chmod +x gradlew`
- run `./gradlew ktlintCheck detekt assembleDebug`
- upload APK artifact

That is enough for a solid first Android CI lane.

## Opinionated call

If I had to pick one move only, it would be:

**Commit the Gradle wrapper first.**

Not detekt tweaks, not extra workflow polish, not more tooling.
The wrapper is the thing that turns this from “maybe builds on this runner” into “this repo defines how it builds.”

## Short summary

- **Biggest blocker:** missing Gradle wrapper
- **Current workflow:** close, but too tolerant of non-wrapper builds
- **Dependabot:** good as-is
- **ktlint/detekt:** fine to keep in CI from day one
- **Minimal path to green CI:** commit wrapper, then run `./gradlew ktlintCheck detekt assembleDebug`
