package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.diozero.aoc.Day;

public class Day23 extends Day {
	// Amber, Bronze, Copper, Desert
	private static final int[] MOVE_ENERGIES = { 1, 10, 100, 1000 };

	private static final int NUM_AMPHIPOD_TYPES = MOVE_ENERGIES.length;
	private static final int NUM_ROOMS = NUM_AMPHIPOD_TYPES;
	private static final int ACTUAL_HALLWAY_LENGTH = 11;
	private static final int HALLWAY_POSITIONS = ACTUAL_HALLWAY_LENGTH - NUM_ROOMS;
	private static final int[] ACTUAL_HALLWAY_POSITIONS;
	static {
		ACTUAL_HALLWAY_POSITIONS = new int[HALLWAY_POSITIONS];
		/*-
		 *  0123456789a
		 * #############
		 * #01x2x3x4x56#
		 * ###7#8#9#a###
		 *   #b#c#d#e#
		 *   #########
		 */
		ACTUAL_HALLWAY_POSITIONS[0] = 0;
		ACTUAL_HALLWAY_POSITIONS[1] = 1;
		ACTUAL_HALLWAY_POSITIONS[2] = 3;
		ACTUAL_HALLWAY_POSITIONS[3] = 5;
		ACTUAL_HALLWAY_POSITIONS[4] = 7;
		ACTUAL_HALLWAY_POSITIONS[5] = 9;
		ACTUAL_HALLWAY_POSITIONS[6] = 10;
	}
	private static final byte UNOCCUPIED = -1;

	public static void main(String[] args) {
		new Day23().run();
	}

	@Override
	public String name() {
		return "Amphipod";
	}

	@Override
	public String part1(Path input) throws IOException {
		return Long.toString(solvePosition(new GameState(loadData(input), 0)));
	}

	@Override
	public String part2(Path input) throws IOException {
		return Long.toString(solvePosition(new GameState(loadData(input.getParent().resolve("day23b.txt")), 0)));
	}

	private static byte[] loadData(Path input) throws IOException {
		final List<String> lines = Files.readAllLines(input);
		final int num_each_type = lines.size() - 3;

		/*-
		 * Store the state of each possible position. -1 is empty, otherwise amphipod index, where:
		 *
		 * For part 1 the index for A is 0..1, B 2..3, C 4..5, D 6..7.
		 * For part 2 the index for A is 0..3, B 4..7, C 8..11, D 12..15
		 *
		 * Amphipods will never stop on the space immediately outside of a room.
		 *
		 * For part 1 there are 15 valid positions, and for part 2 19. I.e., for part 1:
		 * #############
		 * #01x2x3x4x56#
		 * ###7#8#9#a###
		 *   #b#c#d#e#
		 *   #########
		 */

		final byte[] amphipod_positions = new byte[NUM_AMPHIPOD_TYPES * num_each_type];
		for (int i = 0; i < num_each_type; i++) {
			String line = lines.get(i + 2);
			for (int j = 0; j < NUM_ROOMS; j++) {
				int pos_index = (line.charAt(2 * j + 3) - 'A') * num_each_type;
				while (amphipod_positions[pos_index] != 0) {
					pos_index++;
				}
				amphipod_positions[pos_index] = (byte) (NUM_AMPHIPOD_TYPES * i + j + HALLWAY_POSITIONS);
			}
		}

		final byte[] occupancy = new byte[HALLWAY_POSITIONS + amphipod_positions.length];
		Arrays.fill(occupancy, UNOCCUPIED);
		for (byte i = 0; i < amphipod_positions.length; i++) {
			occupancy[amphipod_positions[i]] = i;
		}

		return occupancy;
	}

	private static long solvePosition(GameState start) {
		final int num_amphipods = getNumAmphipods(start.occupancy().length);

		// A priority queue so that game states can be processed in order of cost
		final Queue<GameState> open_nodes = new PriorityQueue<>(Comparator.comparingInt(GameState::cost));
		// Map from GameState representation to cost
		final Map<String, Long> closed_nodes = new HashMap<>();

		open_nodes.offer(start);

		while (!open_nodes.isEmpty()) {
			final GameState current = open_nodes.poll();
			final byte[] occupancy = current.occupancy();

			/*
			 * FIXME Use the generic A* implementation
			 *
			 * The GraphNode neighbours for each game state would need to be calculated by
			 * taking every possible move for every amphipod.
			 */

			// For each position
			for (byte pos = 0; pos < occupancy.length; pos++) {
				// Ignore unoccupied positions
				if (occupancy[pos] == UNOCCUPIED) {
					continue;
				}

				final int type_index = getAmphipodTypeIndex(occupancy[pos], num_amphipods);
				// Get all valid moves for this amphipod
				final boolean[] valid_positions = findValidPositions(occupancy, pos, type_index, num_amphipods);
				// For each valid move
				for (byte to_pos = 0; to_pos < valid_positions.length; to_pos++) {
					if (!valid_positions[to_pos]) {
						continue;
					}

					// Calculate the move cost (neighbour cost in generic A* terms)
					final int neighbour_cost = calcMoveCost(pos, to_pos, MOVE_ENERGIES[type_index]);
					// int new_cost = current.cost() + neighbour_cost;

					// Generate a new game state instance
					final GameState next_state = current.moveAmphipod(pos, to_pos, neighbour_cost);

					// Are all the amphipods in their correct room?
					if (next_state.isComplete()) {
						// The first one to complete will be the best score
						return next_state.cost();
					}

					// Has the next state already been calculated?
					final String state_repr = next_state.toString();
					Long cached_cost = closed_nodes.get(state_repr);
					if (cached_cost == null || next_state.cost() < cached_cost.longValue()) {
						// No, add to the queue to continue processing
						closed_nodes.put(state_repr, Long.valueOf(next_state.cost()));
						open_nodes.offer(next_state);
					}
				}
			}
		}

		throw new IllegalStateException("No solution found");
	}

