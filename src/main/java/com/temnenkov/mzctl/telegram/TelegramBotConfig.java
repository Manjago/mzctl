package com.temnenkov.mzctl.telegram;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class TelegramBotConfig {
    private final String token;
    private final boolean proxyEnabled;
    private final String proxyHost;
    private final int proxyPort;
    private final int httpConnectTimeout;
    private final int httpRequestTimeout;
    private final int longPollingTimeout;

    // Значения по умолчанию
    private static final boolean DEFAULT_PROXY_ENABLED = false;
    private static final String DEFAULT_PROXY_HOST = "localhost";
    private static final int DEFAULT_PROXY_PORT = 3128;
    private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_HTTP_REQUEST_TIMEOUT = 30;
    private static final int DEFAULT_LONG_POLLING_TIMEOUT = 30;

    public TelegramBotConfig(Path configPath) throws IOException {
        final Properties props = new Properties();

        if (Files.exists(configPath)) {
            try (InputStream input = Files.newInputStream(configPath)) {
                props.load(input);
            }
        } else {
            throw new IllegalArgumentException("Config file not found: " + configPath);
        }

        token = props.getProperty("telegram.token");
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("telegram.token must be specified in the configuration file");
        }

        proxyEnabled = Boolean.parseBoolean(props.getProperty("telegram.proxy.enabled", String.valueOf(DEFAULT_PROXY_ENABLED)));
        proxyHost = props.getProperty("telegram.proxy.host", DEFAULT_PROXY_HOST);
        proxyPort = Integer.parseInt(props.getProperty("telegram.proxy.port", String.valueOf(DEFAULT_PROXY_PORT)));
        httpConnectTimeout = Integer.parseInt(props.getProperty("telegram.http.connectTimeout", String.valueOf(DEFAULT_HTTP_CONNECT_TIMEOUT)));
        httpRequestTimeout = Integer.parseInt(props.getProperty("telegram.http.requestTimeout", String.valueOf(DEFAULT_HTTP_REQUEST_TIMEOUT)));
        longPollingTimeout = Integer.parseInt(props.getProperty("telegram.longPollingTimeout", String.valueOf(DEFAULT_LONG_POLLING_TIMEOUT)));
    }

    // геттеры
    public String getToken() { return token; }
    public boolean isProxyEnabled() { return proxyEnabled; }
    public String getProxyHost() { return proxyHost; }
    public int getProxyPort() { return proxyPort; }
    public int getHttpConnectTimeout() { return httpConnectTimeout; }
    public int getHttpRequestTimeout() { return httpRequestTimeout; }
    public int getLongPollingTimeout() { return longPollingTimeout; }
}