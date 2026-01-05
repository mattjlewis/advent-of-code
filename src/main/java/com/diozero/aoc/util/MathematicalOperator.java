package com.diozero.aoc.util;

import java.util.Arrays;
import java.util.List;

public enum MathematicalOperator {
	ADD, SUBTRACT, MULTIPLY, DIVIDE;

	public static MathematicalOperator of(String op) {
		return switch (op) {
		case "+" -> ADD;
		case "-" -> SUBTRACT;
		case "*" -> MULTIPLY;
		case "/" -> DIVIDE;
		default -> throw new IllegalArgumentException("Invalid op '" + op + "'");
		};
	}

	public int apply(int x, int y) {
		return switch (this) {
		case ADD -> x + y;
		case SUBTRACT -> x - y;
		case MULTIPLY -> x * y;
		case DIVIDE -> x / y;
		};
	}

	public long apply(long x, long y) {
		return switch (this) {
		case ADD -> x + y;
		case SUBTRACT -> x - y;
		case MULTIPLY -> x * y;
		case DIVIDE -> x / y;
		};
	}

	public int applyAsInt(List<Integer> numbers) {
		return numbers.stream().mapToInt(Integer::intValue).reduce(this::apply).getAsInt();
	}

	public int apply(int[] numbers) {
		return Arrays.stream(numbers).reduce(this::apply).getAsInt();
	}

	public long applyAsLong(List<Long> numbers) {
		return numbers.stream().mapToLong(Long::longValue).reduce(this::apply).getAsLong();
	}

	public long apply(long[] numbers) {
		return Arrays.stream(numbers).reduce(this::apply).getAsLong();
	}
}
