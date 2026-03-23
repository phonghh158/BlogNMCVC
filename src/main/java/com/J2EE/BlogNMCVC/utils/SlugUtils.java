package com.J2EE.BlogNMCVC.utils;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SlugUtils {
    public static String toSlug(String input) {
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        return slug;
    }

    public static String toUniqueSlug(boolean checkExist, String slug, LocalDateTime time) {
        if (checkExist) {
            return slug;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH'h'mm'm'ss's'");
        String timeString = time.format(formatter);

        return slug + "." + timeString;
    }
}