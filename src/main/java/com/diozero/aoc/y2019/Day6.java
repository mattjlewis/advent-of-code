package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day6 extends Day {
	public static void main(String[] args) {
		new Day6().run();
	}

	@Override
	public String name() {
		return "Universal Orbit Map";
	}

	@Override
	public String part1(Path input) throws IOException {
		Map<String, String> body_orbits = loadData(input);

		return Integer
				.toString(body_orbits.keySet().stream().mapToInt(body -> calcOrbitCounts(body_orbits, body)).sum());
	}

	@Override
	public String part2(Path input) throws IOException {
		Map<String, String> body_orbits = loadData(input);

		List<String> parent_of_you_path = getPathToCentreOfMass(body_orbits, body_orbits.get("YOU"));
		List<String> parent_of_san_path = getPathToCentreOfMass(body_orbits, body_orbits.get("SAN"));

		/*
		 * Find the first body in the path from the parent body of YOU to COM that is
		 * also in the path from the parent body of SAN to COM. That is the first common
		 * ancestor.
		 */
		String common_ancestor = parent_of_you_path.stream().filter(parent_of_san_path::contains).findFirst()
				.orElseThrow();

		// Now it is as simple as adding together the index of the common ancestor in
		// the two paths
		return Integer
				.toString(parent_of_you_path.indexOf(common_ancestor) + parent_of_san_path.indexOf(common_ancestor));
	}

	private static Map<String, String> loadData(Path input) throws IOException {
		// Map from body to the body it is orbiting
		return Files.lines(input).map(line -> line.split("\\)"))
				.collect(Collectors.toMap(parts -> parts[1], parts -> parts[0]));

	}

	private static int calcOrbitCounts(Map<String, String> bodyOrbits, String body) {
		String other = body;
		int count = -1;
		do {
			other = bodyOrbits.get(other);
			count++;
		} while (other != null);

		return count;
	}

	private static List<String> getPathToCentreOfMass(Map<String, String> bodyOrbits, String start) {
		List<String> path = new ArrayList<>();

		String body = start;
		do {
			path.add(body);
			body = bodyOrbits.get(body);
		} while (body != null);

		return path;
	}
}
