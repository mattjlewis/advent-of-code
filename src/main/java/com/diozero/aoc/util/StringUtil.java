package com.diozero.aoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	public static List<String> split(String s, int length) {
		List<String> result = new ArrayList<>();

		for (int start = 0, end = length; start < s.length(); start += length, end += length) {
			result.add(s.substring(start, end > s.length() ? s.length() : end));
		}

		return result;
	}

	public static String repeat(String s, String delimiter, int count) {
		return IntStream.range(0, count).mapToObj(_ -> s).collect(Collectors.joining(delimiter));
	}

	public static String substring(String s, int start, int end) {
		return s.substring(start, end > s.length() ? s.length() : end);
	}

	public static String toString(char... chars) {
		return new String(chars);
	}

	public static String repeat(char ch, int count) {
		return IntStream.range(0, count).mapToObj(_ -> Character.toString(ch)).collect(Collectors.joining());
	}

	public static String reverse(String s) {
		return new StringBuilder(s).reverse().toString();
	}

	public static boolean[] toBooleanArray(String s, char set) {
		final boolean[] values = new boolean[s.length()];
		for (int i = 0; i < s.length(); i++) {
			values[i] = s.charAt(i) == set;
		}
		return values;
	}
}
