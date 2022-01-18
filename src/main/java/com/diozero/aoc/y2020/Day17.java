package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point3D;
import com.diozero.aoc.geometry.Point4D;

public class Day17 extends Day {
	public static void main(String[] args) {
		new Day17().run();
	}

	@Override
	public String name() {
		return "Conway Cubes";
	}

	@Override
	public String part1(final Path input) throws IOException {
		List<Point3D> cubes = new ArrayList<>();
		int y = 0;
		for (String line : Files.readAllLines(input)) {
			for (int x = 0; x < line.length(); x++) {
				if (line.charAt(x) == '#') {
					cubes.add(new Point3D(x, y, 0));
				}
			}
			y++;
		}

		int min_x, max_x, min_y, max_y, min_z, max_z;
		for (int step = 0; step < 6; step++) {
			min_x = Integer.MAX_VALUE;
			max_x = Integer.MIN_VALUE;
			min_y = Integer.MAX_VALUE;
			max_y = Integer.MIN_VALUE;
			min_z = Integer.MAX_VALUE;
			max_z = Integer.MIN_VALUE;
			for (Point3D cube : cubes) {
				min_x = Math.min(min_x, cube.x());
				max_x = Math.max(max_x, cube.x());
				min_y = Math.min(min_y, cube.y());
				max_y = Math.max(max_y, cube.y());
				min_z = Math.min(min_z, cube.z());
				max_z = Math.max(max_z, cube.z());
			}

			// All cubes simultaneously change their state according to the following rules:
			List<Point3D> new_cubes = new ArrayList<>();
			for (int z = min_z - 1; z <= max_z + 1; z++) {
				for (y = min_y - 1; y <= max_y + 1; y++) {
					for (int x = min_x - 1; x <= max_x + 1; x++) {
						Point3D cube = new Point3D(x, y, z);
						int active_neighbours = (int) cubes.stream().filter(c -> c.isAdjacentTo(cube)).count();
						if (cubes.contains(cube)) {
							/*
							 * If a cube is active and exactly 2 or 3 of its neighbours are also active, the
							 * cube remains active. Otherwise, the cube becomes inactive.
							 */
							if (active_neighbours == 2 || active_neighbours == 3) {
								new_cubes.add(cube);
							}
						} else if (active_neighbours == 3) {
							/*
							 * If a cube is inactive but exactly 3 of its neighbours are active, the cube
							 * becomes active. Otherwise, the cube remains inactive.
							 */
							new_cubes.add(cube);
						}
					}
				}
			}

			cubes = new_cubes;
		}

		return Integer.toString(cubes.size());

	}

	@Override
	public String part2(Path input) throws IOException {
		List<Point4D> cubes = new ArrayList<>();
		int y = 0;
		for (String line : Files.readAllLines(input)) {
			for (int x = 0; x < line.length(); x++) {
				if (line.charAt(x) == '#') {
					cubes.add(new Point4D(x, y, 0, 0));
				}
			}
			y++;
		}

		int min_x, max_x, min_y, max_y, min_z, max_z, min_w, max_w;
		for (int step = 0; step < 6; step++) {
			min_x = Integer.MAX_VALUE;
			max_x = Integer.MIN_VALUE;
			min_y = Integer.MAX_VALUE;
			max_y = Integer.MIN_VALUE;
			min_z = Integer.MAX_VALUE;
			max_z = Integer.MIN_VALUE;
			min_w = Integer.MAX_VALUE;
			max_w = Integer.MIN_VALUE;
			for (Point4D cube : cubes) {
				min_x = Math.min(min_x, cube.x());
				max_x = Math.max(max_x, cube.x());
				min_y = Math.min(min_y, cube.y());
				max_y = Math.max(max_y, cube.y());
				min_z = Math.min(min_z, cube.z());
				max_z = Math.max(max_z, cube.z());
				min_w = Math.min(min_w, cube.w());
				max_w = Math.max(max_w, cube.w());
			}
			Logger.debug(
					"min_x: {}, max_x: {}, min_y: {}, max_y: {}, min_z: {}, max_z: {}, min_w: {}, max_w: {}, size: {}",
					min_x, max_x, min_y, max_y, min_z, max_z, min_w, max_w,
					(max_x - min_x) * (max_y - min_y) * (max_z - min_z) * (max_w - min_w));

			// All cubes simultaneously change their state according to the following rules:
			List<Point4D> new_cubes = new ArrayList<>();
			for (int w = min_w - 1; w <= max_w + 1; w++) {
				for (int z = min_z - 1; z <= max_z + 1; z++) {
					for (y = min_y - 1; y <= max_y + 1; y++) {
						for (int x = min_x - 1; x <= max_x + 1; x++) {
							Point4D cube = new Point4D(x, y, z, w);
							int active_neighbours = (int) cubes.stream().filter(c -> c.isAdjacentTo(cube)).count();
							if (cubes.contains(cube)) {
								/*
								 * If a cube is active and exactly 2 or 3 of its neighbours are also active, the
								 * cube remains active. Otherwise, the cube becomes inactive.
								 */
								if (active_neighbours == 2 || active_neighbours == 3) {
									new_cubes.add(cube);
								}
							} else if (active_neighbours == 3) {
								/*
								 * If a cube is inactive but exactly 3 of its neighbours are active, the cube
								 * becomes active. Otherwise, the cube remains inactive.
								 */
								new_cubes.add(cube);
							}
						}
					}
				}
			}

			cubes = new_cubes;
			Logger.debug("cubes.size(): {}", cubes.size());
		}

		return Integer.toString(cubes.size());
	}
}
