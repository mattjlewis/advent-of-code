package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day21 extends Day {
	private static final String ROOT_ID = "root";
	private static final String HUMN_ID = "humn";

	public static void main(String[] args) {
		new Day21().run();
	}

	@Override
	public String name() {
		return "Monkey Math";
	}

	@Override
	public String part1(Path input) throws IOException {
		final Map<String, Job> monkeys = Files.lines(input).map(Job::load)
				.collect(Collectors.toMap(Job::id, Function.identity()));

		return Long.toString(monkeys.get(ROOT_ID).yell(monkeys));
	}

	@Override
	public String part2(Path input) throws IOException {
		final Map<String, Job> monkeys = Files.lines(input).map(Job::load)
				.collect(Collectors.toMap(Job::id, Function.identity()));

		monkeys.values().stream().filter(m -> !m.isInHumnBranch(monkeys)).forEach(j -> j.yell(monkeys));

		final OperationJob root = (OperationJob) monkeys.get(ROOT_ID);
		final OperationJob root_left = (OperationJob) monkeys.get(root.left);
		final OperationJob root_right = (OperationJob) monkeys.get(root.right);

		OperationJob current;
		long humn_number;
		if (root_right.isInHumnBranch(monkeys)) {
			humn_number = root_left.yell(monkeys);
			current = root_right;
		} else {
			humn_number = root_right.yell(monkeys);
			current = root_left;
		}

		while (true) {
			final Job left_term = monkeys.get(current.left);
			final Job right_term = monkeys.get(current.right);

			Job next;
			if (left_term.isInHumnBranch(monkeys)) {
				switch (current.operator) {
				case ADD -> humn_number -= right_term.yell(monkeys);
				case SUBTRACT -> humn_number += right_term.yell(monkeys);
				case MULTIPLY -> humn_number /= right_term.yell(monkeys);
				case DIVIDE -> humn_number *= right_term.yell(monkeys);
				default -> throw new IllegalArgumentException("Unexpected value: " + current.operator);
				}

				next = left_term;
			} else {
				switch (current.operator) {
				case ADD -> humn_number -= left_term.yell(monkeys);
				case SUBTRACT -> humn_number = left_term.yell(monkeys) - humn_number;
				case MULTIPLY -> humn_number /= left_term.yell(monkeys);
				case DIVIDE -> humn_number *= left_term.yell(monkeys);
				default -> throw new IllegalArgumentException("Unexpected value: " + current.operator);
				}

				next = right_term;
			}

			if (next.id().equals(HUMN_ID)) {
				break;
			}

			current = (OperationJob) next;
		}

		return Long.toString(humn_number);
	}

	private static interface Job {
		final Pattern FORMULA = Pattern.compile("(\\w+) ([+\\-\\*/]) (\\w+)");

		String id();

		long yell(Map<String, Job> jobs);

		default boolean isInHumnBranch(Map<String, Job> jobs) {
			if (id().equals(HUMN_ID)) {
				return true;
			}

			if (this instanceof OperationJob formula) {
				return jobs.get(formula.left).isInHumnBranch(jobs) || jobs.get(formula.right).isInHumnBranch(jobs);
			}

			return false;
		}

		public static Job load(String line) {
			final String[] parts = line.split(": ");
			final Matcher matcher = FORMULA.matcher(parts[1]);
			if (matcher.matches()) {
				return new OperationJob(parts[0], matcher.group(1), Operator.of(matcher.group(2)), matcher.group(3),
						OptionalLong.empty());
			}
			return new NumberJob(parts[0], Integer.parseInt(parts[1]));
		}
	}

	private static record NumberJob(String id, int result) implements Job {
		@Override
		public long yell(Map<String, Job> jobs) {
			return result;
		}
	}

	private static enum Operator {
		ADD, SUBTRACT, MULTIPLY, DIVIDE;

		public static Operator of(String op) {
			return switch (op) {
			case "+" -> ADD;
			case "-" -> SUBTRACT;
			case "*" -> MULTIPLY;
			case "/" -> DIVIDE;
			default -> throw new IllegalArgumentException("Invalid op '" + op + "'");
			};
		}
	}

	private static class OperationJob implements Job {
		private String id;
		private String left;
		private Operator operator;
		private String right;
		private OptionalLong value;

		public OperationJob(String id, String left, Operator operator, String right, OptionalLong value) {
			this.id = id;
			this.left = left;
			this.operator = operator;
			this.right = right;
			this.value = OptionalLong.empty();
		}

		@Override
		public String id() {
			return id;
		}

		@Override
		public long yell(Map<String, Job> jobs) {
			if (value.isEmpty()) {
				final Job left_job = jobs.get(left);
				final Job right_job = jobs.get(right);

				final long result = switch (operator) {
				case ADD -> left_job.yell(jobs) + right_job.yell(jobs);
				case SUBTRACT -> left_job.yell(jobs) - right_job.yell(jobs);
				case MULTIPLY -> left_job.yell(jobs) * right_job.yell(jobs);
				case DIVIDE -> left_job.yell(jobs) / right_job.yell(jobs);
				};

				value = OptionalLong.of(result);
			}

			return value.getAsLong();
		}
	}
}
