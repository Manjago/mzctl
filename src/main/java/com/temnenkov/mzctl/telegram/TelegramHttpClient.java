package com.temnenkov.mzctl.telegram;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.temnenkov.mzctl.util.UnicodeUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TelegramHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(TelegramHttpClient.class);

    private final HttpClient httpClient;
    private final String token;
    private final Duration requestTimeout;

    public TelegramHttpClient(@NotNull TelegramBotConfig config) {
        this.token = config.getToken();
        this.requestTimeout = Duration.ofSeconds(config.getHttpRequestTimeout());

        final HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(config.getHttpConnectTimeout()));

        if (config.isProxyEnabled()) {
            builder.proxy(ProxySelector.of(new InetSocketAddress(config.getProxyHost(), config.getProxyPort())));
        }

        this.httpClient = builder.build();
    }

    public String sendRequest(String method, String jsonBody) throws IOException, InterruptedException {
        final String url = "https://api.telegram.org/bot" + token + "/" + method;
        final String maskedUrl = maskToken(url);
        final String requestId = NanoIdUtils.randomNanoId();

        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Telegram API Request: POST {}\nHeaders: {}\nBody: {}", requestId, maskedUrl,
                    "Content-Type: application/json", jsonBody);
        }

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(requestTimeout)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logResponse(requestId, response);

        return response.body();
    }

    public String getUpdates(long offset, int longPollingTimeout) throws IOException, InterruptedException {
        final String url = String.format("https://api.telegram.org/bot%s/getUpdates?timeout=%d&offset=%d",
                token, longPollingTimeout, offset);
        final String maskedUrl = maskToken(url);
        final String requestId =  NanoIdUtils.randomNanoId();

        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Telegram API Request: GET {}\nHeaders: {}", requestId, maskedUrl, "No additional headers");
        }

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(requestTimeout.plusSeconds(longPollingTimeout))
                .GET()
                .build();

        final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logResponse(requestId, response);

        return response.body();
    }

    private static void logResponse(String requestId, HttpResponse<String> response) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Telegram API Response: Status {}\nHeaders: {}\nBody: {}", requestId,
                    response.statusCode(),
                    response.headers().map(),
                    UnicodeUtils.unescapeUnicode(response.body()));
        }
    }

    @Contract(pure = true)
    private @NotNull String maskToken(@NotNull String input) {
        return input.replace(token, "***TOKEN***");
    }
}