	private static int getNumAmphipods(int numPositions) {
		return numPositions - HALLWAY_POSITIONS;
	}

	private static int getAmphipodTypeIndex(int amphipod, int numAmphipods) {
		return amphipod / (numAmphipods / 4);
	}

	private static int getRoomAmphipodTypeIndex(int position) {
		return (position + 1) % NUM_ROOMS;
	}

	private static boolean[] findValidPositions(byte[] occupancy, int amphipodPos, int amphipodTypeIndex,
			int numAmphipods) {
		/*
		 * Amphipods will never stop on the space immediately outside any room. They can
		 * move into that space so long as they immediately continue moving.
		 * (Specifically, this refers to the four open spaces in the hallway that are
		 * directly above an amphipod starting position.)
		 *
		 * Amphipods will never move from the hallway into a room unless that room is
		 * their destination room and that room contains no amphipods which do not also
		 * have that room as their own destination. If an amphipod's starting room is
		 * not its destination room, it can stay in that room until it leaves the room.
		 * (For example, an Amber amphipod will not move from the hallway into the right
		 * three rooms, and will only move into the leftmost room if that room is empty
		 * or if it only contains other Amber amphipods.)
		 *
		 * Once an amphipod stops moving in the hallway, it will stay in that spot until
		 * it can move into a room.
		 */
		if (amphipodPos < HALLWAY_POSITIONS) {
			// The amphipod is in the hallway therefore must move into a room
			return findValidRoomPositions(occupancy, amphipodPos, amphipodTypeIndex, numAmphipods);
		}

		// The amphipod is in a room so must move into the hallway
		return findValidHallwayPositions(occupancy, amphipodPos, amphipodTypeIndex, numAmphipods);
	}

	private static boolean[] findValidHallwayPositions(final byte[] occupancy, final int amphipodPos,
			final int amphipodTypeIndex, int numAmphipods) {
		// The amphipod is in a room so must move into the hallway

		final boolean[] valid_hallway_positions = new boolean[HALLWAY_POSITIONS];

		// Is there another amphipod blocking this amphipod from leaving the room?
		if (amphipodPos >= HALLWAY_POSITIONS + NUM_ROOMS) {
			for (int i = amphipodPos - NUM_ROOMS; i >= HALLWAY_POSITIONS; i -= NUM_ROOMS) {
				if (occupancy[i] != UNOCCUPIED) {
					return valid_hallway_positions;
				}
			}
		}

		/*-
		 * #############
		 * #01x2x3x4x56#
		 * ###7#8#9#a###
		 *   #b#c#d#e#
		 *   #########
		 */
		final int room_type_index = getRoomAmphipodTypeIndex(amphipodPos);
		if (room_type_index == amphipodTypeIndex) {
			// The amphipod is in the correct room
			boolean blocking_another = false;
			// Is there an amphipod below this amphipod that is not in the correct room?
			for (int i = amphipodPos + NUM_ROOMS; i < occupancy.length; i += NUM_ROOMS) {
				if (getAmphipodTypeIndex(occupancy[i], numAmphipods) != amphipodTypeIndex) {
					blocking_another = true;
					break;
				}
			}
			// If this amphipod is in the correct room and it isn't blocking any other
			// amphipods that need to move out of this room then no need for it to move
			if (!blocking_another) {
				return valid_hallway_positions;
			}
		}

		int room_entrance_pos = room_type_index + HALLWAY_POSITIONS;

		// For every hallway position
		for (int hallway_pos = 0; hallway_pos < HALLWAY_POSITIONS; hallway_pos++) {
			// Is it unoccupied and is there a clear path to it?
			if (occupancy[hallway_pos] == UNOCCUPIED && checkHallwayClear(hallway_pos, room_entrance_pos, occupancy)) {
				valid_hallway_positions[hallway_pos] = true;
			}
		}

		return valid_hallway_positions;
	}

