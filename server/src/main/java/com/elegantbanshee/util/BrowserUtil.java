package com.elegantbanshee.util;

import java.awt.image.BufferedImage;
import java.util.Locale;

public class BrowserUtil {
    public static boolean shouldRotate(String userAgent) {
        userAgent = userAgent.toUpperCase(Locale.US);

        if (userAgent.contains("IPHONE") ||
                userAgent.contains("IPAD"))
            return true;
        if (userAgent.contains("ANDROID"))
            return true;
        return false;
    }
}
