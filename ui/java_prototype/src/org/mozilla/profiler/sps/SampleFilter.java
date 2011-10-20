package org.mozilla.profiler.sps;

import java.util.List;

public class SampleFilter {
	public static SampleLog filter(SampleLog log, Object selectStart, Object selectEnd) {
		boolean discard = false;
		if( selectStart != null ) {
			discard = true;
		}
		
		SampleLog filteredLog = new SampleLog();
		List<Sample> samples = log.samples;
		for (Sample sample : samples) {
			if( isSelectMatch(sample, selectStart) ) {
				discard = false;
				selectStart = null;
			}
			if( isSelectMatch(sample, selectEnd) ) {
				break; // Selection end, terminate
			}
			if( discard ) continue;
			
			filteredLog.samples.add(sample);
		}
		
		return filteredLog;
	}

	private static boolean isSelectMatch(Sample sample, Object select) {
		boolean isSelect = false;
		isSelect |= (sample == select);
		List<Marker> markers = sample.getMarkers();
		for (Marker marker : markers) {
			isSelect |= (marker == select);
		}
		return isSelect;
	}
}
