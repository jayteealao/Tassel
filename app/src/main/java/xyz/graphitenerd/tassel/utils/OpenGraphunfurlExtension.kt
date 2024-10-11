package xyz.graphitenerd.tassel.utils

import android.util.Log
import me.saket.unfurl.UnfurlResult
import me.saket.unfurl.extension.HtmlMetadataUnfurlerExtension
import me.saket.unfurl.extension.UnfurlerExtension
import me.saket.unfurl.extension.UnfurlerScope
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.Request
import org.jsoup.Jsoup
import ru.gildor.coroutines.okhttp.await
import org.jsoup.nodes.Document as JsoupDocument

open class OpenGraphunfurlExtension(): UnfurlerExtension {
    override suspend fun UnfurlerScope.unfurl(url: HttpUrl): UnfurlResult? {
        val html = downloadHtml(url) ?: return null
        val htmlMetadata = with(HtmlMetadataUnfurlerExtension()) {
            unfurl(url)
        }
        val name = parseName(html)
        val mediaType = parseMediaType(html)

        return UnfurlResult(
            url = htmlMetadata?.url ?: url,
            title = htmlMetadata?.title,
            description = htmlMetadata?.description,
            favicon = htmlMetadata?.favicon,
            thumbnail = htmlMetadata?.thumbnail,
            extras = mapOf(
                OGExtra::class to OGExtra(
                    name = name,
                    mediaType = mediaType,
                )
            )
        )

    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected suspend fun UnfurlerScope.downloadHtml(url: HttpUrl): JsoupDocument? {
        val request: Request = Request.Builder()
            .url(url)
            // Some websites will deny empty/unknown user agents,
            // probably in an attempt to prevent scrapers?
            .header("User-Agent", SlackBotUserAgent)
            // Websites like nitter will deny requests if
            // content type and language headers are missing.
            .header("Accept", "text/html")
            .header("Accept-Language", "en-US,en;q=0.5")
            // Fetch as little of the page as possible, hoping
            // that the HTML tags are present in the initial range.
            // This was copied from Slack.
            .header("Range", "bytes=0-32_768")
            .build()

        return try {
            httpClient.newCall(request).await().use { response ->
                val body = response.body
                val redirectedUrl = response.request.url

                if (body != null && body.contentType().isHtmlText()) {
                    Jsoup.parse(
                        /* in */ body.source().inputStream(),
                        /* charsetName */ null,
                        /* baseUri */ redirectedUrl.toString(),
                    )
                } else {
                    null
                }
            }
        } catch (e: Throwable) {
            logger.log(e, "Failed to download HTML for $url")
            null
        }
    }

    private fun metaTag(document: JsoupDocument, attr: String, isUrl: Boolean = false): String? {
        return listOf(
            document.select("meta[name=$attr]"),
            document.select("meta[property=$attr]"),
        ).firstNotNullOfOrNull {
            it.attr(if (isUrl) "abs:content" else "content").nullIfBlank()
        }
    }

    private fun parseName(document: JsoupDocument): String? {
        val linkName = metaTag(document, "og:site_name")
            ?: metaTag(document, "twitter:site")
            ?: metaTag(document, "application-name")

        if (linkName == null) {
            Log.d("OpenGraphUnfurlExtension", "No name found")
        }

        return linkName
    }

    private fun parseMediaType(document: JsoupDocument): String? {
        val linkMediaType = metaTag(document, "og:type")
            ?: metaTag(document, "twitter:card")

        if (linkMediaType == null) {
            Log.d("OpenGraphUnfurlExtension", "No media type found")
        }

        return linkMediaType
    }

    private fun String.nullIfBlank(): String? {
        return ifBlank { null }
    }

    private fun MediaType?.isHtmlText(): Boolean {
        return this != null && type == "text" && subtype == "html"
    }

    @Suppress("ConstPropertyName", "unused")
    companion object {
        // Unfurl uses Slack's user agent by default because websites may
        // have special handling for slack. Source: https://api.slack.com/robots.
        const val SlackBotUserAgent = "Slackbot-LinkExpanding 1.0 (+https://api.slack.com/robots)"

        const val ChromeMobileUserAgent =
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Mobile Safari/537.36"
    }
}

data class OGExtra(
    val name: String?,
    val mediaType: String?,
)

fun UnfurlResult.ogExtra(): OGExtra? {
    return extra(OGExtra::class)
}