package br.com.cesarcastro.apis.ccshortener.util;

import java.security.SecureRandom;

public final class Base62 {
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final SecureRandom RNG = new SecureRandom();

    public static String random(int len) {
        var sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHABET[RNG.nextInt(ALPHABET.length)]);
        return sb.toString();
    }
}
