package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

/*-
 *   0:      1:      2:      3:      4:
 *  aaaa    ....    aaaa    aaaa    ....
 * b    c  .    c  .    c  .    c  b    c
 * b    c  .    c  .    c  .    c  b    c
 *  ....    ....    dddd    dddd    dddd
 * e    f  .    f  e    .  .    f  .    f
 * e    f  .    f  e    .  .    f  .    f
 *  gggg    ....    gggg    gggg    ....
 *
 *   5:      6:      7:      8:      9:
 *  aaaa    aaaa    aaaa    aaaa    aaaa
 * b    .  b    .  .    c  b    c  b    c
 * b    .  b    .  .    c  b    c  b    c
 *  dddd    dddd    ....    dddd    dddd
 * .    f  e    f  .    f  e    f  .    f
 * .    f  e    f  .    f  e    f  .    f
 *  gggg    gggg    ....    gggg    gggg
 */
public class Day8 extends AocBase {
	public static void main(String[] args) {
		new Day8().run();
	}

	private static class Line {
		final String[] signalPatterns;
		final String[] outputValues;
		Map<String, Integer> patternToNumber;

		public Line(String[] signalPatterns, String[] outputValues) {
			this.signalPatterns = Arrays.stream(signalPatterns).map(Day8::sortCharactersInString)
					.toArray(String[]::new);
			this.outputValues = Arrays.stream(outputValues).map(Day8::sortCharactersInString).toArray(String[]::new);
		}

		@Override
		public String toString() {
			return "Line [signalPatterns=" + Arrays.toString(signalPatterns) + ", outputValues="
					+ Arrays.toString(outputValues) + "]";
		}
	}

	private static Line parseLine(String line) {
		final String[] parts = line.split("\\|");
		return new Line(parts[0].trim().split(" "), parts[1].trim().split(" "));
	}

