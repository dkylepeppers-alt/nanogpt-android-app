# Android-only build/test options for this repo

## Bottom line

For **this repo**, the most realistic path with **only an Android phone** is:

1. **Use the checked-in Gradle wrapper as the single build entrypoint**.
2. **Make GitHub Actions the primary build machine**.
3. Use the phone for **editing code, pushing commits, triggering workflows, downloading APK artifacts, and sideload testing**.
4. Treat on-device Termux builds as a **fallback / experiment**, not the default workflow.

That recommendation is based on the current repo state:

- `app/build.gradle.kts` uses a modern Android stack: **AGP 8.13.2**, **Kotlin 1.9.25**, **Compose**, **Java 17**.
- The repo now includes a checked-in **Gradle 8.13 wrapper**.
- The GitHub Actions workflow at `.github/workflows/android.yml` now:
  - sets up **JDK 17**
  - restores/configures the Gradle cache via `gradle/actions/setup-gradle@v5`
  - runs wrapper-only commands for `ktlintCheck`, `detekt`, and `assembleDebug`
  - uploads the debug APK as an artifact
- A `test/` source set is now active in `app/src/test/`, with an initial baseline of JVM unit tests for `SettingsViewModel` and `ChatScreen` helpers. CI can run `./gradlew testDebugUnitTest`. There is no `androidTest/` source set yet.

---

## What GitHub Actions already solves

The existing workflow is already close to an Android-only workflow because it removes the need for a local desktop build machine.

What you can do from a phone today:

- edit code in GitHub web / GitHub mobile / a mobile editor + git client / Termux
- push commits
- let Actions build the app remotely
- download the resulting debug APK artifact on the phone
- install it with `adb`-free sideloading (allow install from browser/files app as needed)

---

## Can the Gradle wrapper be generated without a local Gradle install?

### Short answer

**Yes, for this repo, via the dedicated GitHub Actions bootstrap workflow.**

Gradle’s officially recommended path is still to generate/update wrapper files using the `wrapper` task from an installed Gradle runtime. In a phone-first setup, this repository solves that by keeping a manual workflow that can regenerate the wrapper remotely.

### What that means in practice

- For everyday use, you do **not** need Gradle installed locally. Run `./gradlew ...`.
- If the wrapper ever needs to be regenerated, use the **Bootstrap Gradle Wrapper** workflow.
- That workflow defaults to **Gradle 8.13**, which matches the committed wrapper baseline.

### Practical implication for this repo

The clean operating model is now:

- wrapper committed to the repo
- CI and local commands use `./gradlew`
- GitHub Actions is the fallback path for wrapper regeneration when local tooling is unavailable

---

## Recommended workflow: GitHub Actions first

## Option 1 — Best overall: CI-first remote builds from phone

### How it works

- edit on phone
- push to GitHub
- GitHub Actions builds on `ubuntu-latest`
- download the debug APK artifact to the phone
- install and test on the same device

### Why this is the best fit

- no need to install the full Android SDK/Gradle toolchain locally on the phone
- avoids most Termux/ARM toolchain edge cases
- reproducible, shareable build environment
- easiest way to keep modern Android tooling working

### Caveats

- slower feedback loop than local desktop Android Studio
- debugging is mostly log-driven unless you add remote logging/crash reporting
- GitHub mobile app has limitations around artifacts; mobile browser is often more reliable
- you cannot realistically run emulator-based UI workflows from the phone itself

### What to add next

1. Keep the wrapper committed and authoritative.
2. Keep CI building `assembleDebug` on every push/PR.
3. Add **unit tests** and run `testDebugUnitTest` in CI.
4. If needed later, add a separate workflow for signed release artifacts.

---

## Option 2 — Good supplemental path: Android phone + Termux for editing and light repo work

### Realistic use

Termux is good for:

- git operations
- editing Kotlin/XML/Gradle files
- running formatters or simple non-Android scripts
- reviewing logs/artifacts
- preparing commits and pushing branches

### Why I would not make it the primary build path

Building a modern Android app fully on-device is possible in some community setups, but it is **not a smooth, officially supported Android dev workflow**.

