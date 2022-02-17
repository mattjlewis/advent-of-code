package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.util.ArrayUtil;
import com.diozero.aoc.util.FunctionUtil;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

/*-
 * Possible instructions are:
 * - Movement via north, south, east, or west.
 * - To take an item the droid sees in the environment, use the command take <name of item>. For example, if the
 *   droid reports seeing a red ball, you can pick it up with take red ball.
 * - To drop an item the droid is carrying, use the command drop <name of item>. For example, if the droid is
 *   carrying a green ball, you can drop it with drop green ball.
 * - To get a list of all of the items the droid is currently carrying, use the command inv (for "inventory").
 *
 *              Science Lab
 *                  I
 * Holodeck <-> Observatory   Hot Chocolate Fountain <-> Hallway <-> Kitchen
 *                  I                                       I
 *               Corridor <---------------------> Gift Wrapping Center <-> Arcade <-> Passages <-> Navigation
 *                  I                                                                    I
 *             Hull Breach* <-> Storage <-> Crew Quarters              Sick Bay <-> Engineering <-> Warp Drive Maintenance
 *                  I                                                     I
 *               Stables             Pressure-Sensitive Floor <-> Security Checkpoint
 *
 * Item                | Room
 * --------------------+-----------------------
 * fixed point         | Stables
 * spool of cat6       | Corridor
 * monolith*           | Observatory
 * planetoid           | Holodeck
 * hypercube*          | Science Lab
 * candy cane          | Hallway
 * easter egg*         | Arcade
 * ornament*           | Engineering
 * photons             | Hot Chocolate Fountain (don't pickup - "It is suddenly completely dark! You are eaten by a Grue!")
 * giant electromagnet | Kitchen                (don't pickup - "The giant electromagnet is stuck to you.  You can't move!!")
 * escape pod          | Passages               (don't pickup - "You're launched into space! Bye!")
 * molten lava         | Navigation             (don't pickup - "The molten lava is way too hot! You melt!")
 * infinite loop       | Warp Drive Maintenance (don't pickup! - "Infinite loop!")
 */
public class Day25 extends Day {
	private static final String DROP_COMMAND = "drop ";
	private static final String TAKE_COMMAND = "take ";
	private static final String INV_COMMAND = "inv";
	private static final Set<String> RESTRICTED_ITEMS = Set.of("photons", "giant electromagnet", "escape pod",
			"molten lava", "infinite loop");
	private static final String SECURITY_CHECKPOINT = "Security Checkpoint";

	public static void main(String[] args) {
		new Day25().run();
	}

	@Override
	public String name() {
		return "Cryostasis";
	}

	@Override
	public String part1(Path input) throws IOException {
		final BlockingQueue<Long> input_queue = new LinkedBlockingQueue<>();
		final OutputBuffer output = new OutputBuffer();
		final IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input,
				FunctionUtil.blockingLongSupplier(input_queue), output);
		final ExecutorService es = Executors.newSingleThreadExecutor();
		final Future<?> future = es.submit(vm);

		// Read the starting position
		Room room;
		try {
			room = output.readRoom();
		} catch (InterruptedException e) {
			Logger.error(e, "Error: {}", e);
			throw new RuntimeException(e);
		}
		if (Logger.isDebugEnabled()) {
			print(room);
		}

		// Search the ship and pick up all unrestricted items
		final Graph<String, Room> graph = exploreShip(room, input_queue, output);

		// Navigate from the current room to the "Security Checkpoint" room
		final GraphNode<String, Room> start = graph.get(output.lastRoom.name);
		final GraphNode<String, Room> dest = graph.get(SECURITY_CHECKPOINT);
		if (!Dijkstra.findPath(start, dest)) {
			throw new IllegalStateException("Cannot get from " + start.id() + " to " + dest.id());
		}

		final Deque<Room> path = new ArrayDeque<>();
		GraphNode<String, Room> current = dest;
		do {
			path.offerFirst(current.value());
			current = current.getParent();
		} while (current != null);
		Logger.debug("Path from {} to {}: {}", start.id(), dest.id(), path);
		path.stream().skip(1).forEach(r -> moveTo(r, input_queue, output));

		// Get the inventory and drop everything
		final Set<String> inventory = getInventory(input_queue, output);
		inventory.forEach(item -> drop(input_queue, output, item));

		// Get the direction to the pressure sensitive floor
		final CompassDirection escape_dir = dest.value().streamUndiscoveredDirections().findFirst().orElseThrow();

