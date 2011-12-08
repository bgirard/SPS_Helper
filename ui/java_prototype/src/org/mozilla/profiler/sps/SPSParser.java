package org.mozilla.profiler.sps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.mozilla.profiler.sps.gui.MainWindow;

public class SPSParser {
	
	public static void main(String[] args) throws Exception {
		System.out.println("Symbolicating...");
		String symbolicate = AtosSymbolicate.symbolicate("profile_mac");
		System.out.println("Parsing...");
		SampleLog log = SPSParser.parse(new Scanner(symbolicate));
		System.out.println("Render...");
		new MainWindow(log);
	}

	private static SampleLog parse(Scanner scanner) throws Exception {
		
		SampleLog log = new SampleLog();
		// Maker go with the next sample
		List<Marker> pendingMarker = new ArrayList<Marker>();
		Leaf leaf = null;
		Sample lastSample = null;
		int r = 0;
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
				s.addResponsiveness(r);
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
			} else if( nextLine.startsWith("r-") ) { // Parse pending marker
				r = Integer.parseInt(nextLine.substring(2));
			} else if( nextLine.startsWith("l-") && lastSample != null && !nextLine.startsWith("l-0x") ) { // Parse pending marker
				lastSample.getStack().add(nextLine.substring(2));
				//String leafStr = nextLine.substring(2);
				//leaf = new Leaf(leafStr);
			} else if( nextLine.startsWith("c-") && lastSample != null ) { // Parse pending marker
				lastSample.getStack().add(nextLine.substring(2));
			}
			
		}
		return log;
	}
}
