package com.diozero.aoc;

import java.util.stream.IntStream;

public class Main {
	public static void main(String[] args) {
		String[] years = { "2020", "2021" };
		for (String year : years) {
			try {
				Main main = (Main) Class.forName(Main.class.getPackageName() + ".y" + year + ".Main" + year)
						.getDeclaredConstructor().newInstance();
				main.run();
			} catch (Exception e) {
				// Ignore
			}
		}
	}

	public final AocBase instantiateDay(int day) {
		try {
			return (AocBase) Class.forName(getClass().getPackageName() + ".Day" + day).getDeclaredConstructor()
					.newInstance();
		} catch (Exception e) {
			// Ignore
		}
		return null;
	}

	public final void run() {
		IntStream.range(1, 24).mapToObj(this::instantiateDay).filter(day -> day != null).forEach(AocBase::run);
	}
}
