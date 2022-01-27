package com.diozero.aoc.y2019.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import org.tinylog.Logger;

import com.diozero.aoc.util.TextParser;

public class IntcodeVirtualMachine implements Runnable {
	private enum Opcode {
		ADD, MULTIPLY, INPUT, OUTPUT, JUMP_IF_TRUE, JUMP_IF_FALSE, LESS_THAN, EQUALS, RELATIVE_BASE, HALT;

		public long apply(long param1, long param2) {
			return switch (this) {
			case ADD -> param1 + param2;
			case MULTIPLY -> param1 * param2;
			default -> throw new IllegalArgumentException("Cannot apply opcode " + this);
			};
		}

		public static Opcode valueOf(long value) {
			return switch ((int) (value % 100)) {
			case 1 -> ADD;
			case 2 -> MULTIPLY;
			case 3 -> INPUT;
			case 4 -> OUTPUT;
			case 5 -> JUMP_IF_TRUE;
			case 6 -> JUMP_IF_FALSE;
			case 7 -> LESS_THAN;
			case 8 -> EQUALS;
			case 9 -> RELATIVE_BASE;
			case 99 -> HALT;
			default -> throw new IllegalArgumentException("Invalid Opcode " + value);
			};
		}
	}

	private enum ParameterMode {
		POSITION, IMMEDIATE, RELATIVE;

		public static ParameterMode valueOf(long instr, int parameterIndex) {
			int mode = (int) (instr / ((int) Math.pow(10, parameterIndex + 1)) % 10);
			return switch (mode) {
			case 0 -> POSITION;
			case 1 -> IMMEDIATE;
			case 2 -> RELATIVE;
			default -> throw new IllegalArgumentException(
					"Invalid ParameterMode " + mode + " for instruction " + instr);
			};
		}
	}

	private static final Long ZERO = Long.valueOf(0);
	private static final AtomicInteger INSTANCE = new AtomicInteger();

	public static IntcodeVirtualMachine load(Path input) throws IOException {
		return load(input, null, null);
	}

	public static IntcodeVirtualMachine load(Path inputPath, LongSupplier input, LongConsumer output)
			throws IOException {
		return load(TextParser.loadFirstLineAsCsvLongArray(inputPath), input, output);
	}

	public static IntcodeVirtualMachine load(long[] program, LongSupplier input, LongConsumer output) {
		Map<Long, Long> memory = new HashMap<>();
		for (int i = 0; i < program.length; i++) {
			memory.put(Long.valueOf(i), Long.valueOf(program[i]));
		}

		return new IntcodeVirtualMachine(memory, input, output);
	}

	private int id = INSTANCE.getAndIncrement();
	private final Map<Long, Long> memory;
	private final LongSupplier input;
	private final LongConsumer output;
	private long relativeBase = 0;

	public IntcodeVirtualMachine(Map<Long, Long> memory) {
		this.memory = memory;
		this.input = null;
		this.output = null;
	}

	public IntcodeVirtualMachine(Map<Long, Long> memory, LongSupplier input, LongConsumer output) {
		this.memory = memory;
		this.input = input;
		this.output = output;
	}

	public void store(long address, long value) {
		memory.put(Long.valueOf(address), Long.valueOf(value));
	}

	public long get(long address) {
		return memory.get(Long.valueOf(address)).longValue();
	}

	public long getOrDefaultToZero(long address) {
		return memory.getOrDefault(Long.valueOf(address), ZERO).longValue();
	}

	public long getParameter(ParameterMode mode, long instructionPointer) {
		return switch (mode) {
		case POSITION -> getOrDefaultToZero(getAddress(mode, instructionPointer));
		case IMMEDIATE -> get(instructionPointer);
		case RELATIVE -> getOrDefaultToZero(getAddress(mode, instructionPointer));
		default -> throw new IllegalArgumentException("Invalid ParameterMode " + mode);
		};
	}

	public long getAddress(ParameterMode mode, long instructionPointer) {
		return switch (mode) {
		case POSITION -> get(instructionPointer);
		case RELATIVE -> get(instructionPointer) + relativeBase;
		default -> throw new IllegalArgumentException("Invalid ParameterMode " + mode);
		};
	}

	@Override
	public void run() {
		boolean halted = false;
		long instruction_pointer = 0;

		while (!halted) {
			final long instr = memory.get(Long.valueOf(instruction_pointer++)).longValue();
			final Opcode opcode = Opcode.valueOf(instr);
			Logger.trace("{}: Processing opcode {}, instr {}", id, opcode, instr);
			switch (opcode) {
			case ADD:
			case MULTIPLY:
				long param1 = getParameter(ParameterMode.valueOf(instr, 1), instruction_pointer++);
				long param2 = getParameter(ParameterMode.valueOf(instr, 2), instruction_pointer++);
				long destination_pos = getAddress(ParameterMode.valueOf(instr, 3), instruction_pointer++);
				store(destination_pos, opcode.apply(param1, param2));
				break;
			case INPUT:
				destination_pos = getAddress(ParameterMode.valueOf(instr, 1), instruction_pointer++);
				store(destination_pos, input.getAsLong());
				break;
			case OUTPUT:
				output.accept(getParameter(ParameterMode.valueOf(instr, 1), instruction_pointer++));
				break;
			case JUMP_IF_TRUE:
				if (getParameter(ParameterMode.valueOf(instr, 1), instruction_pointer++) != 0) {
					instruction_pointer = getParameter(ParameterMode.valueOf(instr, 2), instruction_pointer);
				} else {
					instruction_pointer++;
				}
				break;
			case JUMP_IF_FALSE:
				if (getParameter(ParameterMode.valueOf(instr, 1), instruction_pointer++) == 0) {
					instruction_pointer = getParameter(ParameterMode.valueOf(instr, 2), instruction_pointer);
				} else {
					instruction_pointer++;
				}
				break;
			case LESS_THAN:
				param1 = getParameter(ParameterMode.valueOf(instr, 1), instruction_pointer++);
				param2 = getParameter(ParameterMode.valueOf(instr, 2), instruction_pointer++);
				destination_pos = getAddress(ParameterMode.valueOf(instr, 3), instruction_pointer++);
				store(destination_pos, (param1 < param2) ? 1 : 0);
				break;
			case EQUALS:
				param1 = getParameter(ParameterMode.valueOf(instr, 1), instruction_pointer++);
				param2 = getParameter(ParameterMode.valueOf(instr, 2), instruction_pointer++);
				destination_pos = getAddress(ParameterMode.valueOf(instr, 3), instruction_pointer++);
				store(destination_pos, (param1 == param2) ? 1 : 0);
				break;
			case RELATIVE_BASE:
				relativeBase += getParameter(ParameterMode.valueOf(instr, 1), instruction_pointer++);
				break;
			case HALT:
				halted = true;
				break;
			default:
				throw new IllegalArgumentException("Invalid opcode " + opcode);
			}
		}

		Logger.debug("{}: Intcode program terminating", id);
	}
}