	public static String sortCharactersInString(String s) {
		return s.chars().sorted().collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	@Override
	public String part1(Path input) throws IOException {
		return Long.toString(Files.lines(input).map(line -> line.split("\\|")[1].trim()).map(line -> line.split(" "))
				.mapToLong(Day8::count1478).sum());
	}

	public static long count1478(String[] values) {
		return Arrays.stream(values).filter(value -> {
			int len = value.length();
			return len == 2 || len == 4 || len == 3 || len == 7;
		}).count();
	}

	@Override
	public String part2(Path input) throws IOException {
		final List<Line> lines = Files.lines(input).map(Day8::parseLine).collect(Collectors.toUnmodifiableList());

		for (Line line : lines) {
			// Get all unique values
			Map<String, Integer> pattern_to_number = new HashMap<>();
			Map<Integer, String> number_to_pattern = new HashMap<>();

			for (String pattern : line.signalPatterns) {
				if (!pattern_to_number.containsKey(pattern)) {
					Integer number = getUniqueNumber(pattern);
					pattern_to_number.put(pattern, number);
					if (number != null) {
						number_to_pattern.put(number, pattern);
					}
				}
			}
			for (String pattern : line.outputValues) {
				Integer number = getUniqueNumber(pattern);
				pattern_to_number.put(pattern, number);
				if (number != null) {
					number_to_pattern.put(number, pattern);
				}
			}

			Logger.debug("Starting pattern to number map: {}", pattern_to_number);
			Logger.debug("Starting number to pattern map: {}", number_to_pattern);

			// Now determine the remaining numbers
			// The delta between 1 and 7 is the top row (a)
			String zero = null;
			String one = number_to_pattern.get(Integer.valueOf(1));
			String two;
			String three;
			String four = number_to_pattern.get(Integer.valueOf(4));
			String five;
			String six = null;
			String seven = number_to_pattern.get(Integer.valueOf(7));
			String eight = number_to_pattern.get(Integer.valueOf(8));
			String nine = null;

			Set<Character> cf = toSet(one);

			Character a = distinct(seven, cf).stream().findFirst().orElseThrow();
			Logger.debug("a: {}", a);

			// Segments b and d are the delta between 4 and 1
			Set<Character> bd = distinct(four, cf);
			Logger.debug("bd: {}", bd);

			// Nine is the the only segment with 1 additional segment after joining abcdf
			// (g)
			String abcdf = sortCharactersInString(a + cf.stream().map(String::valueOf).collect(Collectors.joining())
					+ bd.stream().map(String::valueOf).collect(Collectors.joining()));

			Set<Character> abcdf_set = toSet(abcdf);
			Character g = null;
			for (String segment : pattern_to_number.entrySet().stream().filter(entry -> entry.getValue() == null)
					.filter(entry -> entry.getKey().length() == abcdf.length() + 1).map(Map.Entry::getKey)
					.collect(Collectors.toUnmodifiableList())) {
				Set<Character> diff = distinct(segment, abcdf_set);
				if (diff.size() == 1) {
					nine = segment;
					g = diff.stream().findFirst().orElseThrow();
					pattern_to_number.put(nine, Integer.valueOf(9));
					number_to_pattern.put(Integer.valueOf(9), nine);
					break;
				}
			}
			Logger.debug("9 is {}", nine);
			Logger.debug("g: {}", g);

			// Difference between 8 and 9 is e
			Character e = distinct(eight, toSet(nine)).stream().findFirst().orElseThrow();
			Logger.debug("e: {}", e);

			// Now know: cf, a, bd, g, e

			// There are 2 remaining unknown segments of length 6 (0 and 6).
			// 0 is the only unknown segment without d.
			Character d = null;
			for (String segment : pattern_to_number.entrySet().stream().filter(entry -> entry.getValue() == null)
					.filter(entry -> entry.getKey().length() == 6).map(Map.Entry::getKey)
					.collect(Collectors.toUnmodifiableList())) {
				Set<Character> diff = distinct(bd, toSet(segment));
				if (diff.size() == 1) {
					zero = segment;
					pattern_to_number.put(zero, Integer.valueOf(0));
					number_to_pattern.put(Integer.valueOf(0), zero);
					d = diff.stream().findFirst().orElseThrow();
				} else {
					six = segment;
					pattern_to_number.put(six, Integer.valueOf(6));
					number_to_pattern.put(Integer.valueOf(6), six);
				}
			}
			Logger.debug("0 is {}", zero);
			Logger.debug("d: {}", d);
			Logger.debug("6 is {}", six);

			// Difference between 6 and 8 is c
			Character c = distinct(eight, six).stream().findFirst().orElseThrow();
			Logger.debug("c: {}", c);
			Character b = distinct(bd, Set.of(d)).stream().findFirst().orElseThrow();
			Logger.debug("b: {}", b);
			Character f = distinct(cf, Set.of(c)).stream().findFirst().orElseThrow();
			Logger.debug("f: {}", f);

			// Now know: a, b, c, d, e, f, g

			two = sortCharactersInString("" + a + c + d + e + g);
			pattern_to_number.put(two, Integer.valueOf(2));
			number_to_pattern.put(Integer.valueOf(2), two);
			Logger.debug("two is {}", two);

			three = sortCharactersInString("" + a + c + d + f + g);
			pattern_to_number.put(three, Integer.valueOf(3));
			number_to_pattern.put(Integer.valueOf(3), three);
			Logger.debug("three is {}", three);

			five = sortCharactersInString("" + a + b + d + f + g);
			pattern_to_number.put(five, Integer.valueOf(5));
			number_to_pattern.put(Integer.valueOf(5), five);
			Logger.debug("five is {}", five);

			Logger.debug("pattern to number map: {}", pattern_to_number);
			Logger.debug("number to pattern map: {}", number_to_pattern);

			line.patternToNumber = pattern_to_number;
		}

		int sum = 0;
		for (Line line : lines) {
			int i = 0;
			for (String segment : line.outputValues) {
				Integer value = line.patternToNumber.get(segment);
				if (value == null) {
					Logger.debug("unknown segment '" + segment + "'");
				} else {
					i = i * 10 + value.intValue();
				}
			}
			sum += i;
		}

		return Integer.toString(sum);
	}

	private static Set<Character> toSet(String s) {
		return s.chars().mapToObj(c -> Character.valueOf((char) c)).collect(Collectors.toUnmodifiableSet());
	}

	private static Set<Character> distinct(String s1, String s2) {
		return distinct(s1, toSet(s2));
	}

	private static Set<Character> distinct(String s, Set<Character> chars) {
		return s.chars().mapToObj(c -> Character.valueOf((char) c)).filter(c -> !chars.contains(c))
				.collect(Collectors.toUnmodifiableSet());
	}

	private static Set<Character> distinct(Set<Character> s, Set<Character> chars) {
		return s.stream().filter(c -> !chars.contains(c)).collect(Collectors.toUnmodifiableSet());
	}

	private static Integer getUniqueNumber(String pattern) {
		switch (pattern.length()) {
		case 2:
			return Integer.valueOf(1);
		case 4:
			return Integer.valueOf(4);
		case 3:
			return Integer.valueOf(7);
		case 7:
			return Integer.valueOf(8);
		default:
			return null;
		}
	}
}