		// Try all combinations
		for (int i = 1; i < inventory.size(); i++) {
			for (List<String> inv : ArrayUtil.permutations(inventory, i).toList()) {
				inv.forEach(item -> take(input_queue, output, item));
				room = move(input_queue, output, escape_dir);

				if (!room.name.equals(SECURITY_CHECKPOINT)) {
					Logger.debug("Found the combo! {}", inv);
					try {
						future.cancel(true);
						vm.halt();

						es.shutdown();
						es.awaitTermination(10, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						Logger.error(e, "Error: {}", e);
					}

					return room.name;
				}

				inv.forEach(item -> drop(input_queue, output, item));
			}
		}

		throw new IllegalStateException("Failed to solve");
	}

	@Override
	public String part2(Path input) throws IOException {
		final BlockingQueue<Long> input_queue = new LinkedBlockingQueue<>();
		final OutputBuffer output = new OutputBuffer();
		final IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input,
				FunctionUtil.blockingLongSupplier(input_queue), output);
		final ExecutorService es = Executors.newSingleThreadExecutor();
		final Future<?> future = es.submit(vm);

		try {
			output.readRoom();
		} catch (InterruptedException e) {
			Logger.error(e, "Error: {}", e);
			throw new RuntimeException(e);
		}

		// The Cheats version...
		List<String> commands = List.of("north", "north", "take monolith", "north", "take hypercube", "south", "south",
				"east", "east", "take easter egg", "east", "south", "take ornament", "west", "south", "west");
		Room room = null;
		for (String command : commands) {
			room = sendAndReadRoom(input_queue, output, command);
		}

		try {
			future.cancel(true);
			vm.halt();

			es.shutdown();
			es.awaitTermination(10, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Logger.error(e, "Error: {}", e);
		}

		if (room == null) {
			throw new IllegalStateException();
		}

		return room.name;
	}

	private static Graph<String, Room> exploreShip(Room startRoom, BlockingQueue<Long> inputQueue,
			OutputBuffer output) {
		// Keep track of where we have already visited
		final Set<String> visited = new HashSet<>();
		visited.add(startRoom.name);

		// Record the moves so we can backtrack when we reach a dead-end
		// A stack so they can be accessed in LIFO order
		final Deque<CompassDirection> movement_history = new ArrayDeque<>();
		// A stack of unexplored paths that we will backtrack to on reaching a dead-end
		final Deque<Room> branches = new ArrayDeque<>();

		Room current_room = startRoom;
		final Graph<String, Room> graph = new Graph<>();
		GraphNode<String, Room> current_node = graph.getOrPut(current_room.name, current_room);
		CompassDirection direction_to_escape_room = null;
		while (true) {
			// Must do this separately to avoid ConcurrentModificationException - picking up
			// an item updates the list of items that can be picked up in this room
			final List<String> items_to_pickup = current_room.getUnrestrictedItems();
			items_to_pickup.forEach(i -> take(inputQueue, output, i));

			/*
			 * Continue on a single path until we hit a dead-end, then traverse back to the
			 * last branch point until there are no more branches to explore.
			 */

			// Explore the immediate area and get a list of unexplored directions
			final List<CompassDirection> unexplored_directions = current_room.getUndiscoveredDirections();

			// A dead-end?
			if (unexplored_directions.size() == 0) {
				// Exit the loop if there are no more branches to explore
				if (branches.isEmpty()) {
					Logger.debug("No more branches, exiting exploration loop");

					// Restore the path to the escape room
					if (direction_to_escape_room != null) {
						graph.get(SECURITY_CHECKPOINT).value().addUndiscoveredDirection(direction_to_escape_room);
					}

					break;
				}

				// Get the position of the last branch and backtrack to it
				final Room branch_position = branches.removeLast();
				Logger.debug("Traversing back to branch @ {} from current room {}", branch_position, current_room);
				do {
					// Move the droid in the opposite direction
					current_room = move(inputQueue, output, movement_history.pollLast().opposite());
					current_node = graph.getOrPut(current_room.name, current_room);
				} while (!current_room.equals(branch_position));
			} else {
				CompassDirection dir;
				if (unexplored_directions.size() == 1) {
					// Just continue if there is only one choice
					dir = unexplored_directions.get(0);
				} else {
					Logger.debug("Multiple directions to explore: {}...", unexplored_directions);
					// Multiple paths to explore, continue on the current one if possible
					// Otherwise just pick any unexplored direction
					dir = unexplored_directions.get(0);

					// Add the location of this branch position the tail of the queue
					Logger.debug("Adding a branch @ {}...", current_room);
					branches.offerLast(current_room);
				}

				// Move the droid and keep track of this movement direction
				final Room next_room = move(inputQueue, output, dir);
				if (next_room.name.equals(current_room.name)) {
					if (!current_room.name.equals(SECURITY_CHECKPOINT)) {
						Logger.warn("Failed to move from {} in direction {}", current_room.name, dir);
					}
					// Temporarily remove the path to the escape room
					direction_to_escape_room = dir;
					current_room.removeDirection(dir);
				} else {
					visited.add(next_room.name);
					movement_history.add(dir);

					current_room.addNeighbour(dir, next_room);

					final GraphNode<String, Room> next_node = graph.getOrPut(next_room.name, next_room);
					next_node.addNeighbour(current_node, 1);
					current_node.addNeighbour(next_node, 1);

					current_room = next_room;
					current_node = graph.getOrPut(current_room.name, current_room);
				}
			}
		}

		return graph;
	}

