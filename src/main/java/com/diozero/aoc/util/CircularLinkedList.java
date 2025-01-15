package com.diozero.aoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Credit: https://www.baeldung.com/java-circular-linked-list
 * https://github.com/eugenp/tutorials/blob/master/data-structures/src/main/java/com/baeldung/circularlinkedlist/CircularLinkedList.java
 */
// TODO Optimise get by storing a map from key to node
// public class CircularLinkedList<K, V> {
public class CircularLinkedList<E> {
	// Head is always tail.next
	private Node<E> tail;

	public CircularLinkedList() {
	}

	public CircularLinkedList(List<E> values) {
		values.forEach(this::add);
	}

	public int size() {
		if (tail == null) {
			return 0;
		}

		int size = 0;

		// Start at head
		Node<E> current_node = tail.next;
		do {
			current_node = current_node.next;
			size++;
		} while (current_node != tail.next);

		return size;
	}

	public E poll() {
		final E value = tail.next.value;
		tail = tail.next;

		return value;
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
		return get(e) != null;
	}

	public Node<E> get(E e) {
		if (tail == null) {
			return null;
		}

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
		Node<E> e_node = get(e);

		if (e_node == null) {
			throw new NoSuchElementException();
		}

		Node<E> next_node = e_node.next;
		// Insert the values
		for (E value : values) {
			e_node.next = new Node<>(value);
			e_node = e_node.next;

			// FIXME Do we need to adjust the tail?
		}
		e_node.next = next_node;
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
		Node<E> e_node = get(e);

		if (e_node == null) {
			throw new NoSuchElementException();
		}

		List<E> deleted = new ArrayList<>();

		Node<E> current_node = e_node;
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

	public static class NodeIterator<T> {
		private Node<T> node;

		public NodeIterator(Node<T> node) {
			this.node = node;
		}

		public T getValueAndIncrement() {
			T value = node.value;

			node = node.next;

			return value;
		}
	}
}
