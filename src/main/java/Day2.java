import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day2 {
	private static int horizontal;
	private static int depth;
	private static int aim;

	public static void main(String[] args) {
		String input_file = "day2.txt";
		Path input_path = Path.of(input_file);
		try {
			part1(input_path);
			System.out.println("depth: " + depth + ", horizontal: " + horizontal + ", d*h=" + (depth * horizontal));

			horizontal = 0;
			depth = 0;
			aim = 0;
			part2(input_path);
			System.out.println("depth: " + depth + ", horizontal: " + horizontal + ", aim=" + aim + ", d*h="
					+ (depth * horizontal));
		} catch (IOException e) {
			System.out.println("Error unable to read input file '" + input_file + "'");
		}
	}

	private static void part1(Path input) throws IOException {
		Files.lines(input).map(Day2::parse).forEach(instruction -> {
			switch (instruction.movement) {
			case FORWARD:
				horizontal += instruction.amount;
				break;
			default:
				depth += instruction.amount;
				break;
			}
		});
	}

	private static void part2(Path input) throws IOException {
		Files.lines(input).map(Day2::parse).forEach(instruction -> {
			switch (instruction.movement) {
			case FORWARD:
				horizontal += instruction.amount;
				depth += aim * instruction.amount;
				break;
			default:
				aim += instruction.amount;
				break;
			}
		});
	}

	private static Instruction parse(String line) {
		String[] parts = line.split(" ");
		return new Instruction(Instruction.Movement.valueOf(parts[0].toUpperCase()), Integer.parseInt(parts[1]));
	}

	public static class Instruction {
		public enum Movement {
			FORWARD, UP, DOWN;
		}

		final Movement movement;
		final int amount;

		public Instruction(Instruction.Movement movement, int amount) {
			this.movement = movement;
			if (movement == Movement.UP) {
				this.amount = -amount;
			} else {
				this.amount = amount;
			}
		}
	}
}
