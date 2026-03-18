# Gradle wrapper bootstrap and recovery

This repo now includes a checked-in Gradle wrapper targeting **Gradle 8.13**.

Committed wrapper files:
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`

## Normal usage

Use the wrapper for all local and CI builds:

- `./gradlew ktlintCheck`
- `./gradlew detekt`
- `./gradlew assembleDebug`

The wrapper properties currently point at:
- Gradle `8.13`
- distribution URL: `https://services.gradle.org/distributions/gradle-8.13-bin.zip`

## If the wrapper ever needs to be regenerated

Use the **Bootstrap Gradle Wrapper** workflow from GitHub Actions.

1. Open the repo on GitHub.
2. Go to **Actions**.
3. Run **Bootstrap Gradle Wrapper**.
4. Leave the default version at `8.13` unless there is a deliberate upgrade plan.
5. Let the workflow commit updated wrapper files.

## Why keep the workflow if the wrapper is already committed?

Because phone-first development in Termux may not always have a working local JDK + Gradle install available for wrapper regeneration. The workflow provides a reproducible recovery path without depending on desktop Android Studio.

## Notes

- CI is now wrapper-only; there is no system-Gradle fallback in `.github/workflows/android.yml`.
- If GitHub Actions cannot push the wrapper update commit, check repository settings so workflow-created commits have write permission.
- When upgrading Gradle in the future, keep the wrapper files, CI workflow, and docs in sync in the same change.
