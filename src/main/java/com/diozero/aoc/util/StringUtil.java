package com.diozero.aoc.util;

public class StringUtil {
	private StringUtil() {
	}

	public static String sortCharactersInString(final String s) {
		return s.chars().sorted().collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}
}
