package com.sliit.echanneling.features.hospitalmanagement.util;

import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    public String clean(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("<", "")
                .replace(">", "")
                .replace("\"", "")
                .replace("'", "")
                .trim();
    }

    public String cleanNullable(String value) {
        String cleaned = clean(value);
        return cleaned.isBlank() ? null : cleaned;
    }
}
