package com.diozero.aoc.y2021;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.Point3D;

/**
 * Did not enjoy this one!
 */
public class Day19 extends Day {
	public static void main(String[] args) {
		new Day19().run();
	}

	@Override
	public String name() {
		return "Beacon Scanner";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Scanner> scanners = loadData(input);
		Logger.debug("scanners: {}", scanners);

		return Integer.toString(getBeacons(scanners).size());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Point3D> scanner_locations = getScannerLocations(loadData(input));

		return Integer.toString(scanner_locations.stream()
				.flatMapToInt(a -> scanner_locations.stream().mapToInt(b -> a.manhattanDistance(b))).max().getAsInt());
	}

	private static List<Scanner> loadData(final Path input) throws IOException {
		final Pattern scanner_pattern = Pattern.compile("--- scanner (\\d+) ---");

		final List<Scanner> scanners = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(input.toFile()))) {
			int scanner_id = -1;
			List<Point3D> points = new ArrayList<>();
			while (true) {
				String line = br.readLine();
				if (line == null) {
					scanners.add(new Scanner(Integer.toString(scanner_id), points, null));
					break;
				}

				if (line.isBlank()) {
					scanners.add(new Scanner(Integer.toString(scanner_id), points, null));
					scanner_id = -1;
					points = new ArrayList<>();
				} else if (scanner_id == -1) {
					Matcher m = scanner_pattern.matcher(line);
					if (!m.matches()) {
						throw new IllegalArgumentException(
								"Expecting line matching '" + scanner_pattern.pattern() + "'");
					}
					scanner_id = Integer.parseInt(m.group(1));
					points = new ArrayList<>();
				} else {
					String[] parts = line.split(",");
					points.add(new Point3D(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
							parts.length == 2 ? 0 : Integer.parseInt(parts[2])));
				}
			}
		}

		return scanners;
	}

	private static Optional<ScannerTranslation> findTranslation(final int minDelta, final List<Point3D> scanner1Beacons,
			final Scanner scanner2) {
		Optional<ScannerTranslation> translation = Optional.empty();

		Map<Point3D, AtomicInteger> delta_counts = new HashMap<>();
		for (Scanner scanner2_variation : scanner2.calculateOrientationVariations()) {
			for (Point3D s2v_beacon : scanner2_variation.beacons()) {
				// Find the most common delta that is at least as frequent as minDelta

				// 1. Compute the distance from this scanner2 variation's beacon to every beacon
				// in scanner1
				for (Point3D s1_beacon : scanner1Beacons) {
					Logger.trace("{}.distanceTo({}): {}", s2v_beacon, s1_beacon, s2v_beacon.delta(s1_beacon));
					delta_counts.computeIfAbsent(s2v_beacon.delta(s1_beacon), r -> new AtomicInteger()).addAndGet(1);
				}
			}

			Logger.trace("delta_counts: {}", delta_counts);
			// 2. Are the any distance delta counts greater than the minimum?
			delta_counts = delta_counts.entrySet().stream().filter(e -> e.getValue().get() >= minDelta)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			Logger.trace("delta_counts: {}", delta_counts);

			if (!delta_counts.isEmpty()) {
				Logger.debug("Found a match to scanner #{}", scanner2.id());
				Point3D delta = delta_counts.entrySet().stream()
						.max((a, b) -> Integer.compare(a.getValue().get(), b.getValue().get())).map(Map.Entry::getKey)
						.orElseThrow();
				translation = Optional
						.of(new ScannerTranslation(scanner2_variation, delta, delta_counts.get(delta).get()));

				break;
			}

			delta_counts.clear();
		}

		return translation;
	}

	private static List<Point3D> getBeacons(final List<Scanner> scanners) {
		final int min_match_count = Math.min(scanners.get(0).beacons().size(), 12);

		do {
			a: for (int i = 0; i < scanners.size() - 1; i++) {
				Scanner scanner1 = scanners.get(i);
				// Find another scanner with at least 12 overlapping beacons
				for (int j = i + 1; j < scanners.size(); j++) {
					Scanner scanner2 = scanners.get(j);

					Optional<ScannerTranslation> st_opt = findTranslation(min_match_count, scanner1.beacons(),
							scanner2);
					Logger.debug("translation i={} j={} (scanner #{} - scanner #{}): {}", i, j, scanner1.id(),
							scanner2.id(), st_opt.map(st -> st.translation().toString()).orElse("none"));

					if (st_opt.isPresent()) {
						ScannerTranslation st = st_opt.get();
						Logger.debug("st.count(): {}", st.count());

						// Translate each beacon in the realigned scanner and add to scanner1
						st.scanner().beacons().forEach(b -> scanner1.addIfUnique(b.translate(st.translation())));

						// Remove scanner2 as it has been merged into scanner1
						scanners.remove(j);

						continue a;
					}
				}
			}
		} while (scanners.size() > 1);

		if (Logger.isDebugEnabled()) {
			List<Point3D> sorted_beacons = scanners.get(0).beacons.stream()
					.sorted((p1, p2) -> Integer.compare(p1.x(), p2.x())).toList();
			sorted_beacons.forEach(b -> System.out.format("%d,%d,%d%n", b.x(), b.y(), b.z()));
		}

		return scanners.get(0).beacons();
	}

	private static List<Point3D> getScannerLocations(final List<Scanner> scanners) {
		final int min_match_count = Math.min(scanners.get(0).beacons().size(), 12);

		final List<Scanner> to_align = new ArrayList<>(scanners);
		final List<Scanner> aligned = new ArrayList<>();

		// Use scanner0 as the alignment reference
		final Scanner scanner0 = to_align.remove(0);
		aligned.add(new Scanner(scanner0.id(), scanner0.beacons(), new Point3D(0, 0, 0)));

		// Loop until all scanners aligned
		a: while (!to_align.isEmpty()) {
			// Use aligned scanners as source
			for (Scanner aligned_scanner : aligned) {
				for (Scanner not_aligned_scanner : to_align) {
					Optional<ScannerTranslation> opt_st = findTranslation(min_match_count, aligned_scanner.beacons(),
							not_aligned_scanner);
					if (opt_st.isPresent()) {
						ScannerTranslation st = opt_st.get();
						aligned.add(new Scanner(st.scanner().id(), st.scanner().beacons(),
								st.translation().translate(aligned_scanner.translation())));
						to_align.remove(not_aligned_scanner);
						continue a;
					}
				}
			}
		}
		Logger.debug("aligned: {}", aligned);
		Logger.debug("original list size: {}, to_align list size: {}, aligned list size: {}", scanners.size(),
				to_align.size(), aligned.size());

		// Now that they are all aligned, populate the translation vectors
		List<Point3D> translations = aligned.stream().map(scanner -> scanner.translation()).toList();

		Logger.debug("translations: {}", translations);

		return translations;
	}

	private static record Scanner(String id, List<Point3D> beacons, Point3D translation) {
		public List<Scanner> calculateOrientationVariations() {
			List<Scanner> variations = new ArrayList<>();

			// There are 24 possible cube face orientations - each of the 6 faces can have 4
			// rotational positions
			for (int rotation = 0; rotation < 4; rotation++) {
				for (int face = 0; face < 6; face++) {
					final int f = face;
					final int r = rotation;
					variations.add(new Scanner(id + "-" + rotation + "-" + face,
							beacons.stream().map(point -> orient(point, f, r)).toList(), null));
				}
			}

			return variations;
		}

		public void addIfUnique(Point3D beacon) {
			if (!beacons.contains(beacon)) {
				Logger.debug("Adding beacon {}", beacon);
				beacons.add(beacon);
			}
		}

		public static Point3D orient(Point3D point, int face, int rotation) {
			return switch (face) {
			case 0 -> point.rotate(Point3D.Axis.X, rotation);
			case 1 -> point.rotate(Point3D.Axis.Z, 1).rotate(Point3D.Axis.X, rotation);
			case 2 -> point.rotate(Point3D.Axis.Z, 2).rotate(Point3D.Axis.X, rotation);
			case 3 -> point.rotate(Point3D.Axis.Z, 3).rotate(Point3D.Axis.X, rotation);
			case 4 -> point.rotate(Point3D.Axis.Y, 1).rotate(Point3D.Axis.X, rotation);
			case 5 -> point.rotate(Point3D.Axis.Y, 3).rotate(Point3D.Axis.X, rotation);
			default -> throw new IllegalArgumentException("Invalid face value");
			};
		}
	}

	private static record ScannerTranslation(Scanner scanner, Point3D translation, int count) {
		//
	}
}
