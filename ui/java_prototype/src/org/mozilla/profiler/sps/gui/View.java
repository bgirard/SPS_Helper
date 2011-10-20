package org.mozilla.profiler.sps.gui;

import javax.swing.JInternalFrame;

import org.mozilla.profiler.sps.SampleLog;

public class View extends JInternalFrame {
	protected final SampleLog log;

	public View(SampleLog log) {
		this.log = log;
	}
	
	public SampleLog getSampleLog() {
		return log;
	}
}