	private static void send(BlockingQueue<Long> inputQueue, OutputBuffer output, String command)
			throws InterruptedException {
		Logger.debug("Sending '{}'", command);
		command.chars().mapToObj(Long::valueOf).forEach(inputQueue::offer);
		inputQueue.put(Long.valueOf('\n'));
	}

	private static Room move(BlockingQueue<Long> inputQueue, OutputBuffer output, CompassDirection direction) {
		return sendAndReadRoom(inputQueue, output, direction.name().toLowerCase());
	}

	private static void moveTo(Room destRoom, BlockingQueue<Long> inputQueue, OutputBuffer output) {
		move(inputQueue, output, output.lastRoom.getDirectionTo(destRoom.name).orElseThrow());
	}

	private static Room take(BlockingQueue<Long> inputQueue, OutputBuffer output, String item) {
		return sendAndReadRoom(inputQueue, output, TAKE_COMMAND + item);
	}

	private static Room drop(BlockingQueue<Long> inputQueue, OutputBuffer output, String item) {
		return sendAndReadRoom(inputQueue, output, DROP_COMMAND + item);
	}

	private static Set<String> getInventory(BlockingQueue<Long> inputQueue, OutputBuffer output) {
		try {
			send(inputQueue, output, INV_COMMAND);

			return output.readInventory();
		} catch (InterruptedException e) {
			Logger.error(e, "Error: {}", e);
			throw new RuntimeException(e);
		}
	}

	private static Room sendAndReadRoom(BlockingQueue<Long> inputQueue, OutputBuffer output, String command) {
		try {
			send(inputQueue, output, command);

			return output.readRoom();
		} catch (InterruptedException e) {
			Logger.error(e, "Error: {}", e);
			throw new RuntimeException(e);
		}
	}

	private static void print(Room room) {
		System.out.println("== " + room.name + " ==");
		System.out.println(room.description);
		System.out.println("Doors here lead:");
		room.directions.keySet().forEach(d -> System.out.println("- " + d));
		System.out.println("Items here:");
		room.items.forEach(i -> System.out.println("- " + i));
		System.out.println();
	}

	private static class OutputBuffer implements LongConsumer {
		private final BlockingQueue<Character> buffer;
		private final Map<String, Room> roomCache;
		private Room lastRoom;

		public OutputBuffer() {
			buffer = new LinkedBlockingQueue<>();
			roomCache = new HashMap<>();
		}

		@Override
		public void accept(long value) {
			buffer.offer(Character.valueOf((char) value));
		}

		public String readLine() throws InterruptedException {
			final StringBuilder line = new StringBuilder();
			while (true) {
				char ch = buffer.take().charValue();
				if (ch == '\n') {
					break;
				}
				line.append(ch);
			}
			return line.toString();
		}

		public Set<String> readInventory() throws InterruptedException {
			final Set<String> inventory = new HashSet<>();

			boolean reading_inventory = false;
			while (true) {
				String line = readLine();
				Logger.debug(line);

				// Responses always end with "Command?"
				if (line.equals("Command?")) {
					break;
				}

				if (line.equals("Items in your inventory:")) {
					reading_inventory = true;
				} else if (line.isBlank()) {
					reading_inventory = false;
				} else if (reading_inventory) {
					inventory.add(line.substring(2));
				} else {
					Logger.warn("Unexpected response '{}'", line);
				}
			}

			return inventory;
		}

