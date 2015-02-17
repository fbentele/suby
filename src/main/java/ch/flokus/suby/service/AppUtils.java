package ch.flokus.suby.service;

import java.math.BigInteger;
import java.nio.charset.Charset;

public class AppUtils {
    public static String replaceAsciiChars(String s) {
        s = s.replaceAll("&#196;", "Ä");
        s = s.replaceAll("&#214;", "Ö");
        s = s.replaceAll("&#220;", "Ü");
        s = s.replaceAll("&#223;", "ß");
        s = s.replaceAll("&#228;", "ä");
        s = s.replaceAll("&#229;", "å");
        s = s.replaceAll("&#232;", "è");
        s = s.replaceAll("&#233;", "é");
        s = s.replaceAll("&#235;", "ë");
        s = s.replaceAll("&#242;", "ó");
        s = s.replaceAll("&#244;", "ô");
        s = s.replaceAll("&#245;", "õ");
        s = s.replaceAll("&#246;", "ö");
        s = s.replaceAll("&#252;", "ü");
        s = s.replaceAll("&#776;", "ö");
        s = s.replaceAll("&#215;", "×");

        return s;
    }

    public static String toHex(String string) {
        return String.format("%040x", new BigInteger(1, string.getBytes(Charset.defaultCharset())));
    }
}
