package org.mozilla.profiler.sps;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class AtosSymbolicate {

	public static void main(String[] args) throws Exception {
		String log = AtosSymbolicate.symbolicate("profile_mac");
		System.out.println(log);
	}

	public static String symbolicate(String fileName) throws Exception {
		File file = new File(fileName);
		Scanner scanner = new Scanner(file);

		ArrayList<String> symbolicate = new ArrayList<String>();
		HashMap<String, ArrayList<Request>> libRequestMap = new HashMap<String, ArrayList<Request>>();
		while(scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();

			if( !nextLine.startsWith("l-") || nextLine.length() <= 3 ) {
				symbolicate.add(nextLine);
				continue;
			}

			String tagData = nextLine.substring(2);
			String[] tagPart = nextLine.substring(2).split("@");
			if( tagPart.length != 2 || !tagPart[1].startsWith("0x") ) {
				symbolicate.add(nextLine);
				continue;
			}

			String libName = tagPart[0];
			String hexAddress = tagPart[1];
			//System.out.println("Get " + libName + " at " + hexAddress);
			ArrayList<Request> arrayList = libRequestMap.get(libName);
			if( arrayList == null ) {
				libRequestMap.put(libName, new ArrayList<Request>());
				arrayList = libRequestMap.get(libName);
			}
			arrayList.add( new Request(hexAddress, symbolicate.size()) );
			symbolicate.add("<placeholder>");
		}

		Iterator<String> iterator = libRequestMap.keySet().iterator();
		while (iterator.hasNext()) {
			String libName = (String) iterator.next();
			int batch = 0;
			boolean isDone = false;
			while(isDone==false) {

				String[] split = libName.split("/");
				System.out.print(split[split.length-1] + ", ");
				ArrayList<Request> arrayList = libRequestMap.get(libName);
				StringBuilder listOfAddresses = new StringBuilder();
				for (int i = batch * 10000; i < arrayList.size() && i < batch * 10000 + 10000; i++) {
					Request request = arrayList.get(i);
					listOfAddresses.append(" ");
					listOfAddresses.append(request.hexAddress);
					if( i == arrayList.size() - 1 ) {
						isDone = true;
					}
				}
				batch++;
				//System.out.println("atos -l 0x0 -o " + libName + " " + listOfAddresses.toString());
				Process exec = Runtime.getRuntime().exec("atos -l 0x0 -o " + libName + " " + listOfAddresses.toString());
				Scanner result = new Scanner(exec.getInputStream());
				BufferedReader stdInput = new BufferedReader(new 
						InputStreamReader(exec.getInputStream()));
	
				BufferedReader stdError = new BufferedReader(new 
						InputStreamReader(exec.getErrorStream()));
				String s;
				// read the output from the command
				int requestId = 0;
				while ((s = stdInput.readLine()) != null) {
					symbolicate.set(arrayList.get(requestId++).fileLine, "l-" + s);
				}
				int waitFor = exec.waitFor();
			}
		}
		System.out.println("");
		
		StringBuilder result = new StringBuilder();
		for (String string : symbolicate) {
			result.append(string);
			result.append("\n");
		}
		return result.toString();
	}
}

class Request {
	public String hexAddress;
	public int fileLine;
	public Request(String hexAddress, int fileLine) {
		this.hexAddress = hexAddress;
		this.fileLine = fileLine;
	}
}
