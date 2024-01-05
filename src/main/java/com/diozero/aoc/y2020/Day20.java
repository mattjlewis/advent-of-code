package com.diozero.aoc.y2020;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day20 extends Day {
	private static final char PIXEL = TextParser.SET_CHAR;
	private static final char BLANK = TextParser.UNSET_CHAR;
	private static final char SEA_HORSE = 'O';

	public static void main(String[] args) {
		new Day20().run();
	}

	@Override
	public String name() {
		return "Jurassic Jigsaw";
	}

	@Override
	public String part1(final Path input) throws IOException {
		// Multiply the id of each corner
		return Long.toString(loadData(input).stream().filter(tile -> tile.neighbours().size() == 2).mapToLong(Tile::id)
				.reduce(1, (a, b) -> a * b));
	}

	@Override
	public String part2(final Path input) throws IOException {
		// Load the list of unaligned tiles inc information on neighbouring tiles
		final List<Tile> unaligned_tiles_with_neighbours = loadData(input);

		// Extract the actual image by aligning all tiles from the top-left corner tile
		final int[][] image = extractImage(
				alignTiles(getTopLeftCorner(unaligned_tiles_with_neighbours), unaligned_tiles_with_neighbours));
		if (Logger.isDebugEnabled()) {
			print(image);
			System.out.println();
		}

		// The sea horse "image"
		final String[] sea_horse = new String[] { //
				"                  # ", //
				"#    ##    ##    ###", //
				" #  #  #  #  #  #   " };
		// Mark the occurrences of each possible orientation of the sea horse image
		getOrientations(Arrays.stream(sea_horse).map(Day20::extractImageData).toArray(int[][]::new))
				.forEach(orientation -> markOccurrences(orientation, image));
		if (Logger.isDebugEnabled()) {
			print(image);
		}

		/*
		 * Finally (!) determine how rough the waters are in the sea monsters' habitat
		 * by counting the number of '#' characters that are not part of a sea monster.
		 */
		return Integer.toString(countOccurrences(image, PIXEL));
	}

	private static List<Tile> loadData(final Path input) throws FileNotFoundException, IOException {
		final List<Tile> tiles = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(input.toFile()))) {
			Pattern pattern = Pattern.compile("Tile (\\d+):");
			int tile_number = -1;
			int[][] area = null;
			int line_number = 0;
			while (true) {
				String line = br.readLine();
				if (line == null || line.isBlank()) {
					tiles.add(new Tile(tile_number, area));

					if (line == null) {
						break;
					}

					tile_number = -1;
					area = null;
					line_number = 0;

					continue;
				}

				if (line.startsWith("Tile ")) {
					Matcher m = pattern.matcher(line);
					if (!m.matches()) {
						throw new IllegalArgumentException(
								"Line '" + line + "' does not match pattern " + pattern.pattern());
					}

					tile_number = Integer.parseInt(m.group(1));
				} else {
					if (area == null) {
						// FIXME Harcoded to 10
						area = new int[10][line.length()];
					}

					area[line_number++] = extractImageData(line);
				}
			}
		}

		populateNeighbours(tiles);

		return tiles;
	}

	private static void populateNeighbours(final List<Tile> tiles) {
		for (Tile tile : tiles) {
			// A tile can have a maximum of 4 neighbours
			if (tile.neighbours().size() == 4) {
				continue;
			}

			for (Tile.Edge edge : Tile.Edge.values()) {
				// Has this neighbour already been populated?
				if (tile.neighbours().containsKey(edge)) {
					continue;
				}

				final int[] tile_edge = tile.getEdge(edge);

				for (Tile other_tile : tiles) {
					if (tile == other_tile) {
						continue;
					}

					for (Tile.Edge other_edge : Tile.Edge.values()) {
						final int[] other_tile_edge = other_tile.getEdge(other_edge);
						if (Arrays.equals(tile_edge, other_tile_edge)
								|| Arrays.equals(tile_edge, reverse(other_tile_edge))) {
							tile.setNeighbour(edge, other_tile);
							other_tile.setNeighbour(other_edge, tile);
						}
					}
				}
			}
		}
	}

	private static int[] extractImageData(final String line) {
		return line.chars().toArray();
	}

	private static Tile getTopLeftCorner(final List<Tile> unalignedTilesWithNeighbours) {
		// Start with the first corner tile and align so that it is top-left
		final Tile start_corner = unalignedTilesWithNeighbours.stream().filter(tile -> tile.neighbours().size() == 2)
				.findFirst().orElseThrow();

		if (start_corner.neighbours().containsKey(Tile.Edge.TOP)
				&& start_corner.neighbours().containsKey(Tile.Edge.RIGHT)) {
			start_corner.rotateClockwise90();
		} else if (start_corner.neighbours().containsKey(Tile.Edge.TOP)
				&& start_corner.neighbours().containsKey(Tile.Edge.LEFT)) {
			start_corner.rotateClockwise90(2);
		} else if (start_corner.neighbours().containsKey(Tile.Edge.BOTTOM)
				&& start_corner.neighbours().containsKey(Tile.Edge.LEFT)) {
			start_corner.rotateClockwise90(3);
		}

		return start_corner;
	}

	private static int[] reverse(final int[] array) {
		final int[] reversed = new int[array.length];

		for (int i = 0; i < reversed.length; i++) {
			reversed[i] = array[array.length - i - 1];
		}

		return reversed;
	}

	private static Tile[][] alignTiles(final Tile topLeftCorner, final List<Tile> unalignedTilesWithNeighbours) {
		final int size = (int) Math.sqrt(unalignedTilesWithNeighbours.size());

		final Tile[][] aligned_tile_grid = new Tile[size][size];
		Tile tile;
		for (int y = 0; y < size; y++) {
			if (y == 0) {
				tile = topLeftCorner;
			} else {
				// Get and align the tile below grid[y-1][0];
				tile = aligned_tile_grid[y - 1][0].neighbours().get(Tile.Edge.BOTTOM)
						.alignTo(aligned_tile_grid[y - 1][0].getEdge(Tile.Edge.BOTTOM), Tile.Edge.TOP);
			}

			aligned_tile_grid[y][0] = tile;
			unalignedTilesWithNeighbours.remove(tile);

			for (int x = 1; x < size; x++) {
				// Align the left hand edge of the tile to right to this tile's right hand edge
				tile = tile.neighbours().get(Tile.Edge.RIGHT).alignTo(tile.getEdge(Tile.Edge.RIGHT), Tile.Edge.LEFT);

				aligned_tile_grid[y][x] = tile;
				unalignedTilesWithNeighbours.remove(tile);
			}
		}

		return aligned_tile_grid;
	}

	private static int[][] extractImage(final Tile[][] alignedTileGrid) {
		// Join into one big grid by removing the borders on each tile
		final int tile_width = alignedTileGrid[0][0].data.length - 2;
		final int tile_height = alignedTileGrid[0][0].data[0].length - 2;

		final int[][] image = new int[alignedTileGrid.length * tile_height][alignedTileGrid[0].length * tile_width];

		for (int tile_y = 0; tile_y < alignedTileGrid.length; tile_y++) {
			for (int tile_x = 0; tile_x < alignedTileGrid[tile_y].length; tile_x++) {
				int[][] tile_data = alignedTileGrid[tile_y][tile_x].data;
				for (int y = 1; y < tile_data.length - 1; y++) {
					for (int x = 1; x < tile_data[0].length - 1; x++) {
						image[tile_y * tile_height + y - 1][tile_x * tile_width + x - 1] = tile_data[y][x];
					}
				}
			}
		}

		return image;
	}

	private static List<Tile> getOrientations(final int[][] image) {
		// Calculate every possible orientation of image (8)
		final Tile orig = new Tile(0, image);
		final List<Tile> orientations = new ArrayList<>();

		int i = 0;
		for (Tile.Transformation transformation : Tile.ORIENTATIONS) {
			// Hack - relies on the transformation operations duplicating the image data
			Tile variation = new Tile(i++, orig.data);
			orientations.add(variation);

			transformation.transform(orig);
		}

		return orientations;
	}

	/**
	 * Count the occurrences of tile in sourceImage where the dimensions of tile is
	 * guaranteed to be less than sourceImage
	 *
	 * @param tile        the image to look for
	 * @param sourceImage the image to look in
	 * @return number of occurrences of tile in image
	 */
	private static void markOccurrences(final Tile tile, final int[][] sourceImage) {
		for (int y = 0; y < sourceImage.length - tile.data.length; y++) {
			for (int x = 0; x < sourceImage[0].length - tile.data[0].length; x++) {
				if (containsImage(sourceImage, x, y, tile.data)) {
					markOccurrence(tile.data, sourceImage, x, y);
				}
			}
		}
	}

	private static boolean containsImage(final int[][] sourceImage, final int startX, final int startY,
			final int[][] image) {
		for (int y = 0; y < image.length; y++) {
			for (int x = 0; x < image[0].length; x++) {
				// If the image pixel is set then the sourceImage pixel must also be set
				if (image[y][x] == PIXEL && sourceImage[y + startY][x + startX] == BLANK) {
					return false;
				}
			}
		}

		return true;
	}

	private static void markOccurrence(final int[][] image, final int[][] sourceImage, final int startX,
			final int startY) {
		for (int y = 0; y < image.length; y++) {
			for (int x = 0; x < image[0].length; x++) {
				if (image[y][x] == PIXEL) {
					sourceImage[y + startY][x + startX] = SEA_HORSE;
				}
			}
		}
	}

	private static int countOccurrences(final int[][] image, final char ch) {
		int count = 0;
		for (int y = 0; y < image.length; y++) {
			for (int x = 0; x < image[y].length; x++) {
				if (image[y][x] == ch) {
					count++;
				}
			}
		}

		return count;
	}

	private static void print(final int[][] data) {
		for (int y = 0; y < data.length; y++) {
			System.out.println(Tile.toString(data[y]));
		}
	}

	private static class Tile {
		enum Edge {
			TOP, RIGHT, BOTTOM, LEFT;
		}

		enum Transformation {
			R_90, FLIP_H, FLIP_V;

			public void transform(Tile tile) {
				switch (this) {
				case R_90:
					tile.rotateClockwise90();
					break;
				case FLIP_H:
					tile.flipHorizontalAxis();
					break;
				case FLIP_V:
					tile.flipVerticalAxis();
					break;
				default:
					throw new IllegalArgumentException("Illegal transformation " + this);
				}
			}
		}

		/*-
		 * Start -> FlipH -> R 90  -> FlipV -> R 90  -> FlipH -> R 90  -> FlipV -> R 90 (start)
		 * 1 2 3    7 8 9    1 4 7    7 4 1    9 8 7    3 2 1    9 6 3    3 6 9    1 2 3
		 * 4 5 6 -> 4 5 6 -> 2 5 8 -> 8 5 2 -> 6 5 4 -> 6 5 4 -> 8 5 2 -> 2 5 8 -> 4 5 6
		 * 7 8 9    1 2 3    3 6 9    9 6 3    3 2 1    9 8 7    7 4 1    1 4 7    7 8 9
		 */
		private static List<Transformation> ORIENTATIONS = List.of(Transformation.FLIP_H, Transformation.R_90,
				Transformation.FLIP_V, Transformation.R_90, Transformation.FLIP_H, Transformation.R_90,
				Transformation.FLIP_V, Transformation.R_90);

		private final int id;
		private final Map<Edge, Tile> neighbours;
		private int[][] data;

		public Tile(final int id, final int[][] data) {
			this.id = id;
			neighbours = new HashMap<>();
			this.data = data;
		}

		public int id() {
			return id;
		}

		public Map<Edge, Tile> neighbours() {
			return neighbours;
		}

		public void setNeighbour(final Edge edge, final Tile tile) {
			neighbours.put(edge, tile);
		}

		public int[] getEdge(final Edge edge) {
			return switch (edge) {
			case TOP -> data[0];
			case RIGHT -> getLeftOrRightEdge(false);
			case BOTTOM -> data[data.length - 1];
			case LEFT -> getLeftOrRightEdge(true);
			default -> throw new IllegalArgumentException();
			};
		}

		private int[] getLeftOrRightEdge(final boolean left) {
			int[] b = new int[data.length];
			int x = left ? 0 : data[0].length - 1;
			for (int y = 0; y < b.length; y++) {
				b[y] = data[y][x];
			}

			return b;
		}

		public Tile alignTo(final int[] targetEdge, final Edge edge) {
			for (Transformation transformation : ORIENTATIONS) {
				if (Arrays.equals(targetEdge, getEdge(edge))) {
					return this;
				}
				transformation.transform(this);
			}

			Logger.warn("Unable to align {} edge {} to {}", this, edge, toString(targetEdge));

			return null;
		}

		/*-
		 * 1 2 3    7 8 9
		 * 4 5 6 -> 4 5 6
		 * 7 8 9    1 2 3
		 */
		public void flipHorizontalAxis() {
			final int[][] new_data = new int[data.length][data[0].length];
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[y].length; x++) {
					new_data[data.length - 1 - y][x] = data[y][x];
				}
			}
			data = new_data;

			// Swap top and bottom neighbours
			Tile top = neighbours.remove(Edge.TOP);
			Tile bottom = neighbours.remove(Edge.BOTTOM);
			if (bottom != null) {
				neighbours.put(Edge.TOP, bottom);
			}
			if (top != null) {
				neighbours.put(Edge.BOTTOM, top);
			}
		}

		/*-
		 * 1 2 3    3 2 1
		 * 4 5 6 -> 6 5 4
		 * 7 8 9    9 8 7
		 */
		public void flipVerticalAxis() {
			final int[][] new_data = new int[data.length][data[0].length];
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[y].length; x++) {
					new_data[y][data[0].length - 1 - x] = data[y][x];
				}
			}
			data = new_data;

			// Swap left and right neighbours
			final Tile left = neighbours.remove(Edge.LEFT);
			final Tile right = neighbours.remove(Edge.RIGHT);
			if (left != null) {
				neighbours.put(Edge.RIGHT, left);
			}
			if (right != null) {
				neighbours.put(Edge.LEFT, right);
			}
		}

		public void rotateClockwise90(final int count) {
			for (int i = 0; i < count; i++) {
				rotateClockwise90();
			}
		}

		/*-
		 * 1 2 3    7 4 1 (0,0) -> (2,0); (1,0) -> (2,1); (2,0) -> (2,2)
		 * 4 5 6 -> 8 5 2 (0,1) -> (1,0); (1,1) -> (1,1); (2,1) -> (1,2)
		 * 7 8 9    9 6 3 (0,2) -> (0,0); (1,2) -> (0,1); (2,2) -> (0,2)
		 */
		public void rotateClockwise90() {
			final int[][] new_data = new int[data[0].length][data.length];
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[y].length; x++) {
					new_data[x][data.length - 1 - y] = data[y][x];
				}
			}
			data = new_data;

			// Update all neighbours
			final Tile top = neighbours.remove(Edge.TOP);
			final Tile right = neighbours.remove(Edge.RIGHT);
			final Tile left = neighbours.remove(Edge.LEFT);
			final Tile bottom = neighbours.remove(Edge.BOTTOM);

			if (top != null) {
				neighbours.put(Edge.RIGHT, top);
			}
			if (right != null) {
				neighbours.put(Edge.BOTTOM, right);
			}
			if (bottom != null) {
				neighbours.put(Edge.LEFT, bottom);
			}
			if (left != null) {
				neighbours.put(Edge.TOP, left);
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tile other = (Tile) obj;
			if (id != other.id)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "[Tile " + id + ", neighbours: "
					+ neighbours.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue().id()).toList() + "]";
		}

		public static String toString(int[] data) {
			StringBuffer buffer = new StringBuffer();
			for (int i : data) {
				buffer.append((char) i);
			}
			return buffer.toString();
		}
	}
}
