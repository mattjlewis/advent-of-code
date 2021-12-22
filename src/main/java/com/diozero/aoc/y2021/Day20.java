package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.diozero.aoc.AocBase;
import com.diozero.aoc.util.Point2D;

public class Day20 extends AocBase {
	public static void main(String[] args) {
		new Day20().run();
	}

	private static void printImage(Image image) {
		System.out.format("%d, %d to %d, %d%n", image.minX, image.minY, image.maxX, image.maxY);

		for (int y = image.minY; y <= image.maxY; y++) {
			for (int x = image.minX; x <= image.maxX; x++) {
				System.out.print(image.getPixels().contains(new Point2D(x, y)) ? "#" : ".");
			}
			System.out.println();
		}
	}

	private static Puzzle loadData(Path input) throws IOException {
		boolean[] algorithm = extractBits(Files.lines(input).findFirst().orElseThrow());
		return new Puzzle(algorithm, Image.create(Files.lines(input).skip(2).toList()));
	}

	@Override
	public long part1(Path input) throws IOException {
		Puzzle puzzle = loadData(input);
		Image image = puzzle.image();
		printImage(image);

		// Two iterations
		for (int i = 0; i < 2; i++) {
			image.enhance(puzzle.algorithm);
			printImage(image);
		}

		return image.getPixels().size();
	}

	@Override
	public long part2(Path input) throws IOException {
		Puzzle puzzle = loadData(input);
		Image image = puzzle.image();
		printImage(image);

		// 50 iterations
		for (int i = 0; i < 50; i++) {
			image.enhance(puzzle.algorithm);
			// printImage(image);
		}

		return image.getPixels().size();
	}

	private static boolean[] extractBits(String line) {
		final char[] chars = line.toCharArray();
		final boolean[] data = new boolean[chars.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = chars[i] == '#' ? true : false;
		}

		return data;
	}

	private static record Puzzle(boolean[] algorithm, Image image) {
		//
	}

	/**
	 * A class rather than a record so that bounds can be calculated
	 */
	private static class Image {
		public static Image create(List<String> rows) {
			Set<Point2D> pixels = new HashSet<>();
			for (int y = 0; y < rows.size(); y++) {
				String row = rows.get(y);
				for (int x = 0; x < row.length(); x++) {
					if (row.charAt(x) == '#') {
						pixels.add(new Point2D(x, y));
					}
				}
			}

			return new Image(pixels);
		}

		private Set<Point2D> pixels;
		private int minX;
		private int minY;
		private int maxX;
		private int maxY;
		private boolean background;

		public Image(Set<Point2D> pixels) {
			setPixels(pixels);
		}

		public Set<Point2D> getPixels() {
			return pixels;
		}

		private void setPixels(Set<Point2D> pixels) {
			this.pixels = pixels;
			updateBounds();
		}

		private void updateBounds() {
			minX = Integer.MAX_VALUE;
			maxX = Integer.MIN_VALUE;
			minY = Integer.MAX_VALUE;
			maxY = Integer.MIN_VALUE;
			for (Point2D pixel : pixels) {
				minX = Math.min(minX, pixel.x());
				maxX = Math.max(maxX, pixel.x());
				minY = Math.min(minY, pixel.y());
				maxY = Math.max(maxY, pixel.y());
			}
		}

		public void enhance(boolean[] algorithm) {
			final Set<Point2D> new_pixels = new HashSet<>();
			for (int y = minY - 2; y <= maxY + 2; y++) {
				for (int x = minX - 2; x <= maxX + 2; x++) {
					enhancePixel(algorithm, new Point2D(x, y)).ifPresent(new_pixels::add);
				}
			}

			setPixels(new_pixels);

			// Ugly - check to see if all border pixels are set to determine background
			boolean all_borders = true;
			for (int y = minY; y <= maxY && all_borders; y++) {
				all_borders = pixels.contains(new Point2D(minX, y));
				if (all_borders) {
					all_borders = pixels.contains(new Point2D(maxX, y));
				}
			}
			for (int x = minX; x <= maxX & all_borders; x++) {
				all_borders = pixels.contains(new Point2D(x, minY));
				if (all_borders) {
					all_borders = pixels.contains(new Point2D(x, maxY));
				}
			}

			background = all_borders;
		}

		private Optional<Point2D> enhancePixel(boolean[] algorithm, Point2D pixel) {
			int algorithm_pos = 0;
			for (int y = pixel.y() - 1; y <= pixel.y() + 1; y++) {
				for (int x = pixel.x() - 1; x <= pixel.x() + 1; x++) {
					algorithm_pos <<= 1;
					algorithm_pos |= isPixelOrBackground(x, y) ? 1 : 0;
				}
			}

			return algorithm[algorithm_pos] ? Optional.of(pixel) : Optional.empty();
		}

		private boolean isPixelOrBackground(int x, int y) {
			if (x < minX || x > maxX || y < minY || y > maxY) {
				return background;
			}
			return pixels.contains(new Point2D(x, y));
		}
	}
}
