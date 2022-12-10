package com.diozero.aoc.util;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	public static Set<Character> toCharSet(String s) {
		return s.chars().mapToObj(ch -> Character.valueOf((char) ch)).collect(Collectors.toSet());
	}
}
