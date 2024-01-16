package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.IntRange;

public class Day19 extends Day {
	private static String VARIABLES = "xmas";
	private static final String START_WORKFLOW_ID = "in";
	private static final Pattern WORKFLOW_PATTERN = Pattern.compile("([a-z]+)\\{(.*)\\}");
	private static final Pattern RULE_PREDICATE_PATTERN = Pattern.compile("([" + VARIABLES + "])([<>])(\\d+)");
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("([xmas])=(\\d+)");
	private static final char LESS_THAN_CHAR = '<';
	private static final char GREATER_THAN_CHAR = '>';

	public static void main(String[] args) {
		new Day19().run();
	}

	@Override
	public String name() {
		return "Aplenty";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(load(input).calculateRating());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(load(input).calculateCombinations());
	}

	private static WorkflowSet load(Path input) throws IOException {
		final Map<String, Rule> workflows = new HashMap<>();
		final List<Map<Character, Integer>> variables_list = new ArrayList<>();

		boolean reading_workflows = true;
		for (String line : Files.readAllLines(input)) {
			if (line.isBlank()) {
				reading_workflows = false;
				continue;
			}

			if (reading_workflows) {
				WORKFLOW_PATTERN.matcher(line).results().findFirst().ifPresent(mr -> workflows.put(mr.group(1),
						(Rule) parseComponent(new LinkedList<>(List.of(mr.group(2).split("[,:]"))))));
			} else {
				variables_list.add(VARIABLE_PATTERN.matcher(line).results().collect(Collectors
						.toMap(mr -> Character.valueOf(mr.group(1).charAt(0)), mr -> Integer.valueOf(mr.group(2)))));
			}
		}

		return new WorkflowSet(workflows, variables_list);
	}

	private static Component parseComponent(Queue<String> queue) {
		final String component = queue.remove();
		// Is this a Rule or a Reference or Result?
		if (component.indexOf(LESS_THAN_CHAR) != -1 || component.indexOf(GREATER_THAN_CHAR) != -1) {
			// The true part is always a Reference or a Result, the false part can be any
			return RULE_PREDICATE_PATTERN.matcher(component).results().findFirst()
					.map(mr -> new Rule(mr, parseReferenceOrResult(queue.remove()), parseComponent(queue))).get();
		}

		return parseReferenceOrResult(component);
	}

	private static ReferenceOrResult parseReferenceOrResult(String component) {
		// XXX Cast to ReferenceOrResult to allow orElseGet to work
		return WorkflowResult.of(component).map(r -> (ReferenceOrResult) new Result(r))
				.orElseGet(() -> new Reference(component));
	}

	private static enum Comparator {
		LESS_THAN, GREATER_THAN;

		public static Comparator of(String op) {
			return switch (op.charAt(0)) {
			case LESS_THAN_CHAR -> LESS_THAN;
			case GREATER_THAN_CHAR -> GREATER_THAN;
			default -> throw new IllegalArgumentException("Invalid operator '" + op + "'");
			};
		}

		public boolean evaluate(int i, int j) {
			if (this == LESS_THAN) {
				return i < j;
			}
			if (this == GREATER_THAN) {
				return i > j;
			}
			return false;
		}
	}

	private static enum WorkflowResult {
		ACCEPTED, REJECTED;

		public static Optional<WorkflowResult> of(String s) {
			return switch (s) {
			case "A" -> Optional.of(WorkflowResult.ACCEPTED);
			case "R" -> Optional.of(WorkflowResult.REJECTED);
			default -> Optional.empty();
			};
		}
	}

	private static record WorkflowSet(Map<String, Rule> workflows, List<Map<Character, Integer>> variablesList) {
		public int calculateRating() {
			return variablesList.stream().filter(this::evaluate).flatMap(variables -> variables.values().stream())
					.mapToInt(Integer::intValue).sum();
		}

		public boolean evaluate(Map<Character, Integer> variables) {
			return workflows.get(START_WORKFLOW_ID).evaluate(workflows, variables) == WorkflowResult.ACCEPTED;
		}

