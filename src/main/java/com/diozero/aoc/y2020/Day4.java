package com.diozero.aoc.y2020;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.diozero.aoc.AocBase;

public class Day4 extends AocBase {
	public static void main(String[] args) {
		new Day4().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		return loadData(input, false).size();
	}

	@Override
	public long part2(Path input) throws IOException {
		return loadData(input, true).size();
	}

	private static List<Map<Field, String>> loadData(Path input, boolean validate)
			throws FileNotFoundException, IOException {
		final List<Map<Field, String>> passports = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(input.toFile()))) {
			Map<Field, String> passport = new HashMap<>();
			while (true) {
				String line = br.readLine();
				if (line == null || line.isBlank()) {
					// Ignore the cid field
					passport.remove(Field.cid);
					if (validate) {
						passport.entrySet().removeIf(entry -> !entry.getKey().isValid(entry.getValue()));
					}
					if (passport.size() == Field.values().length - 1) {
						passports.add(passport);
					}
					if (line == null) {
						break;
					}
					passport = new HashMap<>();
				} else {
					try {
						passport.putAll(Stream.of(line.split(" ")).map(fv -> fv.split(":"))
								.collect(Collectors.toMap(fv -> Field.valueOf(fv[0]), fv -> fv[1])));
					} catch (IllegalArgumentException e) {
						// Ignore
					}
				}
			}
		}

		return passports;
	}

	private enum Field {
		byr("(\\d{4})"), iyr("(\\d{4})"), eyr("(\\d{4})"), hgt("(\\d+)(cm|in)"), hcl("#([0-9|a-z]{6})"),
		ecl("(amb|blu|brn|gry|grn|hzl|oth)"), pid("(\\d{9})"), cid(".*");

		private final Pattern pattern;

		Field(String pattern) {
			this.pattern = Pattern.compile(pattern);
		}

		public boolean isValid(String value) {
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				return false;
			}

			boolean valid;
			switch (this) {
			case byr:
				int year = Integer.parseInt(matcher.group(1));
				valid = year >= 1920 && year <= 2002;
				break;
			case iyr:
				year = Integer.parseInt(matcher.group(1));
				valid = year >= 2010 && year <= 2020;
				break;
			case eyr:
				year = Integer.parseInt(matcher.group(1));
				valid = year >= 2020 && year <= 2030;
				break;
			case hgt:
				int h = Integer.parseInt(matcher.group(1));
				if (matcher.group(2).equals("cm")) {
					valid = h >= 150 && h <= 193;
				} else {
					valid = h >= 59 && h <= 76;
				}
				break;
			default:
				valid = true;
			}

			return valid;
		}
	}
}
