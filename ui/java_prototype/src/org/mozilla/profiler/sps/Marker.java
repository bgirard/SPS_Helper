package org.mozilla.profiler.sps;

public class Marker {
	private final String name;

	public Marker(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
