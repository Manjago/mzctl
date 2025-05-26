package com.temnenkov.mzctl.telegram;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TelegramHttpClient {
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
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + token + "/" + method))
                .header("Content-Type", "application/json")
                .timeout(requestTimeout)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String getUpdates(long offset, int longPollingTimeout) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.telegram.org/bot%s/getUpdates?timeout=%d&offset=%d",
                        token, longPollingTimeout, offset)))
                .timeout(requestTimeout.plusSeconds(longPollingTimeout))
                .GET()
                .build();

        final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}