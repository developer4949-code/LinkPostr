# LinkPostr

LinkPostr is a Kotlin Android app for drafting LinkedIn-ready posts with a mix of AI and lightweight local helpers. The app can generate a post from a topic, rewrite it for a sharper tone, suggest ideas, create hashtags, and hand the final copy off to LinkedIn through copy/share flows.

## Free AI model choice

This project is wired to Hugging Face Inference Providers using:

- `Qwen/Qwen2.5-7B-Instruct:cheapest`

Why this model:

- It is a strong instruction-following chat model, which matters more than raw size for short LinkedIn prompts.
- The model is live on Hugging Face Inference Providers, so it can be called through the OpenAI-compatible chat completions endpoint instead of requiring custom hosting.
- The `:cheapest` suffix keeps provider routing cost-aware, which fits a free-tier friendly setup.
- It has an Apache-2.0 license and performs well on writing, rewriting, and formatting tasks.

What the model does in LinkPostr:

- `Generate Post`: turns the user's topic and selected tone into a clean LinkedIn post draft.
- `Polish Tone`: rewrites an existing draft to sound more platform-ready.

What stays local and free:

- Smart topic suggestions
- Hashtag generation
- Copy/share actions
- LinkedIn handoff via Android intents

## Hugging Face setup

Create a fine-grained Hugging Face token with `Make calls to Inference Providers` permission, then add it to your local Android config:

```properties
HF_API_TOKEN=hf_your_token_here
HF_MODEL_ID=Qwen/Qwen2.5-7B-Instruct:cheapest
```

Add those keys to your local `local.properties` file in the project root. `HF_MODEL_ID` is optional because the app already defaults to the model above.

## Project structure

- `app/src/main/java/com/linkpostr/app/data`: API and repository layer
- `app/src/main/java/com/linkpostr/app/domain`: local helpers like hashtags and topic ideas
- `app/src/main/java/com/linkpostr/app/ui`: state holder and Compose UI
- `app/src/main/java/com/linkpostr/app/ui/theme`: app styling

## Notes

- Direct posting through LinkedIn's official API is intentionally not used because public access is restricted for most apps.
- If the Hugging Face token is missing, the app shows a helpful error instead of crashing.
