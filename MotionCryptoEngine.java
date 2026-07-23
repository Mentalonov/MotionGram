package org.telegram.ui.Components;

import java.security.SecureRandom;

public class MotionCryptoEngine {
    private static final String MASK_SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\\u20BD#@$%&";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String maskUserNickname() {
        int length = 8 + secureRandom.nextInt(5);
        StringBuilder sb = new StringBuilder(length);
        int limit = MASK_SYMBOLS.length();
        
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(limit);
            sb.append(MASK_SYMBOLS.charAt(index));
        }
        return sb.toString();
    }

    public static boolean stripMessageMetadata() {
        return true;
    }
}
