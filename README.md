# NanoGPT Android App

A native Android app for NanoGPT focused on chat first, with a clean expansion path toward image and video generation.

Repository: <https://github.com/dkylepeppers-alt/nanogpt-android-app>

## Vision

Build a Kotlin + Jetpack Compose Android client that can:
- securely store a NanoGPT API key
- fetch available models dynamically
- send chat requests and stream responses
- expose reasoning when supported
- later grow into image and video generation workflows

## Current status

This repository currently contains:
- Android project scaffold
- Compose app shell with Chat and Settings tabs
- architecture docs
- NanoGPT API guide copied into the repo
- committed Gradle wrapper files for reproducible local and CI builds

The current Termux environment used for editing may not have the full Android build toolchain installed locally, so GitHub Actions remains the primary validation path.

## Build and CI baseline

This repo now builds through the committed Gradle wrapper targeting **Gradle 8.13**.

Typical commands:
- `./gradlew ktlintCheck`
- `./gradlew detekt`
- `./gradlew assembleDebug`

GitHub Actions runs the same wrapper-based flow and uploads the debug APK as an artifact.

## Planned stack

- Kotlin
- Jetpack Compose
- ViewModel + StateFlow
- OkHttp
- kotlinx.serialization
- secure local key storage

## Roadmap

### Phase 1 — Core app spine
- settings screen for API key
- model discovery via NanoGPT models endpoint
- one-shot chat completions
- basic chat state management

### Phase 2 — Streaming chat
- SSE streaming support
- reasoning output handling
- better error and loading states
- pricing/debug visibility where appropriate

### Phase 3 — Expanded NanoGPT features
- search toggle (`:online`)
- memory toggle (`:memory`)
- richer settings and presets

### Phase 4 — Media generation
- image generation workflows
- generated media gallery/history
- video generation job flows
- download/share/save UX

## Repository layout

- `app/` → Android app source
- `docs/` → planning docs
- `nanogpt-api-llm-guide.md` → NanoGPT integration reference

## Important API defaults

- Base URL: `https://nano-gpt.com/api`
- Auth: `Authorization: Bearer <NANOGPT_API_KEY>`
- Models should be queried, not hardcoded

## Notes

This app is intentionally being structured as a broader NanoGPT client, not a chat-only dead end, so image and video generation can be added cleanly later.
