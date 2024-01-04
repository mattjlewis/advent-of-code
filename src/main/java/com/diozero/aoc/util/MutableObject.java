package com.diozero.aoc.util;

import java.io.Serializable;
import java.util.Objects;

public class MutableObject<T> implements Serializable {
	private static final long serialVersionUID = -5544811058936982529L;

	private T value;

	public MutableObject() {
	}

	public MutableObject(T value) {
		this.value = value;
	}

	public boolean isSet() {
		return value != null;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value == null ? "null" : value.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MutableObject<?> other = (MutableObject<?>) obj;
		return Objects.equals(value, other.value);
	}
}
