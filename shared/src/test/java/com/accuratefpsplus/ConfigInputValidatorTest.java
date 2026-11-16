package com.accuratefpsplus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConfigInputValidatorTest {
    @Test
    void integerInputMustBeNumericAndWithinInclusiveBounds() {
        assertEquals(1, ConfigInputValidator.parseInteger("1", 1, 100));
        assertEquals(100, ConfigInputValidator.parseInteger("100", 1, 100));
        assertNull(ConfigInputValidator.parseInteger("", 1, 100));
        assertNull(ConfigInputValidator.parseInteger("0", 1, 100));
        assertNull(ConfigInputValidator.parseInteger("101", 1, 100));
        assertNull(ConfigInputValidator.parseInteger("1x", 1, 100));
        assertNull(ConfigInputValidator.parseInteger("999999999999", 1, 100));
    }

    @Test
    void colorInputRequiresExactlySixHexadecimalDigits() {
        assertEquals(0x000000, ConfigInputValidator.parseColor("000000"));
        assertEquals(0xabcdef, ConfigInputValidator.parseColor("abcdef"));
        assertEquals(0xFFFFFF, ConfigInputValidator.parseColor("FFFFFF"));
        assertNull(ConfigInputValidator.parseColor(""));
        assertNull(ConfigInputValidator.parseColor("12345"));
        assertNull(ConfigInputValidator.parseColor("1234567"));
        assertNull(ConfigInputValidator.parseColor("12G456"));
    }

    @Test
    void textFiltersRetainEmptyEditingAndRejectInvalidCharacters() {
        assertTrue(ConfigInputValidator.digitsOnly(""));
        assertTrue(ConfigInputValidator.digitsOnly("0123456789"));
        assertFalse(ConfigInputValidator.digitsOnly("12.5"));
        assertFalse(ConfigInputValidator.digitsOnly("-1"));

        assertTrue(ConfigInputValidator.hexDigitsOnly(""));
        assertTrue(ConfigInputValidator.hexDigitsOnly("aBcD09"));
        assertFalse(ConfigInputValidator.hexDigitsOnly("12G456"));
    }

    @Test
    void manualThresholdsRemainStrictlyIncreasingWithinTheSupportedRange() {
        assertEquals(new ManualFpsThresholds(30, 60, 120), ManualFpsThresholds.normalize(30, 60, 120));
        assertEquals(new ManualFpsThresholds(998, 999, 1000), ManualFpsThresholds.normalize(1000, 1000, 1000));
        assertEquals(new ManualFpsThresholds(1, 2, 3), ManualFpsThresholds.normalize(0, 1, -1));
        assertEquals(new ManualFpsThresholds(900, 901, 902), ManualFpsThresholds.normalize(900, 60, 50));
    }
}
