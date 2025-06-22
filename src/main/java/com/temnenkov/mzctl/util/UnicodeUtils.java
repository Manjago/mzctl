package com.temnenkov.mzctl.util;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UnicodeUtils {

    private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([0-9a-fA-F]{4})");

    private UnicodeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static @Nullable String unescapeUnicode(@Nullable String input) {
        if (input == null) {
            return null;
        }

        final Matcher matcher = UNICODE_PATTERN.matcher(input);
        final StringBuilder result = new StringBuilder(input.length());

        while (matcher.find()) {
            try {
                final String unicodeChar = String.valueOf((char) Integer.parseInt(matcher.group(1), 16));
                matcher.appendReplacement(result, unicodeChar);
            } catch (NumberFormatException e) {
                // если последовательность некорректна, оставим как есть
                matcher.appendReplacement(result, matcher.group(0));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
