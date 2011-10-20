package org.mozilla.profiler.sps;

import java.util.ArrayList;
import java.util.List;

public class SampleLog {
	public List<Sample> samples = new ArrayList<Sample>();
	
	public SampleLog() {
		
	}
	
	public void addSample(Sample s) {
		samples.add(s);
	}
	
}
