package com.elegantbanshee.util;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageUtil {
    public static boolean isImage(byte[] body) {
        try {
            ImageIO.read(new ByteArrayInputStream(body));
        }
        catch (IOException e) {
            return false;
        }
        return true;
    }
}
