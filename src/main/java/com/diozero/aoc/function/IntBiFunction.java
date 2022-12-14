package com.diozero.aoc.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface IntBiFunction<T> {
	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t the first function argument
	 * @param u the second function argument
	 * @return the function result
	 */
	T apply(int t, int u);

	/**
	 * Returns a composed function that first applies this function to its input,
	 * and then applies the {@code after} function to the result. If evaluation of
	 * either function throws an exception, it is relayed to the caller of the
	 * composed function.
	 *
	 * @param <V>   the type of output of the {@code after} function, and of the
	 *              composed function
	 * @param after the function to apply after this function is applied
	 * @return a composed function that first applies this function and then applies
	 *         the {@code after} function
	 * @throws NullPointerException if after is null
	 */
	default <V> IntBiFunction<V> andThen(Function<? super T, ? extends V> after) {
		Objects.requireNonNull(after);
		return (int t, int u) -> after.apply(apply(t, u));
	}
}
