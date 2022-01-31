package com.diozero.aoc.util;

@FunctionalInterface
public interface ToCharFunction<T> {
	/**
	 * Applies this function to the given argument.
	 *
	 * @param value the function argument
	 * @return the function result
	 */
	char applyAsChar(T value);
}
