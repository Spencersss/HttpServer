package dev.spence.util;

import java.util.List;

public class ByteUtil {

    public static String getStringFromByteList(List<Byte> bytes) {
        StringBuilder byteString = new StringBuilder();
        for (byte b : bytes) {
            byteString.append((char) b);
        }
        return byteString.toString();
    }

}
