# Future Image and Video Generation Roadmap

The app will start as a NanoGPT-native chat client, but the architecture should stay open for media generation.

## Design goal

Do not force image/video generation into a chat-only model.
Instead, keep the app capable of growing into three product areas:

1. Chat
2. Image generation
3. Video generation

## Planned future domains

### Image generation
- prompt input
- style presets
- aspect ratio / size selection
- generated image gallery
- temporary URL handling
- save/share/download actions
- image-to-image inputs

### Video generation
- prompt input
- job submission
- polling / job state
- result gallery
- download/share
- optional image-to-video or storyboard workflows

### Shared concerns
- API key management
- model selection
- pricing visibility
- request history
- error handling
- moderation / failure state UI

## Architectural implication

Prefer feature-based packages later, such as:
- `ui/chat/`
- `ui/image/`
- `ui/video/`
- `data/api/`
- `data/repository/`
- `model/`

The repository layer should eventually expose separate methods for:
- chat completions
- streaming chat
- image generation
- video generation jobs
- model capability discovery

## Why this matters now

If we keep the app framed as only a chat client, image/video support becomes awkward later.
If we treat chat as the first feature in a broader NanoGPT client, expansion stays clean.
