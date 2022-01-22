package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
	private static final int NUM_ROOMS = 4;
	private static final int HALLWAY_LENGTH = 11;
	private static final int HALLWAY_POSITIONS = HALLWAY_LENGTH - NUM_ROOMS;
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
	private static final int UNOCCUPIED = -1;

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

	private static byte[] loadData(Path inputPath) throws IOException {
		final List<String> input = Files.readAllLines(inputPath);
		final int num_each_type = input.size() - 3;

		/*-
		 * Conceptually a 2D array [amphipod_type][amphipod_type_id], however, is held as a 1D array so that it can be
		 * cloned with a single call to System.arraycopy().
		 *
		 * Stores the position of each amphipod.
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
		final byte[] starting_positions = new byte[NUM_AMPHIPOD_TYPES * num_each_type];
		for (int i = 0; i < num_each_type; i++) {
			String line = input.get(i + 2);
			for (int j = 0; j < NUM_ROOMS; j++) {
				int pos_index = (line.charAt(2 * j + 3) - 'A') * num_each_type;
				while (starting_positions[pos_index] != 0) {
					pos_index++;
				}
				starting_positions[pos_index] = (byte) (NUM_AMPHIPOD_TYPES * i + j + HALLWAY_POSITIONS);
			}
		}

		return starting_positions;
	}

	private static long solvePosition(GameState startState) {
		// A priority queue so that game states can be processed in order of cost
		final Queue<GameState> open_set = new PriorityQueue<>();
		// Map from GameState representation to cost
		final Map<String, Long> state_cache = new HashMap<>();

		final int num_amphipods = startState.amphipodPositions().length;

		open_set.offer(startState);

		long best_cost = Long.MAX_VALUE;
		while (!open_set.isEmpty()) {
			final GameState state = open_set.poll();
			/*-
			 * FIXME Use the generic A* implementation
			 */

			/*
			 * FIXME Probably more efficient to simply store the occupancy for each position
			 * rather than calculate it at least twice for each iteration (find valid
			 * hallway / room position as well as GameState.toString())
			 */

			if (state.cost() >= best_cost) {
				break;
			}

			// For each amphipod
			for (int amphipod = 0; amphipod < num_amphipods; amphipod++) {
				final int type_index = getAmphipodTypeIndex(amphipod, num_amphipods);
				// Get all valid moves for this amphipod
				final boolean[] valid_positions = findValidPositions(state.amphipodPositions(), amphipod, type_index,
						num_amphipods);
				// For each valid move
				for (byte i = 0; i < valid_positions.length; i++) {
					if (!valid_positions[i]) {
						continue;
					}

					// Calculate the move cost
					final int move_cost = calcMoveCost(state.amphipodPositions()[amphipod], i,
							MOVE_ENERGIES[type_index]);
					// Generate a new game state instance
					final GameState next_state = state.moveAmphipod(amphipod, i, move_cost);
					// Are all the amphipods in their correct room?
					if (next_state.isComplete()) {
						best_cost = Math.min(best_cost, next_state.cost());
					} else {
						// Has the next state already been calculated?
						final String state_repr = next_state.toString();
						Long cached_cost = state_cache.get(state_repr);
						if (cached_cost == null || next_state.cost() < cached_cost.longValue()) {
							// No, add to the queue to continue processing
							state_cache.put(state_repr, Long.valueOf(next_state.cost()));
							open_set.offer(next_state);
						}
					}
				}
			}
		}

		return best_cost;
	}

	private static int getAmphipodTypeIndex(int amphipod, int numAmphipods) {
		return amphipod / (numAmphipods / 4);
	}

	private static int[] getOccupancy(byte[] amphipodPositions) {
		// Populate position occupancy, use -1 for unoccupied
		final int[] occupancy = new int[amphipodPositions.length + HALLWAY_POSITIONS];
		Arrays.fill(occupancy, UNOCCUPIED);

		for (int i = 0; i < amphipodPositions.length; i++) {
			occupancy[amphipodPositions[i]] = i;
		}

		return occupancy;
	}

	private static boolean[] findValidPositions(byte[] amphipodPositions, int amphipod, int amphipodTypeIndex,
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
		if (amphipodPositions[amphipod] < HALLWAY_POSITIONS) {
			// The amphipod is in the hallway therefore must move into a room
			return findValidRoomPositions(amphipodPositions, amphipod, amphipodTypeIndex, numAmphipods);
		}

		// The amphipod is in a room so must move into the hallway
		return findValidHallwayPositions(amphipodPositions, amphipod, amphipodTypeIndex, numAmphipods);
	}

	private static boolean[] findValidHallwayPositions(final byte[] amphipodPositions, final int amphipod,
			final int amphipodTypeIndex, int numAmphipods) {
		// The amphipod is in a room so must move into the hallway

		final int amphipod_pos = amphipodPositions[amphipod];
		final boolean[] valid_hallway_positions = new boolean[HALLWAY_POSITIONS];
		final int[] occupancy = getOccupancy(amphipodPositions);

		// Is there another amphipod blocking this amphipod from leaving the room?
		if (amphipod_pos >= HALLWAY_POSITIONS + NUM_ROOMS) {
			for (int i = amphipod_pos - NUM_ROOMS; i >= HALLWAY_POSITIONS; i -= NUM_ROOMS) {
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
		final int room_type_index = (amphipod_pos + 1) % NUM_ROOMS;
		if (room_type_index == amphipodTypeIndex) {
			// The amphipod is in the correct room
			boolean blocking_another = false;
			// Is there an amphipod below this amphipod that is not in the correct room?
			for (int i = amphipod_pos + NUM_ROOMS; i < numAmphipods + HALLWAY_POSITIONS; i += NUM_ROOMS) {
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

	private static boolean[] findValidRoomPositions(final byte[] amphipodPositions, final int amphipod,
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
		final boolean[] valid_room_positions = new boolean[numAmphipods + HALLWAY_POSITIONS];
		final int[] occupancy = getOccupancy(amphipodPositions);

		// Check if there is a clear path from the current hallway position to the
		// desired target room
		if (!checkHallwayClear(amphipodPositions[amphipod], target_room_entrance_pos, occupancy)) {
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

		int actual_hallway_pos_above_room = ((room_pos + 1) % NUM_ROOMS) * 2 + 2;
		int dist = Math.abs(ACTUAL_HALLWAY_POSITIONS[hallway_pos] - actual_hallway_pos_above_room) + room_depth;

		return dist * energyPerStep;
	}

	private static boolean checkHallwayClear(int hallwayPos, int targetRoomPos, int[] occupancy) {
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

	private static record GameState(byte[] amphipodPositions, int cost) implements Comparable<GameState> {
		public GameState moveAmphipod(int amphipod, byte position, int moveCost) {
			byte[] new_positions = Arrays.copyOf(amphipodPositions, amphipodPositions.length);
			new_positions[amphipod] = position;
			GameState rv = new GameState(new_positions, cost + moveCost);

			return rv;
		}

		public boolean isComplete() {
			for (int i = 0; i < amphipodPositions.length; i++) {
				int type_index = getAmphipodTypeIndex(i, amphipodPositions.length);
				if (amphipodPositions[i] < HALLWAY_POSITIONS || (amphipodPositions[i] + 1) % NUM_ROOMS != type_index) {
					return false;
				}
			}

			return true;
		}

		@Override
		public int compareTo(GameState other) {
			return Integer.compare(cost, other.cost);
		}

		@Override
		public String toString() {
			final int[] occupancy = getOccupancy(amphipodPositions);

			// FIXME Convert to a long?
			// Values -1..4 (len 5 therefore 3 bits required to store uniquely?)
			// 7 + 2*4 = 15 positions (part 1); 15 * 3 = 45
			// 7 + 4*4 = 23 positions (part 2); 23 * 3 = 69 -> short by 5 bits!
			StringBuffer representation = new StringBuffer();
			for (int i = 0; i < occupancy.length; i++) {
				if (occupancy[i] == UNOCCUPIED) {
					representation.append("x");
				} else {
					representation.append(getAmphipodTypeIndex(occupancy[i], amphipodPositions.length));
				}
			}

			return representation.toString();

			/*-
			long hash = 0;
			for (int i = 0; i < Math.min(occupancy.length, 21); i++) {
				hash <<= 3;
				hash += occupancy[i] == UNOCCUPIED ? 0
						: getAmphipodTypeIndex(occupancy[i], amphipodPositions.length) + 1;
			}
			byte hash2 = 0;
			for (int i = 22; i < occupancy.length; i++) {
				hash2 <<= 3;
				hash2 += occupancy[i] == UNOCCUPIED ? 0
						: getAmphipodTypeIndex(occupancy[i], amphipodPositions.length) + 1;
			}
			
			return Long.toString(hash) + Byte.toString(hash2);
			*/
		}
	}
}
