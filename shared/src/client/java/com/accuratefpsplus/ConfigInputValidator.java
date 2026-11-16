package com.accuratefpsplus;

final class ConfigInputValidator {
    private ConfigInputValidator() {
    }

    static Integer parseInteger(String value, int min, int max) {
        if (value.isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value);
            return parsed >= min && parsed <= max ? parsed : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    static Integer parseColor(String value) {
        if (value.length() != 6 || !hexDigitsOnly(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value, 16);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    static boolean digitsOnly(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    static boolean hexDigitsOnly(String value) {
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (!Character.isDigit(character)
                && (character < 'a' || character > 'f')
                && (character < 'A' || character > 'F')) {
                return false;
            }
        }
        return true;
    }
}
