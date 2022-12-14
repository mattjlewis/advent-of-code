package com.diozero.aoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class StringUtilTest {
	@Test
	public void test1() {
		System.out.println();
		System.out.println("--- test1() ---");
		System.out.println();
		String s0 = "L6,R12,L6,R12,L10,L4,L6,L6,R12,L6,R12,L10,L4,L6,L6,R12,L6,L10,L10,L4,L6,R12,L10,L4,L6,L10,L10,L4,L6,L6,R12,L6,L10,L10,L4,L6,";

		String string = s0;
		System.out.println(string);

		String sub_string;

		sub_string = "L6,R12,L6,";
		System.out.println(StringUtil.countOccurrences(sub_string, string));

		System.out.println("Removing " + sub_string);
		string = string.replaceAll(sub_string, " ".repeat(sub_string.length()));
		System.out.println(string);
		printParts(string);

		sub_string = "R12,L10,L4,L6,";
		System.out.println(StringUtil.countOccurrences(sub_string, string));

		System.out.println("Removing " + sub_string);
		string = string.replaceAll(sub_string, " ".repeat(sub_string.length()));
		System.out.println(string);
		printParts(string);

		sub_string = "L10,L10,L4,L6,";
		System.out.println(StringUtil.countOccurrences(sub_string, string));

		System.out.println("Removing " + sub_string);
		string = string.replaceAll(sub_string, " ".repeat(sub_string.length()));
		System.out.println(string);
	}

	@Test
	public void test2() {
		System.out.println("--- test2() ---");
		String s0 = "L6,R12,L6,R12,L10,L4,L6,L6,R12,L6,R12,L10,L4,L6,L6,R12,L6,L10,L10,L4,L6,R12,L10,L4,L6,L10,L10,L4,L6,L6,R12,L6,L10,L10,L4,L6,";
		Pattern p = Pattern.compile("^(.{1,20})\\1*(.{1,20})(?:\\1|\\2)*(.{1,20})(?:\\1|\\2|\\3)*$");
		Matcher m = p.matcher(s0);
		if (m.matches()) {
			System.out.println(m.group(1));
			System.out.println(m.group(2));
			System.out.println(m.group(3));
		}
	}

	@Test
	public void test() {
		String s0 = "L6,R12,L6,R12,L10,L4,L6,L6,R12,L6,R12,L10,L4,L6,L6,R12,L6,L10,L10,L4,L6,R12,L10,L4,L6,L10,L10,L4,L6,L6,R12,L6,L10,L10,L4,L6,";
		String s1 = s0;

		int start_index = 0;
		int end_index = 0;
		String current_part;
		final List<String> parts = new ArrayList<>();
		while (!s1.isBlank()) {
			end_index = s1.indexOf(',', end_index + 1) + 1;
			if (end_index == 0 || end_index >= s1.length()) {
				break;
			}
			current_part = s1.substring(start_index, end_index).trim();
			if (StringUtil.countOccurrences(',', current_part) > 2) {
				int occurrences = StringUtil.countOccurrences(current_part, s1);
				System.out.format("%d occurrences of '%s'%n", occurrences, current_part);
				int alt_end_index = s1.indexOf(',', end_index + 1) + 1;
				String alt = s1.substring(start_index, alt_end_index).trim();
				int alt_occurrences = StringUtil.countOccurrences(alt, s1);
				System.out.format("%d occurrences of ALT '%s'%n", alt_occurrences, alt);
				String a;
				if (occurrences > alt_occurrences) {
					a = current_part;
					start_index = end_index;
				} else {
					a = alt;
					start_index = alt_end_index;
				}
				System.out.println("Going with " + a);
				parts.add(a.substring(0, a.length() - 1));
				s1 = s1.replaceAll(a, " ".repeat(a.length()));
				System.out.println("s1: " + s1);
			}
		}
		System.out.println("--- parts: " + parts + " ---");
	}

	@Test
	public void test3() {
		String str = "███   ██  ███    ██  ██  ███  █  █ ███  " + //
				"█  █ █  █ █  █    █ █  █ █  █ █  █ █  █ " + //
				"█  █ █  █ █  █    █ █    ███  ████ █  █ " + //
				"███  ████ ███     █ █    █  █ █  █ ███  " + //
				"█    █  █ █    █  █ █  █ █  █ █  █ █    " + //
				"█    █  █ █     ██   ██  ███  █  █ █    ";
		for (String s1 : StringUtil.split(str, 40)) {
			System.out.println(s1);
		}

		str = "01234567890123456789";
		int split_at = 5;
		List<String> split = StringUtil.split(str, split_at);
		Assertions.assertEquals(str.length() / split_at, split.size());

		split_at = 4;
		split = StringUtil.split(str, split_at);
		Assertions.assertEquals(str.length() / split_at, split.size());

		split_at = 3;
		split = StringUtil.split(str, split_at);
		Assertions.assertEquals(str.length() / split_at + 1, split.size());
		Assertions.assertEquals(str.length() % split_at, split.get(split.size() - 1).length());
		Assertions.assertEquals(str.substring(str.length() - str.length() % split_at), split.get(split.size() - 1));

		split = StringUtil.split(str, 1);
		Assertions.assertEquals(str.length(), split.size());
	}

	private static void printParts(String string) {
		String[] parts = string.trim().split(", +");
		System.out.println("parts:");
		for (String s : parts) {
			System.out.println("'" + s + "'");
		}
	}
}
