package com.hollingsworth.arsnouveau.client.container;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;

public class NumberFormatUtil {
    private static final int DIVISION_BASE = 1000;
    private static final char[] ENCODED_POSTFIXES = "KMGTPE".toCharArray();
    public static final Format format;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format_ = new DecimalFormat(".#;0.#");
        format_.setDecimalFormatSymbols(symbols);
        format_.setRoundingMode(RoundingMode.DOWN);
        format = format_;
    }

    public static String formatNumber(long number) {
        int width = 4;
        assert number >= 0;
        String numberString = Long.toString(number);
        int numberSize = numberString.length();
        if (numberSize <= width) {
            return numberString;
        }

        long base = number;
        double last = base * 1000;
        int exponent = -1;
        String postFix = "";

        while (numberSize > width) {
            last = base;
            base /= DIVISION_BASE;

            exponent++;

            numberSize = Long.toString(base).length() + 1;
            postFix = String.valueOf(ENCODED_POSTFIXES[exponent]);
        }

        String withPrecision = format.format(last / DIVISION_BASE) + postFix;
        String withoutPrecision = base + postFix;

        String slimResult = (withPrecision.length() <= width) ? withPrecision : withoutPrecision;
        assert slimResult.length() <= width;
        return slimResult;
    }
}
