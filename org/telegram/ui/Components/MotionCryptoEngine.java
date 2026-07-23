package org.telegram.ui.Components;

import java.util.Random;

public class MotionCryptoEngine {
    private static final String MASK_SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZРі#@$%&";

    public static String maskUserNickname() {
        Random random = new Random();
        int length = 8 + random.nextInt(5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(MASK_SYMBOLS.length());
            sb.append(MASK_SYMBOLS.charAt(index));
        }
        return sb.toString();
    }

    public static boolean stripMessageMetadata() {
        return true;
    }
}
