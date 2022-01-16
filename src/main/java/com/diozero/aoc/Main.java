package com.diozero.aoc;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.tinylog.Logger;

public class Main {
	public static void main(String[] args) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				Main.class.getClassLoader().getResourceAsStream(Main.class.getPackageName().replaceAll("[.]", "/"))))) {
			br.lines().filter(s -> !s.endsWith(".class")).filter(s -> s.startsWith("y"))
					.mapToInt(s -> Integer.parseInt(s.substring(1))).mapToObj(Main::instantiateYear).forEach(Year::run);
		} catch (Exception e) {
			Logger.error(e, "Error: {}", e);
		}
	}

	static Year instantiateYear(int year) {
		try {
			return (Year) Class.forName(Main.class.getPackageName() + ".y" + year + ".Year" + year)
					.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			Logger.error(e, "Error: {}", e);
		}
		return null;
	}
}