Common pain points:

- Android SDK command-line tools on mobile are awkward
- some Android build tools assume desktop-style Linux/macOS/Windows environments
- ARM64 compatibility and `aapt2`/SDK packaging can become fragile
- storage, RAM, thermal throttling, and background process killing are real issues on phones
- troubleshooting build breakage is much worse than on CI

### Recommendation

Use Termux for **code + git**, not as the main Android build host, unless you specifically want to experiment.

---

## Option 3 — Fallback only: full on-device builds in Termux/proot

### Is it possible?

**Sometimes, yes.** There are community-maintained approaches using Termux, proot Ubuntu, OpenJDK 17, Gradle, and Android SDK command-line tools.

### Why this is not the main recommendation

For this repo, that path is lower-confidence than CI because the project uses:

- AGP 8.13.2
- Java 17
- Compose
- modern Gradle/Kotlin tooling

That stack is exactly where environment mismatch becomes expensive.

### Exact caveats

- expect manual SDK setup
- expect possible ARM/host-tool issues
- expect longer builds and more battery/heat impact
- expect occasional breakage after tool upgrades
- if the goal is “ship and iterate from a phone,” CI is more reliable than local Termux builds

### When this path makes sense

Only if you want:

- offline builds
- a no-cloud fallback
- a hobbyist/devops challenge

Not if you want the shortest path to productive Android app iteration.

---

## Testing realities with Android-only access

## What is easy

- **Manual device testing** by installing debug APKs from Actions artifacts
- **Static analysis** in CI (`ktlint`, `detekt`)
- **JVM unit tests** in CI once you add them

## What is harder

- **Instrumented tests** (`connectedAndroidTest`) from a phone-only workflow
- emulator-based testing
- interactive debugging comparable to Android Studio

## Practical recommendation

For now, optimize for:

1. CI lint/static checks
2. CI unit tests
3. manual testing on the physical Android device

That gives the highest return with the least infrastructure pain.

---

## Recommended implementation order

## 1) Keep a real Gradle wrapper committed

This is the highest-priority infrastructure baseline.

Why:

- Android tooling expects builds to be run through the wrapper
- the wrapper makes the project self-contained
- it removes ambiguity between local and CI Gradle versions
- it simplifies every future workflow

Current baseline:

- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`
- wrapper configured for **Gradle 8.13**

## 2) Keep GitHub Actions as the default build path

Use Actions for:

- `./gradlew ktlintCheck`
- `./gradlew detekt`
- `./gradlew assembleDebug`

## 3) Improve mobile artifact delivery later if needed

If artifact download friction becomes a real issue on mobile, evaluate newer artifact options or a release-distribution workflow.

## 4) Expand unit test coverage

The repo now has an initial JVM unit test baseline (`SettingsViewModel` and `ChatScreen` helpers) under `app/src/test/`. Run them in CI with `./gradlew testDebugUnitTest`.

Add more unit tests for:

- additional view-model logic as features expand
- model serialization / parsing logic
- repository edge cases

## 5) Use manual device testing for UI behavior

Since phone-only access makes emulator workflows awkward, rely on:

- install APK from Actions
- manually verify Compose screens and flows
- capture logcat if needed through on-device tools or app-level logging

## 6) Only explore Termux builds if CI is blocked

This is the fallback, not the foundation.

---

## Concrete next steps

1. Keep the wrapper and CI workflow aligned on **Gradle 8.13**.
2. Run CI on the current branch and confirm `ktlintCheck`, `detekt`, and `assembleDebug` pass through `./gradlew`.
3. Expand unit tests as app logic grows; run `./gradlew testDebugUnitTest` in CI.
4. Keep using the Android phone for **manual sideload testing** on meaningful changes.

---

## Final recommendation

If the constraint is truly **Android phone only**, I would not try to force a desktop-style local Android build stack onto the device as the main workflow.

**Best path:**

- Gradle wrapper committed to repo
- GitHub Actions as the build machine
- phone used for editing, pushing, downloading artifacts, and manual testing

That is the most realistic, least fragile, and fastest-to-productive setup for this project.
