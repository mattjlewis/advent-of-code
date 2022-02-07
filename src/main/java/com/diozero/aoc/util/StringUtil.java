package com.diozero.aoc.util;

import java.util.regex.Pattern;

public class StringUtil {
	private StringUtil() {
	}

	public static String sortCharactersInString(final String s) {
		return s.chars().sorted().collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	public static int countOccurrences(String subString, String string) {
		Pattern p = Pattern.compile(subString);
		return (int) p.matcher(string).results().count();
	}

	public static int countOccurrences(char ch, String string) {
		return (int) string.chars().filter(c -> c == ch).count();
	}
}
