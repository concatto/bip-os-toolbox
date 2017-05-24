package br.univali.bipost;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Simulation {
	private List<Integer> indices;

	public Simulation(String filePath) throws IOException {
		indices = new ArrayList<>();
		
		Pattern p = Pattern.compile("\\|PC=0x(.+)\\|ACC=0x");
		
		BufferedReader r = new BufferedReader(new FileReader(filePath));
		String line;
		
		while ((line = r.readLine()) != null) {
			line = line.trim();
			
			Matcher m = p.matcher(line);
			if (m.find()) {
				String s = m.group(1);
				indices.add(Integer.parseInt(s, 16));
			}
		}
		
		r.close();
	}
	
	public List<Integer> getIndices() {
		return indices;
	}
}
