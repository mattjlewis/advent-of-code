package com.diozero.aoc;

import java.util.stream.IntStream;

public abstract class Year implements Runnable {
	private int year;

	protected Year() {
		year = Integer.parseInt(getClass().getPackageName().split("\\.(?=[^.]*$)")[1].substring(1));
	}

	@Override
	public final void run() {
		System.out.format("--- %d '%s' ---%n", Integer.valueOf(year), name());
		final int score = IntStream.range(1, 26).mapToObj(this::instantiateDay).filter(day -> day != null)
				.mapToInt(Day::run).sum();
		System.out.format("--- %d '%s' : Score: %d/50 ---%n", Integer.valueOf(year), name(), Integer.valueOf(score));
		System.out.println();
	}

	final Day instantiateDay(int day) {
		try {
			return (Day) Class.forName(getClass().getPackageName() + ".Day" + day).getDeclaredConstructor()
					.newInstance();
		} catch (Exception e) {
			// Ignore
		}
		return null;
	}

	public abstract String name();
}
