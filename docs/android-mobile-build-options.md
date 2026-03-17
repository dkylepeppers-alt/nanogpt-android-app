# Android-only build/test options for this repo

## Bottom line

For **this repo**, the most realistic path with **only an Android phone** is:

1. **Make GitHub Actions the primary build machine**.
2. **Commit a proper Gradle wrapper** (`gradlew`, `gradlew.bat`, `gradle/wrapper/*`) as the first build-infra fix.
3. Use the phone for **editing code, pushing commits, triggering workflows, downloading APK artifacts, and sideload testing**.
4. Treat on-device Termux builds as a **fallback / experiment**, not the default workflow.

That recommendation is based on the current repo state:

- `app/build.gradle.kts` uses a modern Android stack: **AGP 8.5.2**, **Kotlin 1.9.24**, **Compose**, **Java 17**.
- The repo currently has **no checked-in Gradle wrapper**.
- There is already a GitHub Actions workflow at `.github/workflows/android.yml` that:
  - sets up **JDK 17**
  - installs **Gradle 8.7** via `gradle/actions/setup-gradle@v4`
  - runs `ktlintCheck`, `detekt`, and `assembleDebug`
  - uploads the debug APK as an artifact
- There are currently **no `test/` or `androidTest/` source sets** in `app/src`, so CI is mainly lint + static analysis + build right now.

---

## What GitHub Actions already solves

The existing workflow is already close to an Android-only workflow because it removes the need for a local desktop build machine.

What you can do from a phone today:

- edit code in GitHub web / GitHub mobile / a mobile editor + git client / Termux
- push commits
- let Actions build the app remotely
- download the resulting debug APK artifact on the phone
- install it with `adb`-free sideloading (allow install from browser/files app as needed)

### Caveat

The current workflow uploads artifacts with `actions/upload-artifact@v4`, which typically means **zip download behavior**. That is workable on mobile, but annoying.

GitHub announced in 2026 that newer artifact actions can support **non-zipped browser downloads**, including in **mobile browsers**, when using newer versions and `archive: false`.

So if mobile convenience matters, this workflow should eventually be updated to a newer artifact action version that supports direct, unzipped downloads.

---

## Can the Gradle wrapper be generated without a local Gradle install?

### Short answer

**Not in the officially recommended way.**

Gradle’s own documentation is explicit: the Wrapper is **created using the `gradle wrapper` task**, and **generating the initial wrapper files requires an installed Gradle runtime**.

### What that means in practice

- Once wrapper files are present, **you do not need Gradle installed locally**. `./gradlew` downloads and runs the correct Gradle version for the project.
- But the **first creation** of wrapper files is supposed to happen from an environment that already has Gradle.

### Practical implication for this repo

Since the repo already has a GitHub Actions workflow that installs Gradle 8.7, the cleanest fix is:

- use CI once to run `gradle wrapper --gradle-version 8.7`
- commit the generated wrapper files back to the repo

That avoids needing Android Studio or a desktop machine.

### Unsupported / brittle alternatives

There are hacky ways to bootstrap wrapper files manually by copying scripts/JAR/properties from another source, but I do **not** recommend that as the main path because:

- it is easy to mismatch wrapper files and Gradle version
- it is harder to trust and audit
- Gradle docs do not present that as the supported setup flow

If you only have a phone, **CI-generated wrapper files** are the sane solution.

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

- no need to install Android SDK/Gradle locally on the phone
- avoids Termux/ARM toolchain edge cases
- reproducible, shareable build environment
- easiest way to keep modern Android tooling working

### Caveats

- slower feedback loop than local desktop Android Studio
- debugging is mostly log-driven unless you add remote logging/crash reporting
- GitHub mobile app has limitations around artifacts; mobile browser is often more reliable
- you cannot realistically run emulator-based UI workflows from the phone itself

### What to add next

1. **Commit Gradle wrapper files**.
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

- AGP 8.5.2
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

- **Instrumented tests** (`connectedAndroidTest`) from phone-only workflow
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

## 1) Commit a real Gradle wrapper

This is the highest-priority infrastructure fix.

Why:

- Android docs expect builds to be run through the wrapper
- wrapper makes the project self-contained
- removes ambiguity between local/CI Gradle versions
- simplifies every future workflow

Best way with Android-only access:

- create a one-off GitHub Actions job or temporary branch workflow that runs:
  - `gradle wrapper --gradle-version 8.7`
- commit these generated files back into the repo:
  - `gradlew`
  - `gradlew.bat`
  - `gradle/wrapper/gradle-wrapper.jar`
  - `gradle/wrapper/gradle-wrapper.properties`

After that, update CI to use `./gradlew ...` unconditionally.

## 2) Keep GitHub Actions as the default build path

Use Actions for:

- `ktlintCheck`
- `detekt`
- `assembleDebug`

This is already mostly in place.

## 3) Improve mobile artifact delivery

Upgrade artifact upload/download flow so APK retrieval is easier from a phone.

Preferred direction:

- move to newer artifact actions that support non-zipped artifact handling
- configure uploads so browser/mobile download friction is lower

## 4) Add actual unit tests

Right now the repo has test dependencies but no test source sets.

Add unit tests for:

- settings/repository logic
- view-model logic
- model serialization / parsing logic

Then run them in CI with `testDebugUnitTest`.

## 5) Use manual device testing for UI behavior

Since phone-only access makes emulator workflows awkward, rely on:

- install APK from Actions
- manually verify Compose screens and flows
- capture logcat if needed through on-device tools or app-level logging

## 6) Only explore Termux builds if CI is blocked

This is the fallback, not the foundation.

---

## Concrete next steps

1. **Bootstrap and commit the Gradle wrapper via CI**.
2. **Simplify `.github/workflows/android.yml`** to always use `./gradlew` after wrapper is committed.
3. **Keep debug APK artifact upload** in place.
4. **Modernize artifact delivery** for easier mobile downloads.
5. **Add JVM unit tests** and run them in CI.
6. Use the Android phone for **manual sideload testing** on every meaningful change.

---

## Final recommendation

If the constraint is truly **Android phone only**, I would not try to force a desktop-style local Android build stack onto the device as the main workflow.

**Best path:**

- GitHub Actions as the build machine
- Gradle wrapper committed to repo
- phone used for editing, pushing, downloading artifacts, and manual testing

That is the most realistic, least fragile, and fastest-to-productive setup for this project.