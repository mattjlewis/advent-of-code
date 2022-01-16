package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day20 extends Day {
	public static void main(String[] args) {
		new Day20().run();
	}

	@Override
	public String name() {
		return "Trench Map";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Image image = loadData(input);
		printImage(image);

		image.enhance(2);
		printImage(image);

		return Integer.toString(image.getPixels().size());
	}

	@Override
	public String part2(Path input) throws IOException {
		final Image image = loadData(input);
		printImage(image);

		image.enhance(50);
		printImage(image);

		return Integer.toString(image.getPixels().size());
	}

	private static void printImage(Image image) {
		if (!Logger.isDebugEnabled()) {
			return;
		}

		System.out.format("%d, %d to %d, %d%n", image.minX, image.minY, image.maxX, image.maxY);

		for (int y = image.minY; y <= image.maxY; y++) {
			for (int x = image.minX; x <= image.maxX; x++) {
				System.out.print(image.getPixels().contains(new Point2D(x, y)) ? "#" : ".");
			}
			System.out.println();
		}
	}

	private static Image loadData(Path input) throws IOException {
		return Image.create(Files.lines(input).findFirst().orElseThrow(), Files.lines(input).skip(2).toList());
	}

	/**
	 * A class rather than a record so that it can be mutated
	 */
	private static class Image {
		public static Image create(String algorithmLine, List<String> imageRows) {
			boolean[] algorithm = TextParser.toBooleanArray(algorithmLine, '#');

			Set<Point2D> pixels = new HashSet<>();
			for (int y = 0; y < imageRows.size(); y++) {
				String row = imageRows.get(y);
				for (int x = 0; x < row.length(); x++) {
					if (row.charAt(x) == '#') {
						pixels.add(new Point2D(x, y));
					}
				}
			}

			return new Image(algorithm, pixels);
		}

		private final boolean[] algorithm;
		/** Note that the actual image size is infinite */
		private Set<Point2D> pixels;
		private int minX;
		private int minY;
		private int maxX;
		private int maxY;
		/**
		 * If the first bit of the algorithm is set then every pixel outside of the
		 * current pixel bounds will be toggled on every iteration
		 */
		private boolean background;

		public Image(boolean[] algorithm, Set<Point2D> pixels) {
			this.algorithm = algorithm;
			setPixels(pixels);
		}

		public Set<Point2D> getPixels() {
			return pixels;
		}

		public void enhance(int iterations) {
			for (int i = 0; i < iterations; i++) {
				enhance();
				if (Logger.isTraceEnabled()) {
					printImage(this);
				}
			}
		}

		public void enhance() {
			final Set<Point2D> new_pixels = new HashSet<>();

			for (int y = minY - 2; y <= maxY + 2; y++) {
				for (int x = minX - 2; x <= maxX + 2; x++) {
					enhancePixel(new Point2D(x, y)).ifPresent(new_pixels::add);
				}
			}

			setPixels(new_pixels);
			background = algorithm[0] & !background;
		}

		private void setPixels(Set<Point2D> pixels) {
			this.pixels = pixels;
			updateBounds();
		}

		private void updateBounds() {
			// Assume this is quicker than streaming over the pixels set 4 times
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

		private Optional<Point2D> enhancePixel(Point2D pixel) {
			int algorithm_pos = 0;
			for (int y = pixel.y() - 1; y <= pixel.y() + 1; y++) {
				for (int x = pixel.x() - 1; x <= pixel.x() + 1; x++) {
					algorithm_pos <<= 1;
					algorithm_pos |= isPixelSet(x, y) ? 1 : 0;
				}
			}

			return algorithm[algorithm_pos] ? Optional.of(pixel) : Optional.empty();
		}

		private boolean isPixelSet(int x, int y) {
			if (x < minX || x > maxX || y < minY || y > maxY) {
				return background;
			}
			return pixels.contains(new Point2D(x, y));
		}
	}
}