		public long calculateCombinations() {
			final Map<Character, IntRange> variable_ranges = VARIABLES.chars()
					.mapToObj(i -> Character.valueOf((char) i))
					.collect(Collectors.toMap(Function.identity(), ch -> new IntRange(1, 4000)));

			return workflows.get(START_WORKFLOW_ID).countCombinations(workflows, variable_ranges);
		}
	}

	private static interface Component {
		public WorkflowResult evaluate(Map<String, Rule> workflows, Map<Character, Integer> variables);

		public long countCombinations(Map<String, Rule> workflows, Map<Character, IntRange> ranges);
	}

	private static record Rule(Character variable, Comparator comparator, int value, ReferenceOrResult whenTrue,
			Component whenFalse) implements Component {
		public Rule(MatchResult mr, ReferenceOrResult whenTrue, Component whenFalse) {
			this(Character.valueOf(mr.group(1).charAt(0)), Comparator.of(mr.group(2)), Integer.parseInt(mr.group(3)),
					whenTrue, whenFalse);
		}

		@Override
		public WorkflowResult evaluate(Map<String, Rule> workflows, Map<Character, Integer> variables) {
			return comparator.evaluate(variables.get(variable).intValue(), value)
					? whenTrue.evaluate(workflows, variables)
					: whenFalse.evaluate(workflows, variables);
		}

		@Override
		public long countCombinations(Map<String, Rule> workflows, Map<Character, IntRange> variableRanges) {
			final IntRange var_range = variableRanges.get(variable);

			final Map<Character, IntRange> true_var_ranges = new HashMap<>(variableRanges);
			if (comparator == Comparator.LESS_THAN) {
				// True - variable must be less than value
				true_var_ranges.put(variable,
						new IntRange(var_range.startInclusive(), Math.min(value - 1, var_range.endInclusive())));
				// False - variable must be greater than or equal to value
				variableRanges.put(variable,
						new IntRange(Math.max(value, var_range.startInclusive()), var_range.endInclusive()));
			} else {
				// True - variable must be greater than value
				true_var_ranges.put(variable,
						new IntRange(Math.max(value + 1, var_range.startInclusive()), var_range.endInclusive()));
				// False - variable must be less than or equal to value
				variableRanges.put(variable,
						new IntRange(var_range.startInclusive(), Math.min(value, var_range.endInclusive())));
			}

			return whenTrue.countCombinations(workflows, true_var_ranges)
					+ whenFalse.countCombinations(workflows, variableRanges);
		}

		@Override
		public String toString() {
			return variable.toString() + comparator + value + ":" + whenTrue + "," + whenFalse;
		}
	}

	private static interface ReferenceOrResult extends Component {
		//
	}

	private static record Reference(String workflowId) implements ReferenceOrResult {
		@Override
		public WorkflowResult evaluate(Map<String, Rule> workflows, Map<Character, Integer> variables) {
			return workflows.get(workflowId).evaluate(workflows, variables);
		}

		@Override
		public long countCombinations(Map<String, Rule> workflows, Map<Character, IntRange> ranges) {
			return workflows.get(workflowId).countCombinations(workflows, ranges);
		}

		@Override
		public String toString() {
			return workflowId;
		}
	}

	private static record Result(WorkflowResult result) implements ReferenceOrResult {
		@Override
		public WorkflowResult evaluate(Map<String, Rule> workflows, Map<Character, Integer> variables) {
			return result;
		}

		@Override
		public long countCombinations(Map<String, Rule> workflows, Map<Character, IntRange> ranges) {
			if (result == WorkflowResult.ACCEPTED) {
				return ranges.values().stream().mapToLong(r -> r.size()).reduce(1, (a, b) -> a * b);
			}

			return 0;
		}

		@Override
		public String toString() {
			return result == WorkflowResult.ACCEPTED ? "A" : "R";
		}
	}
}
