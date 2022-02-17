package com.diozero.aoc.y2019.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
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

	public static IntcodeVirtualMachine load(Path input) throws IOException {
		return load(input, null, null);
	}

	public static IntcodeVirtualMachine load(Path inputPath, LongSupplier input, LongConsumer output)
			throws IOException {
		return load(TextParser.loadFirstLineAsCsvLongArray(inputPath), input, output);
	}

	public static IntcodeVirtualMachine load(long[] program, LongSupplier input, LongConsumer output) {
		return new IntcodeVirtualMachine(program, input, output);
	}

	private final long[] program;
	private final Map<Long, Long> memory;
	private long instructionPointer;
	private long relativeBase;
	private final LongSupplier input;
	private final LongConsumer output;
	private final AtomicBoolean running;
	private final AtomicBoolean waitingForInput;
	private CountDownLatch terminationMonitor;
	private Semaphore runningSemaphore;

	public IntcodeVirtualMachine(long[] program) {
		this(program, null, null);
	}

	public IntcodeVirtualMachine(long[] program, LongSupplier input, LongConsumer output) {
		this.program = program;
		memory = new HashMap<>();
		for (int i = 0; i < program.length; i++) {
			memory.put(Long.valueOf(i), Long.valueOf(program[i]));
		}
		instructionPointer = 0;
		relativeBase = 0;
		this.input = input;
		this.output = output;
		running = new AtomicBoolean(false);
		waitingForInput = new AtomicBoolean(false);
		runningSemaphore = new Semaphore(1);
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

	private long getParameter(long instr, int offset) {
		long memory_location = instructionPointer + offset;

		ParameterMode mode = ParameterMode.valueOf(instr, offset);
		if (mode == ParameterMode.IMMEDIATE) {
			return get(memory_location);
		}

		return getOrDefaultToZero(getAddress(mode, memory_location));
	}

	private long getAddress(long instr, int offset) {
		return getAddress(ParameterMode.valueOf(instr, offset), instructionPointer + offset);
	}

	private long getAddress(ParameterMode mode, long memoryLocation) {
		return switch (mode) {
		case POSITION -> get(memoryLocation);
		case RELATIVE -> get(memoryLocation) + relativeBase;
		default -> throw new IllegalArgumentException("Invalid address ParameterMode " + mode);
		};
	}

	@Override
	public void run() {
		running.set(true);
		terminationMonitor = new CountDownLatch(1);
		runningSemaphore.acquireUninterruptibly();

		while (running.get()) {
			processInstruction();
		}

		runningSemaphore.release();
		terminationMonitor.countDown();
		running.set(false);

		Logger.debug("Intcode program terminating - interrupted? {}",
				Boolean.valueOf(Thread.currentThread().isInterrupted()));
	}

	public Opcode peekNextOpcode() {
		return Opcode.valueOf(memory.get(Long.valueOf(instructionPointer)).longValue());
	}

	public void processInstruction() {
		final long instr = memory.get(Long.valueOf(instructionPointer)).longValue();

		final Opcode opcode = Opcode.valueOf(instr);
		Logger.trace("Processing opcode {}, instr {}", opcode, Long.valueOf(instr));

		switch (opcode) {
		case ADD:
		case MULTIPLY:
			long param1 = getParameter(instr, 1);
			long param2 = getParameter(instr, 2);
			long destination_pos = getAddress(instr, 3);
			store(destination_pos, opcode.apply(param1, param2));
			instructionPointer += 4;
			break;
		case INPUT:
			destination_pos = getAddress(instr, 1);
			waitingForInput.set(true);
			long value = input.getAsLong();
			waitingForInput.set(false);
			if (Thread.currentThread().isInterrupted()) {
				running.set(false);
				Logger.debug("Interrupted while waiting for input");
				break;
			}
			store(destination_pos, value);
			instructionPointer += 2;
			break;
		case OUTPUT:
			output.accept(getParameter(instr, 1));
			instructionPointer += 2;
			break;
		case JUMP_IF_TRUE:
			if (getParameter(instr, 1) != 0) {
				instructionPointer = getParameter(instr, 2);
			} else {
				instructionPointer += 3;
			}
			break;
		case JUMP_IF_FALSE:
			if (getParameter(instr, 1) == 0) {
				instructionPointer = getParameter(instr, 2);
			} else {
				instructionPointer += 3;
			}
			break;
		case LESS_THAN:
			param1 = getParameter(instr, 1);
			param2 = getParameter(instr, 2);
			destination_pos = getAddress(instr, 3);
			store(destination_pos, (param1 < param2) ? 1 : 0);
			instructionPointer += 4;
			break;
		case EQUALS:
			param1 = getParameter(instr, 1);
			param2 = getParameter(instr, 2);
			destination_pos = getAddress(instr, 3);
			store(destination_pos, (param1 == param2) ? 1 : 0);
			instructionPointer += 4;
			break;
		case RELATIVE_BASE:
			relativeBase += getParameter(instr, 1);
			instructionPointer += 2;
			break;
		case HALT:
			running.set(false);
			break;
		default:
			throw new IllegalArgumentException("Invalid opcode " + opcode);
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	public void reset() {
		if (running.get()) {
			throw new IllegalStateException("Cannot reset a running VM");
		}

		instructionPointer = 0;
		relativeBase = 0;
		memory.clear();
		for (int i = 0; i < program.length; i++) {
			memory.put(Long.valueOf(i), Long.valueOf(program[i]));
		}
	}

	public State backup() throws InterruptedException {
		// Note should really only backup when the VM is halted or waiting for input...
		if (running.get() && !waitingForInput.get()) {
			Logger.info("Backing up a running VM that isn't waiting on input - will wait for semaphore to be released");
		}

		// Make sure the program isn't running, wait for it to complete if it is
		runningSemaphore.acquire();

		State state = new State(instructionPointer, relativeBase, new HashMap<>(memory));

		runningSemaphore.release();

		return state;
	}

	public void restore(State state) {
		// Can only restore if not running
		if (running.get()) {
			throw new IllegalStateException("Cannot restore a running VM");
		}

		memory.clear();
		memory.putAll(state.backup);
		instructionPointer = state.instructionPointer;
		relativeBase = state.relativeBase;
	}

	public void awaitTermination() throws InterruptedException {
		if (!running.get()) {
			return;
		}

		// Shouldn't be possible for this to be null...
		terminationMonitor.await();
	}

	public void halt() throws InterruptedException {
		if (!running.get()) {
			return;
		}

		running.set(false);

		terminationMonitor.await();
	}

	public static record State(long instructionPointer, long relativeBase, Map<Long, Long> backup) {
		//
	}
}