	private static boolean[] findValidRoomPositions(final byte[] occupancy, final int amphipodPos,
			final int amphipodTypeIndex, final int numAmphipods) {
		/*
		 * The amphipod is in the hallway so must move into a room.
		 *
		 * Amphipods will never move from the hallway into a room unless that room is
		 * their destination room and that room contains no amphipods which do not also
		 * have that room as their own destination. If an amphipod's starting room is
		 * not its destination room, it can stay in that room until it leaves the room.
		 * (For example, an Amber amphipod will not move from the hallway into the right
		 * three rooms, and will only move into the leftmost room if that room is empty
		 * or if it only contains other Amber amphipods.)
		 */

		final int target_room_entrance_pos = amphipodTypeIndex + HALLWAY_POSITIONS;
		// Note this area is bigger than it needs to be but is simpler
		final boolean[] valid_room_positions = new boolean[numAmphipods + HALLWAY_POSITIONS];

		// Check if there is a clear path from the current hallway position to the
		// desired target room
		if (!checkHallwayClear(amphipodPos, target_room_entrance_pos, occupancy)) {
			return valid_room_positions;
		}

		int target_room_position = target_room_entrance_pos;
		/*
		 * Iterate down through the positions in this room to find the last unoccupied
		 * position. There are no valid moves if there is an amphipod of the wrong type
		 * in this room.
		 */
		for (int i = 0; i < numAmphipods / NUM_ROOMS; i++) {
			int pos = target_room_entrance_pos + 4 * i;
			if (occupancy[pos] == UNOCCUPIED) {
				target_room_position = pos;
			} else if (getAmphipodTypeIndex(occupancy[pos], numAmphipods) != amphipodTypeIndex) {
				return valid_room_positions;
			}
		}

		valid_room_positions[target_room_position] = true;

		return valid_room_positions;
	}

	private static boolean checkHallwayClear(int hallwayPos, int targetRoomPos, byte[] occupancy) {
		/*
		 * Is there a clear path between the hallway position and the target room
		 * entrance position?
		 */
		/*-
		 * #############
		 * #01x2x3x4x56#
		 * ###7#8#9#a###
		 *   #b#c#d#e#
		 *   #########
		 */
		final int min_pos = Math.min(hallwayPos + 1, targetRoomPos - 5);
		final int max_pos = Math.max(hallwayPos - 1, targetRoomPos - 6);
		for (int i = min_pos; i <= max_pos; i++) {
			if (occupancy[i] != UNOCCUPIED) {
				return false;
			}
		}

		return true;
	}

	private static int calcMoveCost(byte from, byte to, int energyPerStep) {
		// Make sure from is less than to so that from is always a hallway position
		int hallway_pos = Math.min(from, to);
		int room_pos = Math.max(from, to);

		/*-
		 *  0123456789a
		 * #############
		 * #01x2x3x4x56#
		 * ###7#8#9#a###
		 *   #b#c#d#e#
		 *   #########
		 */
		int room_depth = (room_pos - 3) / NUM_ROOMS;

		int actual_hallway_pos_above_room = getRoomAmphipodTypeIndex(room_pos) * 2 + 2;
		int dist = Math.abs(ACTUAL_HALLWAY_POSITIONS[hallway_pos] - actual_hallway_pos_above_room) + room_depth;

		return dist * energyPerStep;
	}

	private static record GameState(byte[] occupancy, int cost) {
		public GameState moveAmphipod(byte fromPosition, byte toPosition, int moveCost) {
			byte[] new_occupancy = Arrays.copyOf(occupancy, occupancy.length);
			new_occupancy[fromPosition] = UNOCCUPIED;
			new_occupancy[toPosition] = occupancy[fromPosition];

			return new GameState(new_occupancy, cost + moveCost);
		}

		public boolean isComplete() {
			for (int i = HALLWAY_POSITIONS; i < occupancy.length; i++) {
				if (occupancy[i] == UNOCCUPIED || getRoomAmphipodTypeIndex(i) != getAmphipodTypeIndex(occupancy[i],
						getNumAmphipods(occupancy.length))) {
					return false;
				}
			}

			return true;
		}

		@Override
		public String toString() {
			int num_amphipods = getNumAmphipods(occupancy.length);

			// FIXME Convert to a long instead?
			// Values -1..4 (len 5 therefore 3 bits required to store uniquely)
			// 7 + 2*4 = 15 positions (part 1); 15 positions * 3 bits = 45 bits
			// 7 + 4*4 = 23 positions (part 2); 23 positions * 3 bits = 69 bits, 5 too many

			if (num_amphipods == 8) {
				long hash = 0;
				for (int i = 0; i < occupancy.length; i++) {
					hash <<= 3;
					hash += occupancy[i] == UNOCCUPIED ? 0 : getAmphipodTypeIndex(occupancy[i], num_amphipods) + 1;
				}

				return Long.toString(hash);
			}

			/*-
			long hash2 = 0;
			for (int i = 15; i < occupancy.length; i++) {
				hash2 <<= 3;
				hash2 += occupancy[i] == UNOCCUPIED ? 0
						: getAmphipodTypeIndex(occupancy[i], amphipodPositions.length) + 1;
			}

			return Long.toString(hash) + Long.toString(hash2);
			*/

			StringBuffer representation = new StringBuffer();
			for (int i = 0; i < occupancy.length; i++) {
				if (occupancy[i] == UNOCCUPIED) {
					representation.append("x");
				} else {
					representation.append(getAmphipodTypeIndex(occupancy[i], num_amphipods));
				}
			}

			return representation.toString();
		}
	}
}