		public Room readRoom() throws InterruptedException {
			String room_name = null;
			String room_description = null;
			final List<CompassDirection> directions = new ArrayList<>();
			final Set<String> items_here = new HashSet<>();
			final Set<String> inventory = new HashSet<>();

			boolean reading_description = false;
			boolean reading_directions = false;
			boolean reading_items = false;
			boolean reading_inventory = false;
			Room room = null;
			while (true) {
				final String line = readLine();
				Logger.debug(line);

				// Responses always end with "Command?"
				if (line.equals("Command?")) {
					break;
				}

				if (line.equals(
						"A loud, robotic voice says \"Analysis complete! You may proceed.\" and you enter the cockpit.")) {
					// Santa notices your small droid, looks puzzled for a moment, realizes what has
					// happened, and radios your ship directly.
					readLine();
					final String code = readLine().replaceAll(
							"^\"Oh, hello! You should be able to get in by typing (\\d+) on the keypad at the main airlock.\"$",
							"$1");

					return new Room(code);
				}

				if (line.equals("Unrecognized command.")) {
					Logger.warn("Unrecognised command...");
					room = lastRoom;
				} else if (line.contains("Alert! Droids on this ship are lighter than the detected value!")) {
					Logger.debug("Too heavy");
					room = lastRoom;
				} else if (line.contains("Alert! Droids on this ship are heavier than the detected value!")) {
					Logger.debug("Too light");
					room = lastRoom;
				} else if (line.equals("You can't go that way.")) {
					Logger.warn("Attempt to go in an invalid direction");
					room = lastRoom;
				} else if (line.equals("You don't see that item here.")) {
					Logger.warn("Attempted to pick-up an item that isn't present");
					room = lastRoom;
				} else if (line.equals("You don't have that item.")) {
					Logger.warn("Attempted to drop an item not in inventory");
					room = lastRoom;
				} else if (line.startsWith("You take the ")) {
					room = lastRoom;
					if (!room.removeItem(line.substring("You take the ".length(), line.length() - 1))) {
						Logger.warn("Failed to process take response '{}'", line);
					}
				} else if (line.startsWith("You drop the ")) {
					room = lastRoom;
					if (!room.addItem(line.substring("You drop the ".length(), line.length() - 1))) {
						Logger.warn("Failed to process drop response '{}'", line);
					}
				} else if (line.equals("Items in your inventory:")) {
					reading_inventory = true;
					room = lastRoom;
				} else if (line.startsWith("== ") && line.endsWith(" ==")) {
					room_name = line.replace("==", "").trim();
					reading_description = true;
				} else if (line.equals("Doors here lead:")) {
					reading_directions = true;
				} else if (line.equals("Items here:")) {
					reading_items = true;
				} else {
					if (reading_description) {
						if (line.isEmpty()) {
							reading_description = false;
						} else {
							room_description = line;
						}
					} else if (reading_directions) {
						if (line.isEmpty()) {
							reading_directions = false;
						} else {
							directions.add(CompassDirection.valueOf(line.substring(2).toUpperCase()));
						}
					} else if (reading_inventory) {
						if (line.isEmpty()) {
							reading_inventory = false;
						} else {
							inventory.add(line.substring(2));
						}
					} else if (reading_items) {
						if (line.isEmpty()) {
							reading_items = false;
						} else {
							items_here.add(line.substring(2));
						}
					} else if (!line.isEmpty()) {
						Logger.warn("Unhandled line '{}'", line);
					}
				}
			}

			if (room == null) {
				if (room_name == null) {
					room_name = lastRoom.name;
					room_description = lastRoom.description;
				}

				room = roomCache.get(room_name);
				if (room == null) {
					room = new Room(room_name, room_description, items_here, directions);
					roomCache.put(room_name, room);
				} else {
					// Update the items in the room
					room.updateItems(items_here);
				}
			}

			lastRoom = room;

			return room;
		}
	}

	private static class Room {
		private final String name;
		private final String description;
		private final Set<String> items;
		private final Map<CompassDirection, Optional<Room>> directions;

		public Room(String code) {
			this.name = code;
			description = "";
			items = Collections.emptySet();
			directions = Collections.emptyMap();
		}

		public Room(String name, String description, Set<String> items, List<CompassDirection> directions) {
			this.name = name;
			this.description = description;
			this.items = items;
			this.directions = directions.stream().collect(Collectors.toMap(Function.identity(), d -> Optional.empty()));
		}

		public List<String> getUnrestrictedItems() {
			return items.stream().filter(i -> !RESTRICTED_ITEMS.contains(i)).toList();
		}

		public boolean addItem(String item) {
			return items.add(item);
		}

		public boolean removeItem(String item) {
			return items.remove(item);
		}

		public void updateItems(Set<String> items_here) {
			items.clear();
			items.addAll(items_here);
		}

		public void addUndiscoveredDirection(CompassDirection direction) {
			directions.put(direction, Optional.empty());
		}

		public void removeDirection(CompassDirection dir) {
			directions.remove(dir);
		}

		public List<CompassDirection> getUndiscoveredDirections() {
			return streamUndiscoveredDirections().toList();
		}

		public Stream<CompassDirection> streamUndiscoveredDirections() {
			return directions.entrySet().stream().filter(e -> e.getValue().isEmpty()).map(Map.Entry::getKey);
		}

		public void addNeighbour(CompassDirection dir, Room nextRoom) {
			directions.put(dir, Optional.of(nextRoom));
			nextRoom.directions.put(dir.opposite(), Optional.of(this));
		}

		public Optional<CompassDirection> getDirectionTo(String destination) {
			return directions.entrySet().stream().filter(e -> !e.getValue().isEmpty())
					.filter(e -> e.getValue().get().name.equals(destination)).findFirst().map(Map.Entry::getKey);
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			Room other = (Room) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
}
