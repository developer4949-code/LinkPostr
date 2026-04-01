package com.linkpostr.app.data

import com.linkpostr.app.BuildConfig
import com.linkpostr.app.data.ai.ChatCompletionRequest
import com.linkpostr.app.data.ai.ChatMessage
import com.linkpostr.app.data.ai.HuggingFaceApiService
import com.linkpostr.app.ui.ToneOption
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class LinkPostrRepository(
    private val api: HuggingFaceApiService,
) {

    suspend fun generatePost(topic: String, tone: ToneOption): Result<String> {
        val userPrompt = """
            Topic: $topic
            Desired tone: ${tone.promptLabel}
            
            Write a LinkedIn post for a student or early-career professional.
            Requirements:
            - Start with a scroll-stopping first line.
            - Keep it authentic, clear, and easy to read.
            - Use short paragraphs.
            - End with a simple invite for engagement.
            - Do not include hashtags.
            - Return only the final post body.
        """.trimIndent()

        return runPrompt(
            systemPrompt = "You write polished LinkedIn posts that sound human, concise, and credible.",
            userPrompt = userPrompt,
            temperature = 0.8,
        )
    }

    suspend fun improvePost(existingPost: String, tone: ToneOption): Result<String> {
        val userPrompt = """
            Rewrite this LinkedIn post so it feels more platform-ready.
            Target tone: ${tone.promptLabel}
            
            Keep the core message, but improve flow, clarity, and professionalism.
            Do not include hashtags.
            Return only the rewritten post.
            
            Post:
            $existingPost
        """.trimIndent()

        return runPrompt(
            systemPrompt = "You improve social posts without making them sound robotic or overhyped.",
            userPrompt = userPrompt,
            temperature = 0.55,
        )
    }

    private suspend fun runPrompt(
        systemPrompt: String,
        userPrompt: String,
        temperature: Double,
    ): Result<String> {
        if (BuildConfig.HF_API_TOKEN.isBlank()) {
            return Result.failure(
                IllegalStateException(
                    "Missing Hugging Face token. Add HF_API_TOKEN to local.properties or your environment.",
                ),
            )
        }

        return try {
            val response = api.createChatCompletion(
                authorization = "Bearer ${BuildConfig.HF_API_TOKEN}",
                request = ChatCompletionRequest(
                    model = BuildConfig.HF_MODEL_ID,
                    messages = listOf(
                        ChatMessage(role = "system", content = systemPrompt),
                        ChatMessage(role = "user", content = userPrompt),
                    ),
                    maxTokens = 320,
                    temperature = temperature,
                    stream = false,
                ),
            )

            val content = response.choices.firstOrNull()?.message?.content?.trim().orEmpty()
            if (content.isBlank()) {
                Result.failure(IllegalStateException("The model returned an empty response."))
            } else {
                Result.success(content)
            }
        } catch (exception: IOException) {
            Result.failure(
                IOException(
                    "Network error while contacting Hugging Face. Check your internet connection.",
                    exception,
                ),
            )
        } catch (exception: HttpException) {
            Result.failure(
                IllegalStateException(
                    "Hugging Face request failed with ${exception.code()}. Verify your token and remaining credits.",
                    exception,
                ),
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    companion object {
        fun create(): LinkPostrRepository {
            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://router.huggingface.co/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return LinkPostrRepository(retrofit.create(HuggingFaceApiService::class.java))
        }
    }
}

