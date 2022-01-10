package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diozero.aoc.AocBase;

public class Day16 extends AocBase {
	public static void main(String[] args) {
		new Day16().run();
	}

	@Override
	public String part1(Path input) throws IOException {
		final PuzzleInput puzzle_input = loadData(input);

		// Sum the values of each invalid value in all nearby ticket
		return Integer.toString(puzzle_input.nearbyTickets().stream().flatMapToInt(t -> Arrays.stream(t))
				.filter(i -> !isValid(i, puzzle_input.fields())).sum());
	}

	@Override
	public String part2(Path input) throws IOException {
		final PuzzleInput puzzle_input = loadData(input);

		// First discard the invalid tickets
		final List<int[]> valid_nearby_tickets = puzzle_input.nearbyTickets().stream()
				.filter(ticket -> isValid(ticket, puzzle_input.fields())).toList();

		// Create a map of all matching fields for each ticket value
		final Map<Integer, List<Field>> matches = new HashMap<>();
		for (int i = 0; i < puzzle_input.fields().size(); i++) {
			for (Field field : puzzle_input.fields()) {
				// Does this field match all values at index i?
				if (matchesAll(i, field, valid_nearby_tickets)) {
					matches.computeIfAbsent(Integer.valueOf(i), a -> new ArrayList<>()).add(field);
				}
			}
		}

		// Map from field to ticket value
		final Map<Field, Integer> unique_matches = new HashMap<>();
		// Loop until all unique matching field values have been resolved
		while (!matches.isEmpty()) {

			for (Iterator<Entry<Integer, List<Field>>> it = matches.entrySet().iterator(); it.hasNext();) {
				Entry<Integer, List<Field>> match = it.next();
				List<Field> fields = match.getValue();
				// Found a unique field?
				if (fields.size() == 1) {
					Field field = fields.get(0);
					unique_matches.put(field, match.getKey());
					// Remove this entry from the matches map
					it.remove();
					// Remove the resolved field from each remaining entry in matches
					matches.values().forEach(l -> l.remove(field));
				}
			}
		}

		final int[] my_ticket = puzzle_input.myTicket();

		return Long
				.toString(unique_matches.entrySet().stream().filter(e -> e.getKey().category().startsWith("departure"))
						.mapToLong(e -> my_ticket[e.getValue().intValue()]).reduce(1, (a, b) -> a * b));
	}

	private static boolean matchesAll(int index, Field field, List<int[]> tickets) {
		boolean match = true;
		for (int[] ticket : tickets) {
			if (!field.matches(ticket[index])) {
				match = false;
				break;
			}
		}
		return match;
	}

	private static PuzzleInput loadData(Path input) throws IOException {
		List<Field> fields = new ArrayList<>();
		int[] my_ticket = {};
		List<int[]> nearby_tickets = new ArrayList<>();

		int state = 0;
		for (String line : Files.readAllLines(input)) {
			if (line.isBlank()) {
				state++;
				continue;
			}

			if (line.equals("your ticket:") || line.equals("nearby tickets:")) {
				continue;
			}

			switch (state) {
			case 0:
				fields.add(Field.parse(line));
				break;
			case 1:
				my_ticket = Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
				break;
			case 2:
				nearby_tickets.add(Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray());
				break;
			default:
				throw new IllegalArgumentException("Invalid input data, state: " + state);
			}
		}

		return new PuzzleInput(fields, my_ticket, nearby_tickets);
	}

	private static boolean isValid(int[] ticketValues, List<Field> fields) {
		// Each ticket value must match at least one field
		for (int value : ticketValues) {
			if (!isValid(value, fields)) {
				return false;
			}
		}

		return true;
	}

	private static boolean isValid(int ticketValue, List<Field> fields) {
		// Must match at least one field
		for (Field field : fields) {
			if (field.matches(ticketValue)) {
				return true;
			}
		}

		return false;
	}

	private static record PuzzleInput(List<Field> fields, int[] myTicket, List<int[]> nearbyTickets) {
		//
	}

	private static record Field(String category, List<Range> ranges) {
		private static final Pattern PATTERN = Pattern.compile("(.*): (\\w+)-(\\w+) or (\\w+)-(\\w+)");

		public static Field parse(String line) {
			Matcher m = PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Line '" + line + "' does not match pattern " + PATTERN.pattern());
			}

			return new Field(m.group(1), List.of(new Range(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))),
					new Range(Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)))));
		}

		public boolean matches(int ticketValue) {
			// Return true if the ticketValue matches at least one field
			for (Range r : ranges) {
				if (r.matches(ticketValue)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public String toString() {
			return category + ": " + ranges;
		}
	}

	private static record Range(int start, int end) {
		public boolean matches(int ticketValue) {
			return ticketValue >= start && ticketValue <= end;
		}

		@Override
		public String toString() {
			return start + ".." + end;
		}
	}
}
