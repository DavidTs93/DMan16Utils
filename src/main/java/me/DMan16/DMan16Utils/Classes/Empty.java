package me.DMan16.DMan16Utils.Classes;

import org.jetbrains.annotations.NotNull;

public final class Empty implements Comparable<Empty> {
	public static final Empty EMPTY = new Empty();
	
	private Empty() {}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == null || (obj instanceof Empty);
	}
	
	public int compareTo(@NotNull Empty other) {
		return 0;
	}
}