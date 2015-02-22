package ch.flokus.suby.service;

import java.math.BigInteger;
import java.nio.charset.Charset;

public class AppUtils {
	public static String replaceAsciiChars(String s) {
		s = s.replaceAll("&#196;", "Ä");
		s = s.replaceAll("&#214;", "Ö");
		s = s.replaceAll("&#215;", "×");
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
		s = s.replaceAll("&#8216;", "'");
		return s;
	}

	public static String toHex(String string) {
		return String.format("%040x", new BigInteger(1, string.getBytes(Charset.defaultCharset())));
	}

	public static String getNiceTime(Double duration) {
		return getNiceTime(String.valueOf(duration));
	}

	public static String getNiceTime(String duration) {
		Float raw = Float.valueOf(duration);
		int min = (int) (raw / 60);
		int sec = (int) (raw % 60);
		String mins = "" + min;
		String secs = "" + sec;
		if (min < 10)
			mins = "0" + mins;

		if (sec < 10)
			secs = "0" + secs;

		return mins + ":" + secs;
	}

	public static int getPercentage(String max, Double cur) {
		Double m = Double.valueOf(max);
		Double ret = cur * 100 / m;
		return ret.intValue();
	}
}
