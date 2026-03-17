# NanoGPT Android App

Native Android chat app for the NanoGPT API.

## Goal

Build a Kotlin + Jetpack Compose Android app that:
- stores a NanoGPT API key securely
- fetches available models from `https://nano-gpt.com/api/v1/models?detailed=true`
- sends chat requests to NanoGPT
- supports streaming responses and reasoning text
- later adds search (`:online`) and memory (`:memory`) toggles

## Current state

This repo is scaffolded as a project blueprint.
Android SDK / Gradle tooling is not installed in the current Termux environment yet.

## Planned stack

- Kotlin
- Jetpack Compose
- ViewModel + StateFlow
- OkHttp
- kotlinx.serialization
- EncryptedSharedPreferences / Android Keystore-backed storage

## App architecture

- `docs/` → planning docs
- `app/src/main/java/com/kyle/nanogptapp/` → Kotlin source tree blueprint
  - `ui/` → screens and UI state
  - `data/` → API + storage
  - `model/` → request/response models

## First milestones

1. Create Android project shell
2. Settings screen for API key
3. Fetch model list
4. One-shot chat request
5. Streaming support
6. Reasoning toggle

## Key API notes

See `nanogpt-api-llm-guide.md`.

Important defaults:
- Base URL: `https://nano-gpt.com/api`
- Auth: `Authorization: Bearer <NANOGPT_API_KEY>`
- Models: query `/api/v1/models?detailed=true` instead of hardcoding
