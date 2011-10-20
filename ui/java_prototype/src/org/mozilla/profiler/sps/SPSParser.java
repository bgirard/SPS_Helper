package org.mozilla.profiler.sps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.mozilla.profiler.sps.gui.MainWindow;

public class SPSParser {
	
	public static void main(String[] args) throws Exception {
		SampleLog log = SPSParser.parse("profile_log_0");
		new MainWindow(log);
	}

	private static SampleLog parse(String fileName) throws Exception {
		File file = new File(fileName);
		Scanner scanner = new Scanner(file);
		
		SampleLog log = new SampleLog();
		// Maker go with the next sample
		List<Marker> pendingMarker = new ArrayList<Marker>();
		Leaf leaf = null;
		Sample lastSample = null;
		while(scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			
			if( nextLine.startsWith("s-") ) { // Parse sample 
				List<String> stack = new ArrayList<String>();
				String[] stackEntry = nextLine.substring(2).split(",");
				for (int i = 0; i < stackEntry.length; i++) {
					stack.add(stackEntry[i]);
				}
				Sample s = new Sample(stack);
				s.addMarkers(pendingMarker);
				pendingMarker = new ArrayList<Marker>();
				log.addSample(s);
				if( leaf != null ) {
					s.addLeaf(leaf);
					leaf = null;
				}
				lastSample = s;
			} else if( nextLine.startsWith("m-") ) { // Parse pending marker
				String marker = nextLine.substring(2);
				pendingMarker.add(new Marker(marker));
			} else if( nextLine.startsWith("l-") ) { // Parse pending marker
				//String leafStr = nextLine.substring(2);
				//leaf = new Leaf(leafStr);
			} else if( nextLine.startsWith("c-") && lastSample != null ) { // Parse pending marker
				lastSample.getStack().add(nextLine.substring(2));
			}
			
		}
		return log;
	}
}
