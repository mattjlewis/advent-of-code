package com.diozero.aoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Credit: https://www.baeldung.com/java-circular-linked-list
 * https://github.com/eugenp/tutorials/blob/master/data-structures/src/main/java/com/baeldung/circularlinkedlist/CircularLinkedList.java
 */
public class CircularLinkedList<E> {
	// Head is always tail.next
	private Node<E> tail;

	public CircularLinkedList() {
	}

	public CircularLinkedList(List<E> values) {
		values.forEach(this::add);
	}

	public Node<E> head() {
		if (tail == null) {
			throw new NoSuchElementException();
		}

		return tail.next;
	}

	public E headValue() {
		if (tail == null) {
			throw new NoSuchElementException();
		}

		return tail.next.value;
	}

	public Node<E> tail() {
		if (tail == null) {
			throw new NoSuchElementException();
		}

		return tail;
	}

	public E tailValue() {
		if (tail == null) {
			throw new NoSuchElementException();
		}

		return tail.value;
	}

	public E nextValue(E value) {
		if (tail == null) {
			throw new NoSuchElementException();
		}

		// Start at head
		Node<E> current_node = tail.next;
		while (!current_node.value.equals(value)) {
			current_node = current_node.next;
			if (current_node == tail.next) {
				throw new NoSuchElementException();
			}
		}

		return current_node.next.value;
	}

	public boolean contains(E e) {
		if (tail == null) {
			return false;
		}

		// Start at the head
		Node<E> current_node = tail.next;
		do {
			if (current_node.value.equals(e)) {
				return true;
			}
			current_node = current_node.next;
		} while (current_node != tail.next);

		return false;
	}

	public Node<E> get(E e) {
		// Start at the head
		Node<E> current_node = tail.next;
		do {
			if (current_node.value.equals(e)) {
				return current_node;
			}
			current_node = current_node.next;
		} while (current_node != tail.next);

		return null;
	}

	/**
	 * Appends a new item to the end of this list
	 *
	 * @param e element to be appended to this list
	 */
	public Node<E> add(E e) {
		Node<E> new_node = new Node<>(e);

		if (tail == null) {
			tail = new_node;
			tail.next = tail;

			return new_node;
		}

		// Get the current head
		Node<E> head = tail.next;
		// Set the current tail's next node to be the new node
		tail.next = new_node;
		// Update the tail node
		tail = new_node;
		// Make the circle complete
		tail.next = head;

		return new_node;
	}

	public void insertAfter(E e, List<E> values) {
		if (tail == null) {
			throw new NoSuchElementException();
		}

		// Locate e starting at the head
		Node<E> current_node = tail.next;
		while (!current_node.value.equals(e)) {
			current_node = current_node.next;
			if (current_node == tail.next) {
				throw new NoSuchElementException();
			}
		}

		Node<E> next_node = current_node.next;
		// Insert the values
		for (E value : values) {
			current_node.next = new Node<>(value);
			current_node = current_node.next;

			// FIXME May need to adjust head / tail
		}
		current_node.next = next_node;
	}

	public boolean delete(E e) {
		if (tail == null) {
			return false;
		}

		boolean removed = false;
		// Start at head
		Node<E> current_node = tail.next;
		do {
			// Get the next node
			Node<E> next_node = current_node.next;
			if (next_node.value.equals(e)) {
				// Is the list now empty?
				if (tail == tail.next) {
					tail = null;
				} else {
					// Remove next_node by replacing the reference to it from the previous node
					current_node.next = next_node.next;
					// Was next_node the tail?
					if (tail == next_node) {
						tail = current_node;
					}
				}
				removed = true;
				break;
			}
			current_node = next_node;
		} while (current_node != tail.next);

		return removed;
	}

	public List<E> deleteAfter(E e, int count) {
		if (tail == null) {
			throw new NoSuchElementException();
		}

		// Locate e starting at head
		Node<E> start_node = tail.next;
		while (!start_node.value.equals(e)) {
			start_node = start_node.next;
			if (start_node == tail.next) {
				throw new NoSuchElementException();
			}
		}

		List<E> deleted = new ArrayList<>();

		Node<E> current_node = start_node;
		for (int i = 0; i < count; i++) {
			// Is the list empty
			if (tail == tail.next) {
				tail = null;
				break;
			}

			Node<E> next_node = current_node.next;
			deleted.add(next_node.value);
			// Remove next_node by replacing the reference to it from the previous node
			current_node.next = next_node.next;
			// Was next_node the tail?
			if (tail == next_node) {
				tail = current_node;
			}
		}

		return deleted;
	}

	public void traverse(Consumer<Node<E>> consumer) {
		if (tail == null) {
			return;
		}

		// Start at head
		Node<E> current_node = tail.next;
		do {
			consumer.accept(current_node);
			current_node = current_node.next;
		} while (current_node != tail.next);
	}

	public List<E> toList() {
		final List<E> list = new ArrayList<>();
		traverse(node -> list.add(node.value()));
		return list;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		traverse(buffer::append);
		return buffer.toString();
	}

	public static class Node<E> {
		private final E value;
		private Node<E> next;

		public Node(E value) {
			this.value = value;
		}

		public E value() {
			return value;
		}

		public Node<E> next() {
			return next;
		}

		public void setNext(Node<E> next) {
			this.next = next;
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}
}
