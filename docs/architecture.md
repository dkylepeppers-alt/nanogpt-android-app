# Architecture

## v1 Screens

### 1. Chat screen
- message list
- composer input
- send button
- loading/streaming indicator
- optional reasoning panel

### 2. Settings screen
- NanoGPT API key input
- model selector
- reasoning toggle
- reasoning effort selector
- search toggle (`:online`)
- memory toggle (`:memory`, optional days)

## Layers

### UI
Compose screens and reusable components.

### ViewModel
Owns UI state and orchestrates calls.

### Repository
Provides methods like:
- `getModels()`
- `sendMessage()`
- `streamChat()`

### API client
Handles headers, request bodies, and SSE parsing.

### Local storage
Stores:
- API key
- selected model
- feature toggles

## Streaming handling
NanoGPT returns SSE-style chunks.
We need to:
- accumulate `delta.content`
- separately accumulate `delta.reasoning`
- stop on `[DONE]`
- optionally surface `x_nanogpt_pricing`

## Suggested package layout

- `data/api/`
- `data/repository/`
- `data/storage/`
- `model/`
- `ui/chat/`
- `ui/settings/`
- `ui/components/`
