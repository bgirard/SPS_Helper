package org.mozilla.profiler.sps;

import java.util.ArrayList;
import java.util.List;

public class Sample {
	private List<String> stack = new ArrayList<String>();
	private List<Marker> markers = new ArrayList<Marker>();
	private Leaf leaf = null;
	
	public Sample(List<String> stack) {
		this.stack.addAll(stack);
	}
	
	public void addMarkers(List<Marker> stack) {
		this.markers.addAll(stack);
	}

	public List<Marker> getMarkers() {
		return markers;
	}
	
	public List<String> getStack() {
		return stack;
	}

	public void addLeaf(Leaf leaf) {
		this.leaf = leaf;
		stack.add("Leaf data");
		stack.add(leaf.getData());
	}
	
	public Leaf getLeaf() {
		return leaf;
	}
}
